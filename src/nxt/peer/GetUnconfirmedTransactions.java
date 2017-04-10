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

import java.util.List;
import java.util.SortedSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import nxt.Nxt;
import nxt.Transaction;
import nxt.util.JSON;

final class GetUnconfirmedTransactions extends PeerServlet.PeerRequestHandler {

	static final GetUnconfirmedTransactions instance = new GetUnconfirmedTransactions();

	private GetUnconfirmedTransactions() {
	}

	@Override
	JSONStreamAware processRequest(final JSONObject request, final Peer peer) {

		final List<String> exclude = (List<String>) request.get("exclude");
		if (exclude == null) {
			return JSON.emptyJSON;
		}

		final SortedSet<? extends Transaction> transactionSet = Nxt.getTransactionProcessor()
				.getCachedUnconfirmedTransactions(exclude);
		final JSONArray transactionsData = new JSONArray();
		for (final Transaction transaction : transactionSet) {
			if (transactionsData.size() >= 100) {
				break;
			}
			transactionsData.add(transaction.getJSONObject());
		}
		final JSONObject response = new JSONObject();
		response.put("unconfirmedTransactions", transactionsData);

		return response;
	}

	@Override
	boolean rejectWhileDownloading() {
		return true;
	}

}
