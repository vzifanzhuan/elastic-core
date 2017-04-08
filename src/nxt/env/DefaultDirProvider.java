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
import java.nio.file.Paths;
import java.util.Properties;

public class DefaultDirProvider implements DirProvider {

	@Override
	public File getConfDir() {
		return new File(this.getUserHomeDir(), "conf");
	}

	@Override
	public String getDbDir(final String dbDir) {
		return dbDir;
	}

	@Override
	public File getLogFileDir() {
		return new File(this.getUserHomeDir(), "logs");
	}

	@Override
	public String getUserHomeDir() {
		return Paths.get(".").toAbsolutePath().getParent().toString();
	}

	@Override
	public boolean isLoadPropertyFileFromUserDir() {
		return true;
	}

	@Override
	public void updateLogFileHandler(final Properties loggingProperties) {
	}

}
