package cadelac.framework.blade.facility.db;

import cadelac.framework.blade.comm.CommCredentials;

public interface DbCommCredentials extends CommCredentials {
	
	String getAccountName();
	void setAccountName(String accountName);
	
	String getPassword();
	void setPassword(String password);
}
