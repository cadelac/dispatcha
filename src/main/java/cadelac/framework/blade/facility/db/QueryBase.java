package cadelac.framework.blade.facility.db;

import org.apache.log4j.Logger;

/**
 * Base class for Select queries
 * @author cadelac
 *
 */
public abstract class QueryBase implements ExecutableQuery {

	public QueryBase(final String query_) {
		_query = query_;
	}
	
	@Override
	public String getQuery() {
		return _query;
	}

	protected Logger getLogger() {
		return logger;
	}

	private final String _query;
	
	private static final Logger logger = Logger.getLogger(QueryBase.class);
}
