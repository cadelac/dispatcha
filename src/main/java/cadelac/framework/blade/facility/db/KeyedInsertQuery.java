package cadelac.framework.blade.facility.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class KeyedInsertQuery<T> extends InsertQuery<T> {

	public KeyedInsertQuery(QueryBuilder queryBuilder_) {
		super(queryBuilder_);
		_resultSet = null;
	}
	
	public int getGeneratedKey() throws SQLException {
		//final int keyColumnIndex = 1; // assume key is always first column (starts at 1)
		//final int key = _resultSet.getInt(keyColumnIndex);
		final int key = _resultSet.getInt("last_insert_rowid()");
		_resultSet.close();
	    _statement.close();
	    return key;
	}
	
	@Override
	public void executeUpdate(
			final Statement statement
			, final String query) 
					throws SQLException {
		statement.executeUpdate(query); //, Statement.RETURN_GENERATED_KEYS);
		_resultSet = statement.getGeneratedKeys();
		_resultSet.getMetaData();//???
	}
	
	private ResultSet _resultSet;
}
