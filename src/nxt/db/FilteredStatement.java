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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

/**
 * Wrapper for a SQL Statement
 *
 * The wrapper forwards all methods to the wrapped statement
 */
class FilteredStatement implements Statement {

	private final Statement stmt;

	public FilteredStatement(final Statement stmt) {
		this.stmt = stmt;
	}

	@Override
	public void addBatch(final String sql) throws SQLException {
		this.stmt.addBatch(sql);
	}

	@Override
	public void cancel() throws SQLException {
		this.stmt.cancel();
	}

	@Override
	public void clearBatch() throws SQLException {
		this.stmt.clearBatch();
	}

	@Override
	public void clearWarnings() throws SQLException {
		this.stmt.clearWarnings();
	}

	@Override
	public void close() throws SQLException {
		this.stmt.close();
	}

	@Override
	public void closeOnCompletion() throws SQLException {
		this.stmt.closeOnCompletion();
	}

	@Override
	public boolean execute(final String sql) throws SQLException {
		return this.stmt.execute(sql);
	}

	@Override
	public boolean execute(final String sql, final int autoGeneratedKeys) throws SQLException {
		return this.stmt.execute(sql, autoGeneratedKeys);
	}

	@Override
	public boolean execute(final String sql, final int[] columnIndexes) throws SQLException {
		return this.stmt.execute(sql, columnIndexes);
	}

	@Override
	public boolean execute(final String sql, final String[] columnNames) throws SQLException {
		return this.stmt.execute(sql, columnNames);
	}

	@Override
	public int[] executeBatch() throws SQLException {
		return this.stmt.executeBatch();
	}

	@Override
	public long[] executeLargeBatch() throws SQLException {
		return this.stmt.executeLargeBatch();
	}

	@Override
	public long executeLargeUpdate(final String sql) throws SQLException {
		return this.stmt.executeLargeUpdate(sql);
	}

	@Override
	public long executeLargeUpdate(final String sql, final int autoGeneratedKeys) throws SQLException {
		return this.stmt.executeLargeUpdate(sql, autoGeneratedKeys);
	}

	@Override
	public long executeLargeUpdate(final String sql, final int[] columnIndexes) throws SQLException {
		return this.stmt.executeLargeUpdate(sql, columnIndexes);
	}

	@Override
	public long executeLargeUpdate(final String sql, final String[] columnNames) throws SQLException {
		return this.stmt.executeLargeUpdate(sql, columnNames);
	}

	@Override
	public ResultSet executeQuery(final String sql) throws SQLException {
		return this.stmt.executeQuery(sql);
	}

	@Override
	public int executeUpdate(final String sql) throws SQLException {
		return this.stmt.executeUpdate(sql);
	}

	@Override
	public int executeUpdate(final String sql, final int autoGeneratedKeys) throws SQLException {
		return this.stmt.executeUpdate(sql, autoGeneratedKeys);
	}

	@Override
	public int executeUpdate(final String sql, final int[] columnIndexes) throws SQLException {
		return this.stmt.executeUpdate(sql, columnIndexes);
	}

	@Override
	public int executeUpdate(final String sql, final String[] columnNames) throws SQLException {
		return this.stmt.executeUpdate(sql, columnNames);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return this.stmt.getConnection();
	}

	@Override
	public int getFetchDirection() throws SQLException {
		return this.stmt.getFetchDirection();
	}

	@Override
	public int getFetchSize() throws SQLException {
		return this.stmt.getFetchSize();
	}

	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		return this.stmt.getGeneratedKeys();
	}

	@Override
	public long getLargeMaxRows() throws SQLException {
		return this.stmt.getLargeMaxRows();
	}

	@Override
	public long getLargeUpdateCount() throws SQLException {
		return this.stmt.getLargeUpdateCount();
	}

	@Override
	public int getMaxFieldSize() throws SQLException {
		return this.stmt.getMaxFieldSize();
	}

	@Override
	public int getMaxRows() throws SQLException {
		return this.stmt.getMaxRows();
	}

	@Override
	public boolean getMoreResults() throws SQLException {
		return this.stmt.getMoreResults();
	}

	@Override
	public boolean getMoreResults(final int current) throws SQLException {
		return this.stmt.getMoreResults(current);
	}

	@Override
	public int getQueryTimeout() throws SQLException {
		return this.stmt.getQueryTimeout();
	}

	@Override
	public ResultSet getResultSet() throws SQLException {
		return this.stmt.getResultSet();
	}

	@Override
	public int getResultSetConcurrency() throws SQLException {
		return this.stmt.getResultSetConcurrency();
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		return this.stmt.getResultSetHoldability();
	}

	@Override
	public int getResultSetType() throws SQLException {
		return this.stmt.getResultSetType();
	}

	@Override
	public int getUpdateCount() throws SQLException {
		return this.stmt.getUpdateCount();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return this.stmt.getWarnings();
	}

	@Override
	public boolean isClosed() throws SQLException {
		return this.stmt.isClosed();
	}

	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		return this.stmt.isCloseOnCompletion();
	}

	@Override
	public boolean isPoolable() throws SQLException {
		return this.stmt.isPoolable();
	}

	@Override
	public boolean isWrapperFor(final Class<?> iface) throws SQLException {
		return this.stmt.isWrapperFor(iface);
	}

	@Override
	public void setCursorName(final String name) throws SQLException {
		this.stmt.setCursorName(name);
	}

	@Override
	public void setEscapeProcessing(final boolean enable) throws SQLException {
		this.stmt.setEscapeProcessing(enable);
	}

	@Override
	public void setFetchDirection(final int direction) throws SQLException {
		this.stmt.setFetchDirection(direction);
	}

	@Override
	public void setFetchSize(final int rows) throws SQLException {
		this.stmt.setFetchSize(rows);
	}

	@Override
	public void setLargeMaxRows(final long max) throws SQLException {
		this.stmt.setLargeMaxRows(max);
	}

	@Override
	public void setMaxFieldSize(final int max) throws SQLException {
		this.stmt.setMaxFieldSize(max);
	}

	@Override
	public void setMaxRows(final int max) throws SQLException {
		this.stmt.setMaxRows(max);
	}

	@Override
	public void setPoolable(final boolean poolable) throws SQLException {
		this.stmt.setPoolable(poolable);
	}

	@Override
	public void setQueryTimeout(final int seconds) throws SQLException {
		this.stmt.setQueryTimeout(seconds);
	}

	@Override
	public <T> T unwrap(final Class<T> iface) throws SQLException {
		return this.stmt.unwrap(iface);
	}
}
