package cadelac.framework.blade.facility.db.operation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import cadelac.framework.blade.facility.db.connection.DbCommConnection;
import cadelac.lib.primitive.exception.FrameworkException;

public abstract class Select<T,U> implements Query<U> {

	@Override
	public Select<T,U> execute(DbCommConnection connection_, U params_) throws SQLException	{
       _statement = connection_.getConnection().createStatement(); 
       _resultSet = _statement.executeQuery(createQuery(params_));
       return this;
	}
	
	@Override
	public Select<T,U> execute(DbCommConnection connection_) throws SQLException	{
       _statement = connection_.getConnection().createStatement(); 
       _resultSet = _statement.executeQuery(getQuery());
       return this;
	}
	
	public abstract String createQuery(U params_);
	
	public String getQuery() {
		return _query;
	}
	public void setQuery(U params_) {
		_query = createQuery(params_);
	}
	
	public abstract List<T> getResults()  throws //InstantiationException, IllegalAccessException, SQLException, 
	FrameworkException, Exception;

	
	protected Statement _statement;
	protected ResultSet _resultSet;
	protected String _query;
}
