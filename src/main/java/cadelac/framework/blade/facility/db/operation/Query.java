package cadelac.framework.blade.facility.db.operation;

import java.sql.SQLException;

import cadelac.framework.blade.facility.db.connection.DbCommConnection;

public interface Query<T> {
	public Query<T> execute(DbCommConnection connection_, T params_) 
			throws SQLException, InstantiationException, IllegalAccessException;
	public Query<T> execute(DbCommConnection connection_) 
			throws SQLException, InstantiationException, IllegalAccessException;
}
