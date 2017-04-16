package nxt;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * A very simple class that helps encode/decode for Ascii85 / base85 The version
 * that is likely most similar that is implemented here would be the Adobe
 * version.
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Ascii85">Ascii85</a>
 */
class Ascii85 {

	private final static int ASCII_SHIFT = 33;

	private static final int[] BASE85_POW = new int[]{1, 85, 85 * 85, 85 * 85 * 85, 85 * 85 * 85 * 85};

	private static final Pattern REMOVE_WHITESPACE = Pattern.compile("\\s+");

	private static int byteToInt(final byte[] value) {
		if ((value == null) || (value.length != 4))
            throw new IllegalArgumentException("You cannot create an int without exactly 4 bytes.");
		return ByteBuffer.wrap(value).getInt();
	}

	/**
	 * This is a very simple base85 decoder. It respects the 'z' optimization
	 * for empty chunks, & strips whitespace between characters to respect line
	 * limits.
	 * 
	 * @see <a href="https://en.wikipedia.org/wiki/Ascii85">Ascii85</a>
	 * @param chars
	 *            The input characters that are base85 encoded.
	 * @return The binary data decoded from the input
	 */
	static byte[] decode(String chars) {
		if ((chars == null) || (chars.length() == 0))
            throw new IllegalArgumentException("You must provide a non-zero length input");
		final ByteBuffer bytebuff = ByteBuffer.allocate(((chars.length() * 4) / 5));
		// 1. Whitespace characters may occur anywhere to accommodate line
		// length limitations. So lets strip it.
		chars = Ascii85.REMOVE_WHITESPACE.matcher(chars).replaceAll("");
		// Since Base85 is an ascii encoder, we don't need to get the bytes as
		// UTF-8.
		final byte[] payload = chars.getBytes(StandardCharsets.US_ASCII);
		final byte[] chunk = new byte[5];
		int chunkIndex = 0;
		for (final byte currByte : payload) {
			// Because all-zero data is quite common, an exception is made for
			// the sake of data compression,
			// and an all-zero group is encoded as a single character "z"
			// instead of "!!!!!".
			if (currByte == 'z') {
				if (chunkIndex > 0) throw new IllegalArgumentException("The payload is not base 85 encoded.");
				chunk[chunkIndex++] = '!';
				chunk[chunkIndex++] = '!';
				chunk[chunkIndex++] = '!';
				chunk[chunkIndex++] = '!';
				chunk[chunkIndex++] = '!';
			} else chunk[chunkIndex++] = currByte;

			if (chunkIndex == 5) {
				bytebuff.put(Ascii85.decodeChunk(chunk));
				Arrays.fill(chunk, (byte) 0);
				chunkIndex = 0;
			}
		}

		// If we didn't end on 0, then we need some padding
		if (chunkIndex > 0) {
			final int numPadded = chunk.length - chunkIndex;
			Arrays.fill(chunk, chunkIndex, chunk.length, (byte) 'u');
			final byte[] paddedDecode = Ascii85.decodeChunk(chunk);
			for (int i = 0; i < (paddedDecode.length - numPadded); i++) bytebuff.put(paddedDecode[i]);
		}

		bytebuff.flip();
		return Arrays.copyOf(bytebuff.array(), bytebuff.limit());
	}

	private static byte[] decodeChunk(final byte[] chunk) {
		if (chunk.length != 5) throw new IllegalArgumentException("You can only decode chunks of size 5.");
		int value = 0;
		value += (chunk[0] - Ascii85.ASCII_SHIFT) * Ascii85.BASE85_POW[4];
		value += (chunk[1] - Ascii85.ASCII_SHIFT) * Ascii85.BASE85_POW[3];
		value += (chunk[2] - Ascii85.ASCII_SHIFT) * Ascii85.BASE85_POW[2];
		value += (chunk[3] - Ascii85.ASCII_SHIFT) * Ascii85.BASE85_POW[1];
		value += (chunk[4] - Ascii85.ASCII_SHIFT) * Ascii85.BASE85_POW[0];

		return Ascii85.intToByte(value);
	}

	static String encode(final byte[] payload) {
		if ((payload == null) || (payload.length == 0))
            throw new IllegalArgumentException("You must provide a non-zero length input");
		final StringBuilder stringBuff = new StringBuilder((payload.length * 5) / 4);
		// We break the payload into int (4 bytes)
		final byte[] chunk = new byte[4];
		int chunkIndex = 0;
		for (final byte currByte : payload) {
			chunk[chunkIndex++] = currByte;

			if (chunkIndex == 4) {
				final int value = Ascii85.byteToInt(chunk);
				// Because all-zero data is quite common, an exception is made
				// for the sake of data compression,
				// and an all-zero group is encoded as a single character "z"
				// instead of "!!!!!".
				if (value == 0) stringBuff.append('z');
                else stringBuff.append(Ascii85.encodeChunk(value));
				Arrays.fill(chunk, (byte) 0);
				chunkIndex = 0;
			}
		}

		// If we didn't end on 0, then we need some padding
		if (chunkIndex > 0) {
			final int numPadded = chunk.length - chunkIndex;
			Arrays.fill(chunk, chunkIndex, chunk.length, (byte) 0);
			final int value = Ascii85.byteToInt(chunk);
			final char[] encodedChunk = Ascii85.encodeChunk(value);
			for (int i = 0; i < (encodedChunk.length - numPadded); i++) stringBuff.append(encodedChunk[i]);
		}

		return stringBuff.toString();
	}

	private static char[] encodeChunk(int value) {
		final char[] encodedChunk = new char[5];
		for (int i = 0; i < encodedChunk.length; i++) {
			encodedChunk[i] = (char) ((value / Ascii85.BASE85_POW[4 - i]) + Ascii85.ASCII_SHIFT);
			value = value % Ascii85.BASE85_POW[4 - i];
		}
		return encodedChunk;
	}

	private static byte[] intToByte(final int value) {
		return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) (value) };
	}

	private Ascii85() {
	}

}
