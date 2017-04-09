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

package nxt.peer;

import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import nxt.Nxt;
import nxt.NxtException;
import nxt.util.JSON;

final class ProcessTransactions extends PeerServlet.PeerRequestHandler {

	static final ProcessTransactions instance = new ProcessTransactions();

	private ProcessTransactions() {
	}

	@Override
	JSONStreamAware processRequest(final JSONObject request, final Peer peer) {

		try {
			Nxt.getTransactionProcessor().processPeerTransactions(request);
			return JSON.emptyJSON;
		} catch (RuntimeException | NxtException.ValidationException e) {
			// Logger.logDebugMessage("Failed to parse peer transactions: " +
			// request.toJSONString());
			peer.blacklist(e);
			return PeerServlet.error(e);
		}

	}

	@Override
	boolean rejectWhileDownloading() {
		return true;
	}

}
