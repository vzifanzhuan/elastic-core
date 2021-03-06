/******************************************************************************
 * Copyright © 2013-2016 The XEL Core Developers.                             *
 *                                                                            *
 * See the AUTHORS.txt, DEVELOPER-AGREEMENT.txt and LICENSE.txt files at      *
 * the top-level directory of this distribution for the individual copyright  *
 * holder information and the developer policies on copyright and licensing.  *
 *                                                                            *
 * Unless otherwise agreed in a custom licensing agreement, no part of the    *
 * XEL software, including this file, may be copied, modified, propagated,    *
 * or distributed except according to the terms contained in the LICENSE.txt  *
 * file.                                                                      *
 *                                                                            *
 * Removal or modification of this copyright notice is prohibited.            *
 *                                                                            *
 ******************************************************************************/

package nxt.http;

import java.io.IOException;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.*;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import nxt.Constants;
import nxt.Nxt;
import nxt.util.Convert;
import nxt.util.Logger;
import nxt.util.ThreadPool;
import nxt.util.UPnP;

public final class API {

	private static class NetworkAddress {

		private BigInteger netAddress;
		private BigInteger netMask;

		private NetworkAddress(final String address) throws UnknownHostException {
			final String[] addressParts = address.split("/");
			if (addressParts.length == 2) {
				final InetAddress targetHostAddress = InetAddress.getByName(addressParts[0]);
				final byte[] srcBytes = targetHostAddress.getAddress();
				this.netAddress = new BigInteger(1, srcBytes);
				final int maskBitLength = Integer.valueOf(addressParts[1]);
				final int addressBitLength = (targetHostAddress instanceof Inet4Address) ? 32 : 128;
				this.netMask = BigInteger.ZERO.setBit(addressBitLength).subtract(BigInteger.ONE)
						.subtract(BigInteger.ZERO.setBit(addressBitLength - maskBitLength).subtract(BigInteger.ONE));
			} else throw new IllegalArgumentException("Invalid address: " + address);
		}

		private boolean contains(final BigInteger hostAddressToCheck) {
			return Objects.equals(hostAddressToCheck.and(this.netMask), this.netAddress);
		}

	}

	private static class PasswordCount {
		private int count;
		private int time;
	}

	public static final class XFrameOptionsFilter implements Filter {

		@Override
		public void destroy() {
		}

		@Override
		public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
				throws IOException, ServletException {
			((HttpServletResponse) response).setHeader("X-FRAME-OPTIONS", "SAMEORIGIN");
			chain.doFilter(request, response);
		}

		@Override
		public void init(final FilterConfig filterConfig) throws ServletException {
		}

	}

	private static final int TESTNET_API_PORT = 6876;

	private static final int TESTNET_API_SSLPORT = 6877;
	public static final int openAPIPort;

	public static final int openAPISSLPort;
	public static final List<String> disabledAPIs;
	public static final List<APITag> disabledAPITags;
	private static final Set<String> allowedBotHosts;
	private static final List<NetworkAddress> allowedBotNets;
	private static final Map<String, PasswordCount> incorrectPasswords = new HashMap<>();
	private static final String adminPassword = Nxt.getStringProperty("nxt.adminPassword", "", true);
	static final boolean disableAdminPassword;
	static final int maxRecords = Nxt.getIntProperty("nxt.maxAPIRecords");

	private static final boolean enableAPIUPnP = Nxt.getBooleanProperty("nxt.enableAPIUPnP");
	public static final int apiServerIdleTimeout = Nxt.getIntProperty("nxt.apiServerIdleTimeout");
	public static final boolean apiServerCORS = Nxt.getBooleanProperty("nxt.apiServerCORS");

	private static final Server apiServer;

	private static URI welcomePageUri;

	private static URI serverRootUri;

	static {
		List<String> disabled = new ArrayList<>(Nxt.getStringListProperty("nxt.disabledAPIs"));
		Collections.sort(disabled);
		disabledAPIs = Collections.unmodifiableList(disabled);
		disabled = Nxt.getStringListProperty("nxt.disabledAPITags");
		Collections.sort(disabled);
		final List<APITag> apiTags = new ArrayList<>(disabled.size());
		disabled.forEach(tagName -> apiTags.add(APITag.fromDisplayName(tagName)));
		disabledAPITags = Collections.unmodifiableList(apiTags);
		final List<String> allowedBotHostsList = Nxt.getStringListProperty("nxt.allowedBotHosts");
		if (!allowedBotHostsList.contains("*")) {
			final Set<String> hosts = new HashSet<>();
			final List<NetworkAddress> nets = new ArrayList<>();
			for (final String host : allowedBotHostsList)
				if (host.contains("/")) try {
					nets.add(new NetworkAddress(host));
				} catch (final UnknownHostException e) {
					Logger.logErrorMessage("Unknown network " + host, e);
					throw new RuntimeException(e.toString(), e);
				}
				else hosts.add(host);
			allowedBotHosts = Collections.unmodifiableSet(hosts);
			allowedBotNets = Collections.unmodifiableList(nets);
		} else {
			allowedBotHosts = null;
			allowedBotNets = null;
		}

		final boolean enableAPIServer = Nxt.getBooleanProperty("nxt.enableAPIServer");
		if (enableAPIServer) {
			final int port = Constants.isTestnet ? API.TESTNET_API_PORT : Nxt.getIntProperty("nxt.apiServerPort");
			final int sslPort = Constants.isTestnet ? API.TESTNET_API_SSLPORT
					: Nxt.getIntProperty("nxt.apiServerSSLPort");
			final String host = Nxt.getStringProperty("nxt.apiServerHost");
			disableAdminPassword = Nxt.getBooleanProperty("nxt.disableAdminPassword")
					|| (Objects.equals("127.0.0.1", host) && API.adminPassword.isEmpty());

			apiServer = new Server();
			ServerConnector connector;
			final boolean enableSSL = Nxt.getBooleanProperty("nxt.apiSSL");
			//
			// Create the HTTP connector
			//
			if (!enableSSL || (port != sslPort)) {
				final HttpConfiguration configuration = new HttpConfiguration();
				configuration.setSendDateHeader(false);
				configuration.setSendServerVersion(false);

				connector = new ServerConnector(API.apiServer, new HttpConnectionFactory(configuration));
				connector.setPort(port);
				connector.setHost(host);
				connector.setIdleTimeout(API.apiServerIdleTimeout);
				connector.setReuseAddress(true);
				API.apiServer.addConnector(connector);
				Logger.logMessage("API server using HTTP port " + port);
			}
			//
			// Create the HTTPS connector
			//
			final SslContextFactory sslContextFactory;
			if (enableSSL) {
				final HttpConfiguration https_config = new HttpConfiguration();
				https_config.setSendDateHeader(false);
				https_config.setSendServerVersion(false);
				https_config.setSecureScheme("https");
				https_config.setSecurePort(sslPort);
				https_config.addCustomizer(new SecureRequestCustomizer());
				sslContextFactory = new SslContextFactory();
				final String keyStorePath = Paths.get(Nxt.getUserHomeDir())
						.resolve(Paths.get(Nxt.getStringProperty("nxt.keyStorePath"))).toString();
				Logger.logInfoMessage("Using keystore: " + keyStorePath);
				sslContextFactory.setKeyStorePath(keyStorePath);
				sslContextFactory.setKeyStorePassword(Nxt.getStringProperty("nxt.keyStorePassword", null, true));
				sslContextFactory.addExcludeCipherSuites("SSL_RSA_WITH_DES_CBC_SHA", "SSL_DHE_RSA_WITH_DES_CBC_SHA",
						"SSL_DHE_DSS_WITH_DES_CBC_SHA", "SSL_RSA_EXPORT_WITH_RC4_40_MD5",
						"SSL_RSA_EXPORT_WITH_DES40_CBC_SHA", "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA",
						"SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA");
				sslContextFactory.addExcludeProtocols("SSLv3");
				final List<String> ciphers = Nxt.getStringListProperty("nxt.apiSSLCiphers");
				if (!ciphers.isEmpty())
					sslContextFactory.setIncludeCipherSuites(ciphers.toArray(new String[ciphers.size()]));
				connector = new ServerConnector(API.apiServer, new SslConnectionFactory(sslContextFactory, "http/1.1"),
						new HttpConnectionFactory(https_config));
				connector.setPort(sslPort);
				connector.setHost(host);
				connector.setIdleTimeout(API.apiServerIdleTimeout);
				connector.setReuseAddress(true);
				API.apiServer.addConnector(connector);
				Logger.logMessage("API server using HTTPS port " + sslPort);
			} else sslContextFactory = null;
			final String localhost = "0.0.0.0".equals(host) || "127.0.0.1".equals(host) ? "localhost" : host;
			try {
				API.welcomePageUri = new URI(enableSSL ? "https" : "http", null, localhost, enableSSL ? sslPort : port,
						"/index.html", null, null);
				API.serverRootUri = new URI(enableSSL ? "https" : "http", null, localhost, enableSSL ? sslPort : port,
						"", null, null);
			} catch (final URISyntaxException e) {
				Logger.logInfoMessage("Cannot resolve browser URI", e);
			}
			openAPIPort = !Constants.isLightClient && Objects.equals("0.0.0.0", host) && (API.allowedBotHosts == null)
					&& (!enableSSL || (port != sslPort)) ? port : 0;
			openAPISSLPort = !Constants.isLightClient && Objects.equals("0.0.0.0", host) && (API.allowedBotHosts == null)
					&& enableSSL ? sslPort : 0;

			final HandlerList apiHandlers = new HandlerList();

			final ServletContextHandler apiHandler = new ServletContextHandler();
			final String apiResourceBase = Nxt.getStringProperty("nxt.apiResourceBase");
			if (apiResourceBase != null) {
				final ServletHolder defaultServletHolder = new ServletHolder(new DefaultServlet());
				defaultServletHolder.setInitParameter("dirAllowed", "false");
				defaultServletHolder.setInitParameter("resourceBase", apiResourceBase);
				defaultServletHolder.setInitParameter("welcomeServlets", "true");
				defaultServletHolder.setInitParameter("redirectWelcome", "true");
				defaultServletHolder.setInitParameter("gzip", "true");
				defaultServletHolder.setInitParameter("etags", "true");
				apiHandler.addServlet(defaultServletHolder, "/*");
				apiHandler.setWelcomeFiles(new String[] { Nxt.getStringProperty("nxt.apiWelcomeFile") });
			}

			final String javadocResourceBase = Nxt.getStringProperty("nxt.javadocResourceBase");
			if (javadocResourceBase != null) {
				final ContextHandler contextHandler = new ContextHandler("/doc");
				final ResourceHandler docFileHandler = new ResourceHandler();
				docFileHandler.setDirectoriesListed(false);
				docFileHandler.setWelcomeFiles(new String[] { "index.html" });
				docFileHandler.setResourceBase(javadocResourceBase);
				contextHandler.setHandler(docFileHandler);
				apiHandlers.addHandler(contextHandler);
			}

			ServletHolder servletHolder = apiHandler.addServlet(APIServlet.class, "/nxt");
			servletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement(null,
					Math.max(Nxt.getIntProperty("nxt.maxUploadFileSize"), Constants.MAX_TAGGED_DATA_DATA_LENGTH), -1L,
					0));

			servletHolder = apiHandler.addServlet(APIProxyServlet.class, "/nxt-proxy");
			servletHolder.setInitParameters(Collections.singletonMap("idleTimeout",
					"" + Math.max(API.apiServerIdleTimeout - APIProxyServlet.PROXY_IDLE_TIMEOUT_DELTA, 0)));
			servletHolder.getRegistration().setMultipartConfig(new MultipartConfigElement(null,
					Math.max(Nxt.getIntProperty("nxt.maxUploadFileSize"), Constants.MAX_TAGGED_DATA_DATA_LENGTH), -1L,
					0));

			final GzipHandler gzipHandler = new GzipHandler();
			if (!Nxt.getBooleanProperty("nxt.enableAPIServerGZIPFilter"))
				gzipHandler.setExcludedPaths("/nxt", "/nxt-proxy");
			gzipHandler.setIncludedMethods("GET", "POST");
			gzipHandler.setMinGzipSize(nxt.peer.Peers.MIN_COMPRESS_SIZE);
			apiHandler.setGzipHandler(gzipHandler);

			apiHandler.addServlet(APITestServlet.class, "/test");
			apiHandler.addServlet(APITestServlet.class, "/test-proxy");

			if (API.apiServerCORS) {
				final FilterHolder filterHolder = apiHandler.addFilter(CrossOriginFilter.class, "/*", null);
				filterHolder.setInitParameter("allowedHeaders", "*");
				filterHolder.setAsyncSupported(true);
			}

			if (Nxt.getBooleanProperty("nxt.apiFrameOptionsSameOrigin")) {
				final FilterHolder filterHolder = apiHandler.addFilter(XFrameOptionsFilter.class, "/*", null);
				filterHolder.setAsyncSupported(true);
			}

			apiHandlers.addHandler(apiHandler);
			apiHandlers.addHandler(new DefaultHandler());

			API.apiServer.setHandler(apiHandlers);
			API.apiServer.setStopAtShutdown(true);

			ThreadPool.runBeforeStart(() -> {
				try {
					if (API.enableAPIUPnP) {
						final Connector[] apiConnectors = API.apiServer.getConnectors();
						for (final Connector apiConnector : apiConnectors)
							if (apiConnector instanceof ServerConnector)
								UPnP.addPort(((ServerConnector) apiConnector).getPort());
					}
					APIServlet.initClass();
					APIProxyServlet.initClass();
					APITestServlet.initClass();
					API.apiServer.start();
					if (sslContextFactory != null) {
						Logger.logDebugMessage(
								"API SSL Protocols: " + Arrays.toString(sslContextFactory.getSelectedProtocols()));
						Logger.logDebugMessage(
								"API SSL Ciphers: " + Arrays.toString(sslContextFactory.getSelectedCipherSuites()));
					}
					Logger.logMessage("Started API server at " + host + ":" + port
							+ (enableSSL && (port != sslPort) ? ", " + host + ":" + sslPort : ""));
				} catch (final Exception e) {
					Logger.logErrorMessage("Failed to start API server", e);
					throw new RuntimeException(e.toString(), e);
				}

			}, true);

		} else {
			apiServer = null;
			disableAdminPassword = false;
			openAPIPort = 0;
			openAPISSLPort = 0;
			Logger.logMessage("API server not enabled");
		}

	}

	private static void checkOrLockPassword(final HttpServletRequest req) throws ParameterException {
		final int now = Nxt.getEpochTime();
		final String remoteHost = req.getRemoteHost();
		synchronized (API.incorrectPasswords) {
			PasswordCount passwordCount = API.incorrectPasswords.get(remoteHost);
			if ((passwordCount != null) && (passwordCount.count >= 3) && ((now - passwordCount.time) < (60 * 60))) {
				Logger.logWarningMessage("Too many incorrect admin password attempts from " + remoteHost);
				throw new ParameterException(JSONResponses.LOCKED_ADMIN_PASSWORD);
			}
			if (!Objects.equals(API.adminPassword, req.getParameter("adminPassword"))) {
				if (passwordCount == null) {
					passwordCount = new PasswordCount();
					API.incorrectPasswords.put(remoteHost, passwordCount);
				}
				passwordCount.count++;
				passwordCount.time = now;
				Logger.logWarningMessage("Incorrect adminPassword from " + remoteHost);
				throw new ParameterException(JSONResponses.INCORRECT_ADMIN_PASSWORD);
			}
			if (passwordCount != null) API.incorrectPasswords.remove(remoteHost);
		}
	}

	public static boolean checkPassword(final HttpServletRequest req) {
		if (API.disableAdminPassword) return true;
		if (API.adminPassword.isEmpty()) return false;
		if (Convert.emptyToNull(req.getParameter("adminPassword")) == null) return false;
		try {
			API.checkOrLockPassword(req);
			return true;
		} catch (final ParameterException e) {
			return false;
		}
	}

	public static URI getServerRootUri() {
		return API.serverRootUri;
	}

	public static URI getWelcomePageUri() {
		return API.welcomePageUri;
	}

	public static void init() {
	}

	static boolean isAllowed(final String remoteHost) {
		if ((API.allowedBotHosts == null) || API.allowedBotHosts.contains(remoteHost)) return true;
		try {
			final BigInteger hostAddressToCheck = new BigInteger(InetAddress.getByName(remoteHost).getAddress());
			for (final NetworkAddress network : API.allowedBotNets)
				if (network.contains(hostAddressToCheck)) return true;
		} catch (final UnknownHostException e) {
			// can't resolve, disallow
			Logger.logMessage("Unknown remote host " + remoteHost);
		}
		return false;

	}

	public static void shutdown() {
		if (API.apiServer != null) try {
			API.apiServer.stop();
			if (API.enableAPIUPnP) {
				final Connector[] apiConnectors = API.apiServer.getConnectors();
				for (final Connector apiConnector : apiConnectors)
					if (apiConnector instanceof ServerConnector)
						UPnP.deletePort(((ServerConnector) apiConnector).getPort());
			}
		} catch (final Exception e) {
			Logger.logShutdownMessage("Failed to stop API server", e);
		}
	}

	public static void verifyPassword(final HttpServletRequest req) throws ParameterException {
		if (API.disableAdminPassword) return;
		if (API.adminPassword.isEmpty()) throw new ParameterException(JSONResponses.NO_PASSWORD_IN_CONFIG);
		API.checkOrLockPassword(req);
	}

	private API() {
	} // never

}
