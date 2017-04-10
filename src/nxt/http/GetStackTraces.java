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

import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

/**
 * <p>
 * The GetStackTraces API will return the current stack trace for each Nxt
 * thread.
 * </p>
 *
 * <p>
 * Request parameters:
 * </p>
 * <ul>
 * <li>depth - Stack trace depth (minimum 1, defaults to full trace)</li>
 * </ul>
 *
 * <p>
 * Response parameters:
 * </p>
 * <ul>
 * <li>locks - An array of lock objects for locks with waiters</li>
 * <li>threads - An array of thread objects</li>
 * </ul>
 *
 * <p>
 * Lock object:
 * </p>
 * <ul>
 * <li>name - Lock class name</li>
 * <li>hash - Lock identity hash code</li>
 * <li>thread - Identifier of thread holding the lock</li>
 * </ul>
 *
 * <p>
 * Monitor object:
 * </p>
 * <ul>
 * <li>name - Monitor class name</li>
 * <li>hash - Monitor identity hash</li>
 * <li>depth - Stack depth where monitor locked</li>
 * <li>trace - Stack element where monitor locked</li>
 * </ul>
 *
 * <p>
 * Thread object:
 * </p>
 * <ul>
 * <li>blocked - Lock object if thread is waiting on a lock</li>
 * <li>id - Thread identifier</li>
 * <li>locks - Array of monitor objects for locks held by this thread</li>
 * <li>name - Thread name</li>
 * <li>state - Thread state</li>
 * <li>trace - Array of stack trace elements</li>
 * </ul>
 */
public class GetStackTraces extends APIServlet.APIRequestHandler {

	/** GetLog instance */
	static final GetStackTraces instance = new GetStackTraces();

	/**
	 * Create the GetStackTraces instance
	 */
	private GetStackTraces() {
		super(new APITag[] { APITag.DEBUG }, "depth");
	}

	@Override
	protected boolean allowRequiredBlockParameters() {
		return false;
	}

	/**
	 * Process the GetStackTraces API request
	 *
	 * @param req
	 *            API request
	 * @return API response
	 */
	@Override
	protected JSONStreamAware processRequest(final HttpServletRequest req) {
		String value;
		//
		// Get the number of trace lines to return
		//
		int depth;
		value = req.getParameter("depth");
		if (value != null) {
			depth = Math.max(Integer.valueOf(value), 1);
		} else {
			depth = Integer.MAX_VALUE;
		}
		//
		// Get the thread information
		//
		final JSONArray threadsJSON = new JSONArray();
		final JSONArray locksJSON = new JSONArray();
		final ThreadMXBean tmxBean = ManagementFactory.getThreadMXBean();
		final boolean tmxMI = tmxBean.isObjectMonitorUsageSupported();
		final ThreadInfo[] tList = tmxBean.dumpAllThreads(tmxMI, false);
		//
		// Generate the response
		//
		for (final ThreadInfo tInfo : tList) {
			final JSONObject threadJSON = new JSONObject();
			//
			// General thread information
			//
			threadJSON.put("id", tInfo.getThreadId());
			threadJSON.put("name", tInfo.getThreadName());
			threadJSON.put("state", tInfo.getThreadState().toString());
			//
			// Gather lock usage
			//
			if (tmxMI) {
				final MonitorInfo[] mList = tInfo.getLockedMonitors();
				if (mList.length > 0) {
					final JSONArray monitorsJSON = new JSONArray();
					for (final MonitorInfo mInfo : mList) {
						final JSONObject lockJSON = new JSONObject();
						lockJSON.put("name", mInfo.getClassName());
						lockJSON.put("hash", mInfo.getIdentityHashCode());
						lockJSON.put("depth", mInfo.getLockedStackDepth());
						lockJSON.put("trace", mInfo.getLockedStackFrame().toString());
						monitorsJSON.add(lockJSON);
					}
					threadJSON.put("locks", monitorsJSON);
				}
				if (tInfo.getThreadState() == Thread.State.BLOCKED) {
					final LockInfo lInfo = tInfo.getLockInfo();
					if (lInfo != null) {
						final JSONObject lockJSON = new JSONObject();
						lockJSON.put("name", lInfo.getClassName());
						lockJSON.put("hash", lInfo.getIdentityHashCode());
						lockJSON.put("thread", tInfo.getLockOwnerId());
						threadJSON.put("blocked", lockJSON);
						boolean addLock = true;
						for (final Object lock : locksJSON) {
							if (((JSONObject) lock).get("name").equals(lInfo.getClassName())) {
								addLock = false;
								break;
							}
						}
						if (addLock) {
							locksJSON.add(lockJSON);
						}
					}
				}
			}
			//
			// Add the stack trace
			//
			final StackTraceElement[] elements = tInfo.getStackTrace();
			final JSONArray traceJSON = new JSONArray();
			int ix = 0;
			for (final StackTraceElement element : elements) {
				traceJSON.add(element.toString());
				if (++ix == depth) {
					break;
				}
			}
			threadJSON.put("trace", traceJSON);
			//
			// Add the thread to the response
			//
			threadsJSON.add(threadJSON);
		}
		//
		// Return the response
		//
		final JSONObject response = new JSONObject();
		response.put("threads", threadsJSON);
		response.put("locks", locksJSON);
		return response;
	}

	@Override
	protected boolean requireBlockchain() {
		return false;
	}

	/**
	 * Require the administrator password
	 *
	 * @return TRUE if the admin password is required
	 */
	@Override
	protected boolean requirePassword() {
		return true;
	}

}
