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

import nxt.Account;
import nxt.Constants;
import nxt.Nxt;
import nxt.NxtException;
import nxt.db.DbIterator;

public final class GetAccountLessors extends APIServlet.APIRequestHandler {

	static final GetAccountLessors instance = new GetAccountLessors();

	private GetAccountLessors() {
		super(new APITag[] { APITag.ACCOUNTS }, "account", "height");
	}

	@Override
	protected JSONStreamAware processRequest(final HttpServletRequest req) throws NxtException {

		final Account account = ParameterParser.getAccount(req);
		int height = ParameterParser.getHeight(req);
		if (height < 0) {
			height = Nxt.getBlockchain().getHeight();
		}

		final JSONObject response = new JSONObject();
		JSONData.putAccount(response, "account", account.getId());
		response.put("height", height);
		final JSONArray lessorsJSON = new JSONArray();

		try (DbIterator<Account> lessors = account.getLessors(height)) {
			if (lessors.hasNext()) {
				while (lessors.hasNext()) {
					final Account lessor = lessors.next();
					final JSONObject lessorJSON = new JSONObject();
					JSONData.putAccount(lessorJSON, "lessor", lessor.getId());
					lessorJSON.put("guaranteedBalanceNQT", String.valueOf(
							lessor.getGuaranteedBalanceNQT(Constants.GUARANTEED_BALANCE_CONFIRMATIONS, height)));
					lessorsJSON.add(lessorJSON);
				}
			}
		}
		response.put("lessors", lessorsJSON);
		return response;

	}

}
