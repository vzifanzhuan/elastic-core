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

final class GetPeers extends PeerServlet.PeerRequestHandler {

	static final GetPeers instance = new GetPeers();

	private GetPeers() {
	}

	@Override
	JSONStreamAware processRequest(final JSONObject request, final Peer peer) {
		final JSONObject response = new JSONObject();
		final JSONArray jsonArray = new JSONArray();
		final JSONArray services = new JSONArray();
		Peers.getAllPeers().forEach(otherPeer -> {
			if (!otherPeer.isBlacklisted() && (otherPeer.getAnnouncedAddress() != null)
					&& (otherPeer.getState() == Peer.State.CONNECTED) && otherPeer.shareAddress()) {
				jsonArray.add(otherPeer.getAnnouncedAddress());
				services.add(Long.toUnsignedString(((PeerImpl) otherPeer).getServices()));
			}
		});
		response.put("peers", jsonArray);
		response.put("services", services); // Separate array for backwards
											// compatibility
		return response;
	}

	@Override
	boolean rejectWhileDownloading() {
		return false;
	}

}
