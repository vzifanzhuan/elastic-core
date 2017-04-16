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

package nxt.user;

import java.io.IOException;
import java.net.InetAddress;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONStreamAware;

import nxt.peer.Peer;

final class RemoveKnownPeer extends UserServlet.UserRequestHandler {

	static final RemoveKnownPeer instance = new RemoveKnownPeer();

	private RemoveKnownPeer() {
	}

	@Override
	JSONStreamAware processRequest(final HttpServletRequest req, final User user) throws IOException {
		if ((Users.allowedUserHosts == null) && !InetAddress.getByName(req.getRemoteAddr()).isLoopbackAddress())
            return JSONResponses.LOCAL_USERS_ONLY;
        else {
			final int index = Integer.parseInt(req.getParameter("peer"));
			final Peer peer = Users.getPeer(index);
			if (peer != null) peer.remove();
		}
		return null;
	}
}
