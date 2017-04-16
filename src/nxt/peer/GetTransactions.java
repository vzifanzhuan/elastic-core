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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import nxt.Blockchain;
import nxt.Constants;
import nxt.Nxt;
import nxt.Transaction;

/**
 * Get the transactions
 */
class GetTransactions extends PeerServlet.PeerRequestHandler {

	static final GetTransactions instance = new GetTransactions();

	private GetTransactions() {
	}

	@Override
	JSONStreamAware processRequest(final JSONObject request, final Peer peer) {
		if (!Constants.INCLUDE_EXPIRED_PRUNABLE) return PeerServlet.UNSUPPORTED_REQUEST_TYPE;
		final JSONObject response = new JSONObject();
		final JSONArray transactionArray = new JSONArray();
		final JSONArray transactionIds = (JSONArray) request.get("transactionIds");
		final Blockchain blockchain = Nxt.getBlockchain();
		//
		// Return the transactions to the caller
		//
		if (transactionIds != null) transactionIds.forEach(transactionId -> {
			final long id = Long.parseUnsignedLong((String) transactionId);
			final Transaction transaction = blockchain.getTransaction(id);
			if (transaction != null) {
				transaction.getAppendages(true);
				final JSONObject transactionJSON = transaction.getJSONObject();
				transactionArray.add(transactionJSON);
			}
		});
		response.put("transactions", transactionArray);
		return response;
	}

	@Override
	boolean rejectWhileDownloading() {
		return true;
	}
}
