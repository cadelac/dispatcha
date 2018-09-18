package cadelac.framework.blade.facility.db;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
	
	private static final Logger logger = LogManager.getLogger(QueryBase.class);
}
