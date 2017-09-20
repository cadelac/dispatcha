package cadelac.framework.blade.facility.db;

import cadelac.framework.blade.comm.CommUrl;

public class DbCommUrl implements CommUrl {

	public DbCommUrl(final String dbServer_, final String dbName_)  {
		this._dbServer = dbServer_;
		this._dbName = dbName_;
	}
	
	public String getDbServer() {
		return this._dbServer;
	}
	public String getDbName() {
		return this._dbName;
	}
	
	@Override
	public boolean isValid() {		
		return this._dbServer != null && !this._dbServer.isEmpty() && this._dbName != null && !this._dbName.isEmpty();
	}
	
	private final String _dbServer;
	private final String _dbName;
}
