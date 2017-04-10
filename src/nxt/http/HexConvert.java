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

import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import nxt.util.Convert;
import nxt.util.JSON;

public final class HexConvert extends APIServlet.APIRequestHandler {

	static final HexConvert instance = new HexConvert();

	private HexConvert() {
		super(new APITag[] { APITag.UTILS }, "string");
	}

	@Override
	protected boolean allowRequiredBlockParameters() {
		return false;
	}

	@Override
	protected JSONStreamAware processRequest(final HttpServletRequest req) {
		final String string = Convert.emptyToNull(req.getParameter("string"));
		if (string == null) {
			return JSON.emptyJSON;
		}
		final JSONObject response = new JSONObject();
		try {
			final byte[] asHex = Convert.parseHexString(string);
			if (asHex.length > 0) {
				response.put("text", Convert.toString(asHex));
			}
		} catch (final RuntimeException ignore) {
		}
		try {
			final byte[] asText = Convert.toBytes(string);
			response.put("binary", Convert.toHexString(asText));
		} catch (final RuntimeException ignore) {
		}
		return response;
	}

	@Override
	protected boolean requireBlockchain() {
		return false;
	}

}
