package cadelac.framework.blade.facility.db;

import cadelac.framework.blade.core.state.StateBase;

public class SerializedTransactionState 
		extends StateBase {

	public SerializedTransactionState(
			final String stateId_
			, final DbConfigInfo dbConfigInfo_) 
					throws Exception {
		super(stateId_);
		_dbConfigInfo = dbConfigInfo_;
		_dbCommConnection = DbCommConnection.connect(_dbConfigInfo);
		_dbCommConnection.getConnection().setAutoCommit(false);
	}
	
	public DbCommConnection getDbCommConnection() {
		return _dbCommConnection;
	}
	public DbConfigInfo getDbConfigInfo() {
		return _dbConfigInfo;
	}
	
	private final DbConfigInfo _dbConfigInfo;
	private final DbCommConnection _dbCommConnection;
}
