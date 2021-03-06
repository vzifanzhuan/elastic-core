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

package nxt.env;

import java.io.File;
import java.net.URI;

public class CommandLineMode implements RuntimeMode {

	@Override
	public void alert(final String message) {
	}

	@Override
	public void init() {
	}

	@Override
	public void launchDesktopApplication() {
	}

	@Override
	public void setServerStatus(final ServerStatus status, final URI wallet, final File logFileDir) {
	}

	@Override
	public void shutdown() {
	}
}
