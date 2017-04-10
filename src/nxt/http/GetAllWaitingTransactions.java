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

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import nxt.Nxt;
import nxt.Transaction;

public final class GetAllWaitingTransactions extends APIServlet.APIRequestHandler {

	static final GetAllWaitingTransactions instance = new GetAllWaitingTransactions();

	private GetAllWaitingTransactions() {
		super(new APITag[] { APITag.DEBUG });
	}

	@Override
	protected JSONStreamAware processRequest(final HttpServletRequest req) {
		final JSONObject response = new JSONObject();
		final JSONArray jsonArray = new JSONArray();
		response.put("transactions", jsonArray);
		final Transaction[] transactions = Nxt.getTransactionProcessor().getAllWaitingTransactions();
		for (final Transaction transaction : transactions) {
			jsonArray.add(JSONData.unconfirmedTransaction(transaction));
		}
		return response;
	}

	@Override
	protected boolean requireBlockchain() {
		return false;
	}

}
