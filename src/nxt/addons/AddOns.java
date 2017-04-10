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

package nxt.addons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import nxt.Nxt;
import nxt.http.APIServlet;
import nxt.http.APITag;
import nxt.util.Logger;

public final class AddOns {

	private static final List<AddOn> addOns;
	static {
		final List<AddOn> addOnsList = new ArrayList<>();
		Nxt.getStringListProperty("nxt.addOns").forEach(addOn -> {
			try {
				addOnsList.add((AddOn) Class.forName(addOn).newInstance());
			} catch (final ReflectiveOperationException e) {
				Logger.logErrorMessage(e.getMessage(), e);
			}
		});
		addOns = Collections.unmodifiableList(addOnsList);
		if (!AddOns.addOns.isEmpty()) {
			System.setProperty("java.security.policy",
					Nxt.isDesktopApplicationEnabled() ? "nxtdesktop.policy" : "nxt.policy");
			Logger.logMessage("Setting security manager with policy " + System.getProperty("java.security.policy"));
			System.setSecurityManager(new SecurityManager());
		}
		AddOns.addOns.forEach(addOn -> {
			Logger.logInfoMessage("Initializing " + addOn.getClass().getName());
			addOn.init();
		});
	}

	public static void init() {
	}

	public static void registerAPIRequestHandlers(final Map<String, APIServlet.APIRequestHandler> map) {
		for (final AddOn addOn : AddOns.addOns) {
			final APIServlet.APIRequestHandler requestHandler = addOn.getAPIRequestHandler();
			if (requestHandler != null) {
				if (!requestHandler.getAPITags().contains(APITag.ADDONS)) {
					Logger.logErrorMessage("Add-on " + addOn.getClass().getName()
							+ " attempted to register request handler which is not tagged as APITag.ADDONS, skipping");
					continue;
				}
				final String requestType = addOn.getAPIRequestType();
				if (requestType == null) {
					Logger.logErrorMessage("Add-on " + addOn.getClass().getName() + " requestType not defined");
					continue;
				}
				if (map.get(requestType) != null) {
					Logger.logErrorMessage("Add-on " + addOn.getClass().getName()
							+ " attempted to override requestType " + requestType + ", skipping");
					continue;
				}
				Logger.logMessage("Add-on " + addOn.getClass().getName() + " registered new API: " + requestType);
				map.put(requestType, requestHandler);
			}
		}
	}

	public static void shutdown() {
		AddOns.addOns.forEach(addOn -> {
			Logger.logShutdownMessage("Shutting down " + addOn.getClass().getName());
			addOn.shutdown();
		});
	}

	private AddOns() {
	}

}
