package cadelac.framework.blade.facility.db.operation;

import java.sql.SQLException;
import java.sql.Statement;

import cadelac.framework.blade.facility.db.connection.DbCommConnection;


/**
 * Single insert
 * @author cadelac
 *
 */
public abstract class Insert<T> {

	public void execute(DbCommConnection connection_, T param_) 
			throws SQLException, InstantiationException, IllegalAccessException {
       final Statement statement = connection_.getConnection().createStatement();
       statement.executeUpdate(createQuery(param_));
	}
	public void execute(DbCommConnection connection_) 
			throws SQLException, InstantiationException, IllegalAccessException {
       final Statement statement = connection_.getConnection().createStatement();
       statement.executeUpdate(getQuery());
	}
	
	public String getQuery() {
		return _query;
	}
	public void setQuery(T params_) {
		_query = createQuery(params_);
	}
	
	public abstract String createQuery(T param_);
	
	private String _query;
}
