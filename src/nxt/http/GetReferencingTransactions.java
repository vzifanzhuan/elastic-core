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

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import nxt.Nxt;
import nxt.NxtException;
import nxt.Transaction;

public final class GetReferencingTransactions extends APIServlet.APIRequestHandler {

	static final GetReferencingTransactions instance = new GetReferencingTransactions();

	private GetReferencingTransactions() {
		super(new APITag[] { APITag.TRANSACTIONS }, "transaction", "firstIndex", "lastIndex");
	}

	@Override
	protected JSONStreamAware processRequest(final HttpServletRequest req) throws NxtException {

		final long transactionId = ParameterParser.getUnsignedLong(req, "transaction", true);
		final int firstIndex = ParameterParser.getFirstIndex(req);
		final int lastIndex = ParameterParser.getLastIndex(req);

		final JSONArray transactions = new JSONArray();
        for (Transaction transaction : Nxt.getBlockchain().getReferencingTransactions(transactionId,
                firstIndex, lastIndex))
            transactions.add(JSONData.transaction(transaction));

		final JSONObject response = new JSONObject();
		response.put("transactions", transactions);
		return response;

	}

}
