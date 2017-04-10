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

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import nxt.peer.Peer;
import nxt.peer.Peers;

/**
 * <p>
 * The GetInboundPeers API will return a list of inbound peers. An inbound peer
 * is a peer that has sent a request to this peer within the previous 30
 * minutes.
 * </p>
 *
 * <p>
 * Request parameters:
 * </p>
 * <ul>
 * <li>includePeerInfo - Specify 'true' to include the peer information or
 * 'false' to include just the peer address. The default is 'false'.</li>
 * </ul>
 *
 * <p>
 * Response parameters:
 * </p>
 * <ul>
 * <li>peers - An array of peers</li>
 * </ul>
 *
 * <p>
 * Error Response parameters:
 * </p>
 * <ul>
 * <li>errorCode - API error code</li>
 * <li>errorDescription - API error description</li>
 * </ul>
 */
public final class GetInboundPeers extends APIServlet.APIRequestHandler {

	/** GetInboundPeers instance */
	static final GetInboundPeers instance = new GetInboundPeers();

	/**
	 * Create the GetInboundPeers instance
	 */
	private GetInboundPeers() {
		super(new APITag[] { APITag.NETWORK }, "includePeerInfo");
	}

	@Override
	protected boolean allowRequiredBlockParameters() {
		return false;
	}

	/**
	 * Process the GetInboundPeers API request
	 *
	 * @param req
	 *            API request
	 * @return API response or null
	 */
	@Override
	protected JSONStreamAware processRequest(final HttpServletRequest req) {
		final boolean includePeerInfo = "true".equalsIgnoreCase(req.getParameter("includePeerInfo"));
		final List<Peer> peers = Peers.getInboundPeers();
		final JSONArray peersJSON = new JSONArray();
		if (includePeerInfo) {
			peers.forEach(peer -> peersJSON.add(JSONData.peer(peer)));
		} else {
			peers.forEach(peer -> peersJSON.add(peer.getHost()));
		}
		final JSONObject response = new JSONObject();
		response.put("peers", peersJSON);
		return response;
	}

}
