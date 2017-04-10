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

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import nxt.Nxt;
import nxt.Transaction;
import nxt.db.DbIterator;
import nxt.util.Convert;
import nxt.util.Filter;

public final class GetUnconfirmedTransactions extends APIServlet.APIRequestHandler {

	static final GetUnconfirmedTransactions instance = new GetUnconfirmedTransactions();

	private GetUnconfirmedTransactions() {
		super(new APITag[] { APITag.TRANSACTIONS, APITag.ACCOUNTS }, "account", "account", "account");
	}

	@Override
	protected JSONStreamAware processRequest(final HttpServletRequest req) throws ParameterException {

		final Set<Long> accountIds = Convert.toSet(ParameterParser.getAccountIds(req, false));
		final Filter<Transaction> filter = accountIds.isEmpty() ? transaction -> true
				: transaction -> accountIds.contains(transaction.getSenderId())
						|| accountIds.contains(transaction.getRecipientId());

		final JSONArray transactions = new JSONArray();
		try (DbIterator<? extends Transaction> transactionsIterator = Nxt.getTransactionProcessor()
				.getAllUnconfirmedTransactions()) {
			while (transactionsIterator.hasNext()) {
				final Transaction transaction = transactionsIterator.next();
				if (filter.ok(transaction)) {
					transactions.add(JSONData.unconfirmedTransaction(transaction));
				}
			}
		}

		final JSONObject response = new JSONObject();
		response.put("unconfirmedTransactions", transactions);
		return response;
	}

}
