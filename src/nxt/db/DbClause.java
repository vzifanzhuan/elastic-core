/*
 * Copyright © 2013-2016 The XEL Core Developers.
 * Copyright © 2016 Jelurida IP B.V.
 *
 * See the LICENSE.txt file at the top-level directory of this distribution
 * for licensing information.
 *
 * Unless otherwise agreed in a custom licensing agreement with Jelurida B.V.,
 * no part of the XEL software, including this file, may be copied, modified,
 * propagated, or distributed except according to the terms contained in the
 * LICENSE.txt file.
 *
 * Removal or modification of this copyright notice is prohibited.
 *
 */

package nxt.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class DbClause {

	public static final class BooleanClause extends DbClause {

		private final boolean value;

		public BooleanClause(final String columnName, final boolean value) {
			super(" " + columnName + " = ? ");
			this.value = value;
		}

		@Override
		protected int set(final PreparedStatement pstmt, final int index) throws SQLException {
			pstmt.setBoolean(index, this.value);
			return index + 1;
		}

	}

	public static final class ByteClause extends DbClause {

		private final byte value;

		public ByteClause(final String columnName, final byte value) {
			super(" " + columnName + " = ? ");
			this.value = value;
		}

		public ByteClause(final String columnName, final Op operator, final byte value) {
			super(" " + columnName + operator.operator() + "? ");
			this.value = value;
		}

		@Override
		protected int set(final PreparedStatement pstmt, final int index) throws SQLException {
			pstmt.setByte(index, this.value);
			return index + 1;
		}

	}

	public static final class BytesClause extends DbClause {

		private final byte[] value;

		public BytesClause(final String columnName, final byte[] value) {
			super(" " + columnName + " = ? ");
			this.value = value;
		}

		public BytesClause(final String columnName, final Op operator, final byte[] value) {
			super(" " + columnName + operator.operator() + "? ");
			this.value = value;
		}

		@Override
		protected int set(final PreparedStatement pstmt, final int index) throws SQLException {
			pstmt.setBytes(index, this.value);
			return index + 1;
		}

	}

	public static final class FixedClause extends DbClause {

		public FixedClause(final String clause) {
			super(clause);
		}

		@Override
		protected int set(final PreparedStatement pstmt, final int index) throws SQLException {
			return index;
		}

	}

	public static final class IntClause extends DbClause {

		private final int value;

		public IntClause(final String columnName, final int value) {
			super(" " + columnName + " = ? ");
			this.value = value;
		}

		public IntClause(final String columnName, final Op operator, final int value) {
			super(" " + columnName + operator.operator() + "? ");
			this.value = value;
		}

		@Override
		protected int set(final PreparedStatement pstmt, final int index) throws SQLException {
			pstmt.setInt(index, this.value);
			return index + 1;
		}

	}

	public static final class LikeClause extends DbClause {

		private final String prefix;

		public LikeClause(final String columnName, final String prefix) {
			super(" " + columnName + " LIKE ? ");
			this.prefix = prefix.replace("%", "\\%").replace("_", "\\_") + '%';
		}

		@Override
		protected int set(final PreparedStatement pstmt, final int index) throws SQLException {
			pstmt.setString(index, this.prefix);
			return index + 1;
		}
	}

	public static final class LongClause extends DbClause {

		private final long value;

		public LongClause(final String columnName, final long value) {
			super(" " + columnName + " = ? ");
			this.value = value;
		}

		public LongClause(final String columnName, final Op operator, final long value) {
			super(" " + columnName + operator.operator() + "? ");
			this.value = value;
		}

		@Override
		protected int set(final PreparedStatement pstmt, final int index) throws SQLException {
			pstmt.setLong(index, this.value);
			return index + 1;
		}
	}

	public static final class NotNullClause extends DbClause {

		public NotNullClause(final String columnName) {
			super(" " + columnName + " IS NOT NULL ");
		}

		@Override
		protected int set(final PreparedStatement pstmt, final int index) throws SQLException {
			return index;
		}

	}

	protected static final class NullClause extends DbClause {

		public NullClause(final String columnName) {
			super(" " + columnName + " IS NULL ");
		}

		@Override
		protected int set(final PreparedStatement pstmt, final int index) throws SQLException {
			return index;
		}

	}

	public enum Op {

		LT("<"), LTE("<="), GT(">"), GTE(">="), NE("<>");

		private final String operator;

		Op(final String operator) {
			this.operator = operator;
		}

		public String operator() {
			return this.operator;
		}
	}

	public static final class StringClause extends DbClause {

		private final String value;

		public StringClause(final String columnName, final String value) {
			super(" " + columnName + " = ? ");
			this.value = value;
		}

		@Override
		protected int set(final PreparedStatement pstmt, final int index) throws SQLException {
			pstmt.setString(index, this.value);
			return index + 1;
		}

	}

	public static final DbClause EMPTY_CLAUSE = new FixedClause(" TRUE ");

	private final String clause;

	DbClause(final String clause) {
		this.clause = clause;
	}

	public DbClause and(final DbClause other) {
		return new DbClause(this.clause + " AND " + other.clause) {
			@Override
			protected int set(final PreparedStatement pstmt, int index) throws SQLException {
				index = DbClause.this.set(pstmt, index);
				index = other.set(pstmt, index);
				return index;
			}
		};
	}

	final String getClause() {
		return this.clause;
	}

	protected abstract int set(PreparedStatement pstmt, int index) throws SQLException;

}
