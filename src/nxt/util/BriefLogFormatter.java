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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * A Java logging formatter that writes more compact output than the default
 */
class BriefLogFormatter extends Formatter {

	/** Format used for log messages */
	private static final ThreadLocal<MessageFormat> messageFormat = ThreadLocal.withInitial(() -> new MessageFormat("{0,date,yyyy-MM-dd HH:mm:ss} {1}: {2}\n{3}"));

	/** Logger instance at the top of the name tree */
	private static final Logger logger = Logger.getLogger("");

	/** singleton BriefLogFormatter instance */
	private static final BriefLogFormatter briefLogFormatter = new BriefLogFormatter();

	/**
	 * Configures JDK logging to use this class for everything
	 */
	static void init() {
		final Handler[] handlers = BriefLogFormatter.logger.getHandlers();
		for (final Handler handler : handlers) handler.setFormatter(BriefLogFormatter.briefLogFormatter);
	}

	private BriefLogFormatter() {
	}

	/**
	 * Format the log record as follows:
	 *
	 * Date Level Message ExceptionTrace
	 *
	 * @param logRecord
	 *            The log record
	 * @return The formatted string
	 */
	@Override
	public String format(final LogRecord logRecord) {
		final Object[] arguments = new Object[4];
		arguments[0] = new Date(logRecord.getMillis());
		arguments[1] = logRecord.getLevel().getName();
		arguments[2] = logRecord.getMessage();
		final Throwable exc = logRecord.getThrown();
		if (exc != null) {
			final Writer result = new StringWriter();
			exc.printStackTrace(new PrintWriter(result));
			arguments[3] = result.toString();
		} else arguments[3] = "";
		return BriefLogFormatter.messageFormat.get().format(arguments);
	}

}
