package cadelac.framework.blade.facility.db;

import java.sql.SQLException;
import java.sql.Statement;

public class InsertQuery<P> extends QueryBase implements ExecutableQuery {

	public InsertQuery(QueryBuilder queryBuilder_) {
		super(queryBuilder_.buildQuery());
		_statement = null;
	}

	@Override
	public InsertQuery<P> execute(final DbCommConnection connection_) throws SQLException {
       _statement = connection_.getConnection().createStatement();
       final String query = getQuery();
       getLogger().debug(String.format("executing insert [\n%s\n]", query));
       executeUpdate(_statement, query);
       return this;
	}
	
	public void executeUpdate(
			final Statement statement
			, final String query) 
					throws SQLException {
		statement.executeUpdate(query);
	}
	
	protected Statement _statement;
}
