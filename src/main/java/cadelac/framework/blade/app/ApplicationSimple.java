package cadelac.framework.blade.app;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cadelac.framework.blade.Framework;
import cadelac.framework.blade.core.Identified;
import cadelac.framework.blade.core.IdentifiedBase;
import cadelac.framework.blade.core.PropertiesManager;
import cadelac.framework.blade.core.Utilities;
import cadelac.framework.blade.core.code.compiler.DefaultCompiler;
import cadelac.framework.blade.core.code.compiler.DynamicCompiler;
import cadelac.framework.blade.core.config.ConfigParameters;
import cadelac.framework.blade.core.exception.ArgumentException;
import cadelac.framework.blade.core.exception.FrameworkException;
import cadelac.framework.blade.core.exception.InitializationException;
import cadelac.framework.blade.core.monitor.Monitor;
import cadelac.framework.blade.core.object.ObjectFactory;
import cadelac.framework.blade.core.object.Prototype2ConcreteMapSimple;
import static cadelac.framework.blade.Framework.$dispatch;


public abstract class ApplicationSimple 
	extends IdentifiedBase implements Application, Identified 
{
	public ApplicationSimple(final String applicationName_, final String[] arguments_) {
		super(applicationName_);
		_arguments = arguments_;
	}
	
	public void start() 
			throws FrameworkException, Exception {
		// boot up the framework
		boot(); 
		logger.info("booted up framework");
		
		// initialize the application
		logger.info(String.format("initializing application [%s]", getId()));
		init();
	}
	
	private void boot() 
			throws Exception {
		
		// basic logger configuration
		BasicConfigurator.configure();
		logger.info("configured logger with basic configuration");
		
		Framework.setApplication(this);

		captureCommandSwitches();
		
		configureProperties();

		configureLogger();

		configureCompiler();

		// create and set object look up
		Framework.setPrototype2ConcreteMap(new Prototype2ConcreteMapSimple());
		
		// create and set object factory
		Framework.setObjectFactory(ObjectFactory.create());
		
		Framework.setMonitor(Monitor.create());

		Framework.setQuantum(calculateQuantum());
		
		Framework.setThreadpoolSize(getThreadPoolSize());
		
		Framework.setScheduledThreadpoolSize(getScheduledThreadPoolSize());
		
		primeThePump();
	}


	private void configureProperties() 
			throws ArgumentException, IOException {
		// get full path of properties file
		final String propertiesPathArg = 
				Framework.getCommandSwitch().getArgument(
						ConfigParameters.PROPERTIES_PATH_ARG);
		if (propertiesPathArg != null && !propertiesPathArg.isEmpty()) {
			// create properties manager
			Framework.setPropertiesManager(
					new PropertiesManager(propertiesPathArg));
		}
	}
	
	private void configureLogger() 
			throws Exception {
		final String loggerConfigFile = getLoggerConfigFile();
		PropertyConfigurator.configure(loggerConfigFile);
		logger.info(String.format(
				"configured logger with: [%s]"
				, loggerConfigFile));
	}
	
	
	private String getLoggerConfigFile() 
			throws Exception {
		String loggerConfigFile = 
				Framework.getCommandSwitch().getArgument(
						ConfigParameters.LOGGER_CONFIG_FILE_PATH);
		if (loggerConfigFile != null && !loggerConfigFile.isEmpty())
			return loggerConfigFile;
		
		final Properties props = 
				Framework.getPropertiesManager().getProperties();
		loggerConfigFile = props.getProperty(
				ConfigParameters.LOGGER_CONFIG_FILE_PATH);
		if (loggerConfigFile != null && !loggerConfigFile.isEmpty()) 
			return loggerConfigFile;
		
		throw new InitializationException(String.format(
				"logger not configured; provide command-line argument or property: %s"
				, ConfigParameters.LOGGER_CONFIG_FILE_PATH));
	}


	private void configureCompiler() {
		String compilerRepo = 
				Framework.getCommandSwitch().getArgument(
						ConfigParameters.COMPILER_USER_DIR);
		if (compilerRepo != null && !compilerRepo.isEmpty())
			Framework.setCompiler(new DefaultCompiler(compilerRepo));
		else {
			PropertiesManager props = Framework.getPropertiesManager();
			if (props != null) {
				compilerRepo = props.getProperties().getProperty(
						ConfigParameters.COMPILER_USER_DIR);
				if (compilerRepo != null && !compilerRepo.isEmpty())
					Framework.setCompiler(new DefaultCompiler(compilerRepo));
			}
		}
		DynamicCompiler compiler = Framework.getCompiler();
		if (compiler == null) {
			// if still not set, use default compiler
			Framework.setCompiler(new DefaultCompiler());
		}	
	}
	
	private void primeThePump()
			throws Exception {
		// complete boot process by sending a message (prevents application from exiting)
		$dispatch.push(() -> {
			Framework.setBootTime(Utilities.getTimestamp());
			logger.info(String.format(
					"booting: timestamp: %d"
					, Framework.getBootTime()));
		});
	}

	private void captureCommandSwitches() throws ArgumentException {
		Framework.$arg.populate(_arguments);
	}
	
	private long calculateQuantum() {
		long quantum = ConfigParameters.DEFAULT_QUANTUM;
		
		String quantumStr = Framework.getCommandSwitch().getArgument(
				ConfigParameters.PROP_QUANTUM_DURATION);
		if (quantumStr != null)
			// get value from command line argument
			quantum = Long.parseLong(quantumStr);
		else {
			// get value from configuration file
			final PropertiesManager props = Framework.getPropertiesManager();
			if (props != null 
					&& (quantumStr = props.getProperties().getProperty(
							ConfigParameters.PROP_QUANTUM_DURATION)) != null)
				quantum = Long.parseLong(quantumStr);
		}
		logger.debug("quantum set to (milliseconds) " + quantum);
		return quantum;
	}
	
	private int getThreadPoolSize() {
		int tps = ConfigParameters.DEFAULT_THREADPOOL_SIZE;
		String threadPoolSize = 
				Framework.getCommandSwitch().getArgument(
						ConfigParameters.THREADPOOL_SIZE);
		if (threadPoolSize!=null) {
			tps = Integer.parseInt(threadPoolSize);
		}
		else {
			final PropertiesManager props = Framework.getPropertiesManager();
			if (props != null
					&& (threadPoolSize =
							props.getProperties().getProperty(
									ConfigParameters.THREADPOOL_SIZE)) != null) {
				tps = Integer.parseInt(threadPoolSize);
			}
		}
		logger.debug("thread pool size set to " + tps);
		return tps;
	}
	
	private int getScheduledThreadPoolSize() {
		int tps = ConfigParameters.DEFAULT_THREADPOOL_SCHEDULED_SIZE;
		String threadPoolSize = 
				Framework.getCommandSwitch().getArgument(
						ConfigParameters.THREADPOOL_SCHEDULED_SIZE);
		if (threadPoolSize!=null) {
			tps = Integer.parseInt(threadPoolSize);
		}
		else {
			final PropertiesManager props = Framework.getPropertiesManager();
			if (props != null
					&& (threadPoolSize =
					props.getProperties().getProperty(
							ConfigParameters.THREADPOOL_SCHEDULED_SIZE)) != null) {
				tps = Integer.parseInt(threadPoolSize);
			}
		}
		logger.debug("scheduled thread pool size set to " + tps);
		return tps;
	}
	
	private static final Logger logger = Logger.getLogger(ApplicationSimple.class);
	
	
	private final String[] _arguments;
}
