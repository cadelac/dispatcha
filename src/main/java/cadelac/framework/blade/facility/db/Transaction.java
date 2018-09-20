package cadelac.framework.blade.facility.db;

import java.sql.SQLException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
public class Transaction {
	
	public Transaction(final DbConfigInfo dbConfigInfo_) {
		_dbConfigInfo = dbConfigInfo_;
		_dbCommConnection = null;
	}

	public void execute(final TransactionScript script_) throws Exception {
		connect();

		try {
			script_.run(_dbCommConnection);
			_dbCommConnection.getConnection().commit();
		}
		catch (SQLException e) {
			logger.warn(String.format("transaction exception: rolling back\n%s", e.getMessage()));
			_dbCommConnection.getConnection().rollback();
		}
		finally {
			dbClose();
		}
	}
	
	private void connect() throws Exception {
		_dbCommConnection = DbCommConnection.connect(_dbConfigInfo);
		_dbCommConnection.getConnection().setAutoCommit(false);
	}
	
	private void dbClose() throws Exception {
		_dbCommConnection.getConnection().close();
	}

	
	private static final Logger logger = LogManager.getLogger(Transaction.class);
	
	private final DbConfigInfo _dbConfigInfo;
	private DbCommConnection _dbCommConnection;
}
