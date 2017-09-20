package cadelac.framework.blade.core.config;

/**
 * enumerates various configuration parameters and values
 * @author cadelac
 *
 */
public class ConfigParameters {

	// framework properties
	public static final String PROPERTIES_PATH_ARG = "properties-path";
	public static final String LOGGER_CONFIG_FILE_PATH = "logger-config-file";
	public static final String PROP_QUANTUM_DURATION = "quantum-duration";
	public static final String COMPILER_USER_DIR = "compiler-user-dir";
	public static final String THREADPOOL_SIZE = "thread-pool-size";
	public static final String THREADPOOL_SCHEDULED_SIZE = "thread-pool-scheduled-size";
	public static final String MESSAGE_DIGEST = "message-digest";
	
	// default values
	public static final long DEFAULT_QUANTUM = 120000L;
	public static final int DEFAULT_THREADPOOL_SIZE = 32;
	public static final int DEFAULT_THREADPOOL_SCHEDULED_SIZE = 8;
	public static final boolean DEFAULT_MESSAGE_DIGEST = false;
	
	// values to choose from
	public static final String DATABASE_SERVER_TYPE_MYSQL = "mysql";
	public static final String DATABASE_SERVER_TYPE_SQLITE = "sqlite";
	
}
