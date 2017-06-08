package cadelac.framework.blade.facility.db.operation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cadelac.framework.blade.facility.db.connection.DbCommConnection;


/**
 * Class for a single insert that returns a single generated key (AUTO INCREMENT) as a result of the insert.
 * @author cadelac
 *
 */
public abstract class KeyedInsert<T> implements Query<T> {
	
	@Override
	public KeyedInsert<T> execute(DbCommConnection connection_, T param_) throws SQLException, InstantiationException, IllegalAccessException {
       _statement = connection_.getConnection().createStatement();
       _statement.executeUpdate(createQuery(param_), Statement.RETURN_GENERATED_KEYS);
       _resultSet = _statement.getGeneratedKeys();
       return this;
	}
	
	public abstract String createQuery(T param_);
	public abstract int getKeyColumnIndex();

	public int getGeneratedKey() throws SQLException {
		final int key = _resultSet.getInt(getKeyColumnIndex());
		_resultSet.close();
	    _statement.close();
	    return key;
	}
	
	protected Statement _statement;
	protected ResultSet _resultSet;
}
