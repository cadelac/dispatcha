package cadelac.framework.blade.facility.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Generic base class for select queries.
 * @author cadelac
 *
 * @param <R>
 */
public class SelectQuery<R> 
	extends QueryBase 
		implements ExecutableQuery, Resultable<R> {

	public SelectQuery(
			final QueryBuilder queryBuilder_
			, final Fabricator<ResultSet,R> fabricator_) {
		super(queryBuilder_.buildQuery());
		_fabricator = fabricator_;
	}

	
	@Override
	public SelectQuery<R> execute(final DbCommConnection connection_) 
			throws SQLException {
		_statement = connection_.getConnection().createStatement();
	    final String query = getQuery();
	    getLogger().info(String.format("executing select [\n%s\n]", query));
		_resultSet = _statement.executeQuery(query);
		return this;
	}

	@Override
	public List<R> getResults() throws Exception {
		final List<R> list = new ArrayList<R>();
		while (_resultSet.next()) {
			list.add(_fabricator.fabricate(_resultSet));
		}
		_resultSet.close();
	    _statement.close();		
		return list;
	}

	
	private final Fabricator<ResultSet,R> _fabricator;
	private Statement _statement;
	private ResultSet _resultSet;
}
