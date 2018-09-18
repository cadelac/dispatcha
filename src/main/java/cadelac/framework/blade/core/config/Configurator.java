package cadelac.framework.blade.core.config;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import cadelac.framework.blade.Framework;
import cadelac.framework.blade.comm.CommCredentials;
import cadelac.framework.blade.comm.websocket.WebsocketConfigInfo;
import cadelac.framework.blade.core.PropertiesManager;
import cadelac.framework.blade.core.Utilities;
import cadelac.framework.blade.core.exception.InitializationException;
import cadelac.framework.blade.core.message.json.JsonFormat;
import cadelac.framework.blade.facility.db.DbCommConnection;
import cadelac.framework.blade.facility.db.DbCommCredentials;
import cadelac.framework.blade.facility.db.DbCommInitParams;
import cadelac.framework.blade.facility.db.DbCommUrl;
import cadelac.framework.blade.facility.db.DbConfigInfo;
import cadelac.framework.blade.facility.db.MysqlDbCommConnection;
import cadelac.framework.blade.facility.db.SqliteDbCommConnection;

/**
 * Contains configuration logic
 * @author cadelac
 *
 */
public class Configurator {
	
	public static <T extends ConfigSetting> T getConfigSetting(
			final Class<T> class_
			, final String propertyName_) 
					throws Exception {
		final PropertiesManager props = Framework.getPropertiesManager();
		if (props == null) {
			throw new InitializationException(String.format(
					"properties not initialized: %s"
					, ConfigParameters.PROPERTIES_PATH_ARG));
		}
		

		final String jsonFilepath = 
				props.getProperties().getProperty(propertyName_);
		return Framework.getObjectFactory().fabricate(
				class_
				, p -> {
					final String jsonText = Utilities.readFile(jsonFilepath);
					logger.debug("Configuration: " + jsonText);
					JsonFormat.decode(p, jsonText);
				}
		);
		
	}
	public static DbConfigInfo getDatabaseConfigurationInformation(
			final String propertyName_)
					throws Exception {
		return getConfigSetting(DbConfigInfo.class, propertyName_);
	}
	
	public static boolean getMessageDigestIsEnabled() {
		final String propertyValue =
				Framework.getPropertiesManager().getProperties().getProperty(
						ConfigParameters.MESSAGE_DIGEST);
		return propertyValue!=null 
				&& !propertyValue.isEmpty() 
				&& Framework.LOGICAL_TRUE.equalsIgnoreCase(propertyValue);
	}
	
	public static DbCommConnection createDbCommConnection(
			final String propertyName_) 
					throws Exception {
		return createDbCommConnection(
				getDatabaseConfigurationInformation(
						propertyName_));
	}
	
	public static DbCommConnection createDbCommConnection(
			final DbConfigInfo dbConfigInfo) 
					throws Exception {
		
		if (ConfigParameters.DATABASE_SERVER_TYPE_SQLITE.equals(
				dbConfigInfo.getDatabaseServerType()))
			return new SqliteDbCommConnection(dbConfigInfo.getDatabaseName());
		
		if (ConfigParameters.DATABASE_SERVER_TYPE_MYSQL.equals(
				dbConfigInfo.getDatabaseServerType())) {
			return new MysqlDbCommConnection(
					dbConfigInfo.getDatabaseName());
		}
			
		throw new InitializationException("Database Server Type must be either mysql or sqlite");
	}
	

	public static DbCommInitParams createDbCommInitParams(
			final DbConfigInfo dbConfigInfo) 
					throws Exception {
		final DbCommUrl commUrl = 
				new DbCommUrl(dbConfigInfo.getDatabaseServer(), dbConfigInfo.getDatabaseName());
		final CommCredentials credentials = Framework.getObjectFactory().fabricate(
				DbCommCredentials.class
				, p -> {
					p.setAccountName(dbConfigInfo.getDatabaseUser());
					p.setPassword(dbConfigInfo.getDatabasePassword());
				}
		);
		return new DbCommInitParams(commUrl, credentials);
	}
	
	
	public static WebsocketConfigInfo getWebsocketConfigurationInformation(
			final String propertyName_) 
					throws Exception {
		return getConfigSetting(WebsocketConfigInfo.class, propertyName_);
	}

	private static final Logger logger = LogManager.getLogger(Configurator.class);
}
