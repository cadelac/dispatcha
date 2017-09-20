package cadelac.framework.blade.facility.db;

import java.sql.SQLException;

/**
 * Executable SQL query
 * @author cadelac
 *
 */
public interface ExecutableQuery {
	public String getQuery();
	public ExecutableQuery execute(final DbCommConnection connection_) throws SQLException;
}
