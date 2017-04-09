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

import nxt.Nxt;

public final class Scan extends APIServlet.APIRequestHandler {

	static final Scan instance = new Scan();

	private Scan() {
		super(new APITag[] { APITag.DEBUG }, "numBlocks", "height", "validate");
	}

	@Override
	protected final boolean allowRequiredBlockParameters() {
		return false;
	}

	@Override
	protected JSONStreamAware processRequest(final HttpServletRequest req) {
		final JSONObject response = new JSONObject();
		try {
			final boolean validate = "true".equalsIgnoreCase(req.getParameter("validate"));
			int numBlocks = 0;
			try {
				numBlocks = Integer.parseInt(req.getParameter("numBlocks"));
			} catch (final NumberFormatException ignored) {
			}
			int height = -1;
			try {
				height = Integer.parseInt(req.getParameter("height"));
			} catch (final NumberFormatException ignore) {
			}
			final long start = System.currentTimeMillis();
			try {
				Nxt.getBlockchainProcessor().setGetMoreBlocks(false);
				if (numBlocks > 0) {
					Nxt.getBlockchainProcessor().scan((Nxt.getBlockchain().getHeight() - numBlocks) + 1, validate);
				} else if (height >= 0) {
					Nxt.getBlockchainProcessor().scan(height, validate);
				} else {
					return JSONResponses.missing("numBlocks", "height");
				}
			} finally {
				Nxt.getBlockchainProcessor().setGetMoreBlocks(true);
			}
			final long end = System.currentTimeMillis();
			response.put("done", true);
			response.put("scanTime", (end - start) / 1000);
		} catch (final RuntimeException e) {
			JSONData.putException(response, e);
		}
		return response;
	}

	@Override
	protected boolean requireBlockchain() {
		return false;
	}

	@Override
	protected boolean requirePassword() {
		return true;
	}

	@Override
	protected final boolean requirePost() {
		return true;
	}

}
