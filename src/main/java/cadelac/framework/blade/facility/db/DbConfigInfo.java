package cadelac.framework.blade.facility.db;

import cadelac.framework.blade.core.config.ConfigSetting;

/**
 * Contains database configuration information
 * @author cadelac
 *
 */
public interface DbConfigInfo extends ConfigSetting {

	public static final String DB_CONFIG_PROPERTY = "db-config-file";
	
	public String getDatabaseServerType();
	public void setDatabaseServerType(String databaseServerType);
	
	public String getDatabaseServer();
	public void setDatabaseServer(String databaseServer);

	public String getDatabaseName();
	public void setDatabaseName(String databaseName);

	public String getDatabaseUser();
	public void setDatabaseUser(String databaseUser);

	public String getDatabasePassword();
	public void setDatabasePassword(String databasePassword);
	
}
