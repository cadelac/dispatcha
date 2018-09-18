package cadelac.framework.blade.facility.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import cadelac.framework.blade.comm.AbstractCommConnection;
import cadelac.framework.blade.comm.CommCredentials;
import cadelac.framework.blade.comm.CommUrl;
import cadelac.framework.blade.core.config.Configurator;
import cadelac.framework.blade.core.exception.ArgumentException;
import cadelac.framework.blade.core.exception.FrameworkException;
import cadelac.framework.blade.core.exception.InitializationException;

public abstract class DbCommConnection extends AbstractCommConnection {

	public DbCommConnection(final String name) {
		super(name);
		this._connection = null;
		this._isInitialized = false;
	}

	@Override
	public void disconnect() throws FrameworkException, IOException  {
		if (this._connection != null) {
			try {
				this._connection.close();
				setIsConnected(false);
				logger.info("Disconnected from DB [" + _url.getDbName() + "]");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean getIsInitialized() {
		return this._isInitialized;
	}

	public void init(final DbCommInitParams params_) throws FrameworkException {
		if (params_ == null)
			throw new ArgumentException(String.format("Cannot initialize [%s]; argument [%s] is null", DbCommConnection.class.getSimpleName(), DbCommInitParams.class.getSimpleName()));
		
		if (getId() == null || getId().isEmpty())
			throw new ArgumentException("Cannot initialize DbCommConnection; name is null or empty");
		
		if (getIsInitialized())
			throw new ArgumentException("Cannot initialize DbCommConnection [" + getId() + "]; it has already been initialized");
		
		// no checking required. DbCommInitParams is guaranteed to contain a DbCommUrl
		this._url = (DbCommUrl) params_.getCommUrl();
		if (_url == null)
			throw new ArgumentException("Cannot initialize DbCommConnection [" + getId() + "]; CommUrl is null");

		if (!_url.isValid())
			throw new ArgumentException("Cannot initialize DbCommConnection [" + getId() + "]; CommUrl [" + this._url.toString() + "] is malformed");

		this._credentials = params_.getCommCredentials();		
		setIsInitialized(true);	
	}
	
	
	public void connect() throws SQLException, FrameworkException {		
		final String connect_string = createConnectString();
		_connection = DriverManager.getConnection(connect_string);
		if (_connection != null) {
			setIsConnected(true);
//			logger.debug("Connected to DB [" + _url.getDbName() + "]");
		}
	}
	
	public Connection getConnection() {
		return _connection;
	}
	
	public static DbCommConnection connect(final DbConfigInfo dbConfigInfo) 
			throws Exception {

		final DbCommConnection dbCommConnection = Configurator.createDbCommConnection(dbConfigInfo);
		final DbCommInitParams dbCommInitParams = Configurator.createDbCommInitParams(dbConfigInfo);
		
		dbCommConnection.init(dbCommInitParams);
		dbCommConnection.connect();
		if (dbCommConnection.getIsConnected()) {
			return dbCommConnection;
		}
		else{
			final InitializationException except = new InitializationException(
					String.format("unable to connect to database [%s]", dbConfigInfo.getDatabaseName()));
			logger.warn(except.getMessage());
			throw except;
		}	
	}
	
	public static DbCommConnection connect(final String propertyName_) 
			throws Exception {
		return  connect(Configurator.getDatabaseConfigurationInformation(propertyName_));
	}
	
	
	protected void setIsInitialized(boolean isInitialized_) {
		this._isInitialized = isInitialized_;
	}
	
	protected CommUrl getCommUrl() {
		return this._url;
	}
	
	protected CommCredentials getCommCredentials() {
		return this._credentials;
	}

	abstract protected String createConnectString();

	
    protected static void handleErrors(Exception ex_) {
        logger.warn("Exception: " + ex_.getMessage());
    } 
    
    private static final Logger logger = LogManager.getLogger(DbCommConnection.class);
    
    private DbCommUrl _url;
    private CommCredentials _credentials;
	private Connection _connection;
	private boolean _isInitialized;
}
