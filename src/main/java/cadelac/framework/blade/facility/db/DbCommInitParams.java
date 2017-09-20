package cadelac.framework.blade.facility.db;

import cadelac.framework.blade.comm.CommCredentials;
import cadelac.framework.blade.comm.CommInitParams;
import cadelac.framework.blade.comm.CommUrl;

public class DbCommInitParams implements CommInitParams {

	public DbCommInitParams(final DbCommUrl commUrl_, final CommCredentials commCredentials_) {
		this._commUrl = commUrl_;
		this._commCredentials = commCredentials_;
	}
	
	@Override
	public CommUrl getCommUrl() {
		return this._commUrl;
	}


	@Override
	public CommCredentials getCommCredentials() {
		return this._commCredentials;
	}

	private final DbCommUrl _commUrl;
	private final CommCredentials _commCredentials;
}
