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

package nxt.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

/**
 * Java LogManager extension for use with Nxt
 */
public class NxtLogManager extends LogManager {

	/** Logging reconfiguration in progress */
	private volatile boolean loggingReconfiguration = false;

	/**
	 * Create the Nxt log manager
	 *
	 * We will let the Java LogManager create its shutdown hook so that the
	 * shutdown context will be set up properly. However, we will intercept the
	 * reset() method so we can delay the actual shutdown until we are done
	 * terminating the Nxt processes.
	 */
	public NxtLogManager() {
		super();
	}

	/**
	 * Nxt shutdown is now complete, so call LogManager.reset() to terminate the
	 * log handlers.
	 */
	void nxtShutdown() {
		super.reset();
	}

	/**
	 * Reconfigure logging support using a configuration file
	 *
	 * @param inStream
	 *            Input stream
	 * @throws IOException
	 *             Error reading input stream
	 * @throws SecurityException
	 *             Caller does not have LoggingPermission("control")
	 */
	@Override
	public void readConfiguration(final InputStream inStream) throws IOException, SecurityException {
		this.loggingReconfiguration = true;
		super.readConfiguration(inStream);
		this.loggingReconfiguration = false;
	}

	/**
	 * Reset the log handlers
	 *
	 * This method is called to reset the log handlers. We will forward the call
	 * during logging reconfiguration but will ignore it otherwise. This allows
	 * us to continue to use logging facilities during Nxt shutdown.
	 */
	@Override
	public void reset() {
		if (this.loggingReconfiguration) super.reset();
	}
}
