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

package nxt;

import java.util.Arrays;

import nxt.crypto.Crypto;
import nxt.util.Convert;

public final class Token {

	public static String generateToken(final String secretPhrase, final byte[] message) {
		final byte[] data = new byte[message.length + 32 + 4];
		System.arraycopy(message, 0, data, 0, message.length);
		System.arraycopy(Crypto.getPublicKey(secretPhrase), 0, data, message.length, 32);
		final int timestamp = Nxt.getEpochTime();
		data[message.length + 32] = (byte) timestamp;
		data[message.length + 32 + 1] = (byte) (timestamp >> 8);
		data[message.length + 32 + 2] = (byte) (timestamp >> 16);
		data[message.length + 32 + 3] = (byte) (timestamp >> 24);

		final byte[] token = new byte[100];
		System.arraycopy(data, message.length, token, 0, 32 + 4);
		System.arraycopy(Crypto.sign(data, secretPhrase), 0, token, 32 + 4, 64);

		final StringBuilder buf = new StringBuilder();
		for (int ptr = 0; ptr < 100; ptr += 5) {

			final long number = (token[ptr] & 0xFF) | (((long) (token[ptr + 1] & 0xFF)) << 8)
					| (((long) (token[ptr + 2] & 0xFF)) << 16) | (((long) (token[ptr + 3] & 0xFF)) << 24)
					| (((long) (token[ptr + 4] & 0xFF)) << 32);

			if (number < 32) buf.append("0000000");
            else if (number < 1024) buf.append("000000");
            else if (number < 32768) buf.append("00000");
            else if (number < 1048576) buf.append("0000");
            else if (number < 33554432) buf.append("000");
            else if (number < 1073741824) buf.append("00");
            else if (number < 34359738368L) buf.append("0");
			buf.append(Long.toString(number, 32));

		}

		return buf.toString();

	}

	public static String generateToken(final String secretPhrase, final String messageString) {
		return Token.generateToken(secretPhrase, Convert.toBytes(messageString));
	}

	public static Token parseToken(final String tokenString, final byte[] messageBytes) {
		final byte[] tokenBytes = new byte[100];
		int i = 0, j = 0;

		for (; i < tokenString.length(); i += 8, j += 5) {

			final long number = Long.parseLong(tokenString.substring(i, i + 8), 32);
			tokenBytes[j] = (byte) number;
			tokenBytes[j + 1] = (byte) (number >> 8);
			tokenBytes[j + 2] = (byte) (number >> 16);
			tokenBytes[j + 3] = (byte) (number >> 24);
			tokenBytes[j + 4] = (byte) (number >> 32);

		}

		if (i != 160) throw new IllegalArgumentException("Invalid token string: " + tokenString);
		final byte[] publicKey = new byte[32];
		System.arraycopy(tokenBytes, 0, publicKey, 0, 32);
		final int timestamp = (tokenBytes[32] & 0xFF) | ((tokenBytes[33] & 0xFF) << 8) | ((tokenBytes[34] & 0xFF) << 16)
				| ((tokenBytes[35] & 0xFF) << 24);
		final byte[] signature = new byte[64];
		System.arraycopy(tokenBytes, 36, signature, 0, 64);

		final byte[] data = new byte[messageBytes.length + 36];
		System.arraycopy(messageBytes, 0, data, 0, messageBytes.length);
		System.arraycopy(tokenBytes, 0, data, messageBytes.length, 36);
		final byte[] announcedPublicKey = Account.getPublicKey(Account.getId(publicKey));
		final boolean isValid = Crypto.verify(signature, data, publicKey)
				&& ((announcedPublicKey == null) || Arrays.equals(publicKey, announcedPublicKey));

		return new Token(publicKey, timestamp, isValid);

	}

	public static Token parseToken(final String tokenString, final String website) {
		return Token.parseToken(tokenString, Convert.toBytes(website));
	}

	private final byte[] publicKey;
	private final int timestamp;
	private final boolean isValid;

	private Token(final byte[] publicKey, final int timestamp, final boolean isValid) {
		this.publicKey = publicKey;
		this.timestamp = timestamp;
		this.isValid = isValid;
	}

	public byte[] getPublicKey() {
		return this.publicKey;
	}

	public int getTimestamp() {
		return this.timestamp;
	}

	public boolean isValid() {
		return this.isValid;
	}

}
