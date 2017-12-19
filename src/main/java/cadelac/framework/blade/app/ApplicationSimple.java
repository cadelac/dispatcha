package cadelac.framework.blade.app;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cadelac.framework.blade.Framework;
import cadelac.framework.blade.core.BootMsg;
import cadelac.framework.blade.core.CommandSwitch;
import cadelac.framework.blade.core.Identified;
import cadelac.framework.blade.core.IdentifiedBase;
import cadelac.framework.blade.core.PropertiesManager;
import cadelac.framework.blade.core.Utilities;
import cadelac.framework.blade.core.code.compiler.DefaultCompiler;
import cadelac.framework.blade.core.config.ConfigParameters;
import cadelac.framework.blade.core.config.Configurator;
import cadelac.framework.blade.core.dispatch.Dispatch;
import cadelac.framework.blade.core.dispatch.Push;
import cadelac.framework.blade.core.dispatch.PushBase;
import cadelac.framework.blade.core.exception.ArgumentException;
import cadelac.framework.blade.core.exception.FrameworkException;
import cadelac.framework.blade.core.exception.InitializationException;
import cadelac.framework.blade.core.monitor.Monitor;
import cadelac.framework.blade.core.object.ObjectFactory;
import cadelac.framework.blade.core.object.Prototype2ConcreteMapSimple;
import cadelac.framework.blade.core.state.StateLess;


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
		
		Framework.setMessageDigestIsEnabled(Configurator.getMessageDigestIsEnabled());
		
		primeThePump();
	}
	
	private void configureProperties() 
			throws InitializationException, ArgumentException, IOException {
		// get full path of properties file
		final String propertiesPathArg = 
				Framework.getCommandSwitch()
				.getArgument(ConfigParameters.PROPERTIES_PATH_ARG);
		if (propertiesPathArg == null || propertiesPathArg.isEmpty())
			throw new InitializationException("Properties full path must not be empty");
		
		// create properties manager
		Framework.setPropertiesFullPathName(propertiesPathArg);
		Framework.setPropertiesManager(new PropertiesManager(propertiesPathArg));
	}

	private void configureLogger() {
		final Properties props = Framework.getPropertiesManager().getProperties();
		final String loggerConfigFile = props.getProperty(ConfigParameters.LOGGER_CONFIG_FILE_PATH);
		if (loggerConfigFile != null && !loggerConfigFile.isEmpty()) {
			PropertyConfigurator.configure(loggerConfigFile);
			logger.info(String.format("configured logger with: [%s]", loggerConfigFile));
		}
	}
	
	private void configureCompiler() {
		final String argRepo = 
				Framework.getCommandSwitch()
				.getArgument(ConfigParameters.COMPILER_USER_DIR);
		
		final String compilerUserDir = 
				Framework.getPropertiesManager()
				.getProperties()
				.getProperty(ConfigParameters.COMPILER_USER_DIR);
		
		if (argRepo != null && !argRepo.isEmpty())
			Framework.setCompiler(new DefaultCompiler(argRepo));
		else if (compilerUserDir != null && !compilerUserDir.isEmpty())
			Framework.setCompiler(new DefaultCompiler(compilerUserDir));
		else
			Framework.setCompiler(new DefaultCompiler());
	}

	private void primeThePump()
			throws Exception {
		// complete boot process by sending a message (prevents application from exiting)
		Framework.getObjectFactory()
		.fabricate(BootMsg.class)
		.push(() -> {
			Framework.setBootTime(Utilities.getTimestamp());
			logger.info(String.format("booting: timestamp: %d", Framework.getBootTime()));
		});
	}

	private void captureCommandSwitches() throws ArgumentException {
		final CommandSwitch commandSwitch = new CommandSwitch();
		commandSwitch.populate(_arguments);
		Framework.setCommandSwitch(commandSwitch);
	}
	
	private long calculateQuantum() {
		long quantum = ConfigParameters.DEFAULT_QUANTUM;
		
		String quantumStr = Framework.getCommandSwitch().getArgument(ConfigParameters.PROP_QUANTUM_DURATION);
		if (quantumStr != null)
			// get value from command line argument
			quantum = Long.parseLong(quantumStr);
		else {
			// get value from configuration file
			final Properties props = Framework.getPropertiesManager().getProperties();
			if ((quantumStr = props.getProperty(ConfigParameters.PROP_QUANTUM_DURATION)) != null)
				quantum = Long.parseLong(quantumStr);
		}
		logger.debug("quantum set to (milliseconds) " + quantum);
		return quantum;
	}
	
	private int getThreadPoolSize() {
		int tps = ConfigParameters.DEFAULT_THREADPOOL_SIZE;
		String threadPoolSize = Framework.getCommandSwitch().getArgument(ConfigParameters.THREADPOOL_SIZE);
		if (threadPoolSize!=null) {
			tps = Integer.parseInt(threadPoolSize);
		}
		else if ((threadPoolSize =
				Framework.getPropertiesManager().getProperties().getProperty(ConfigParameters.THREADPOOL_SIZE)) != null) {
			tps = Integer.parseInt(threadPoolSize);
		}
		logger.debug("thread pool size set to " + tps);
		return tps;
	}
	
	private int getScheduledThreadPoolSize() {
		int tps = ConfigParameters.DEFAULT_THREADPOOL_SCHEDULED_SIZE;
		String threadPoolSize = Framework.getCommandSwitch().getArgument(ConfigParameters.THREADPOOL_SCHEDULED_SIZE);
		if (threadPoolSize!=null) {
			tps = Integer.parseInt(threadPoolSize);
		}
		else if ((threadPoolSize =
				Framework.getPropertiesManager().getProperties().getProperty(ConfigParameters.THREADPOOL_SCHEDULED_SIZE)) 
				!= null) {
			tps = Integer.parseInt(threadPoolSize);
		}
		logger.debug("scheduled thread pool size set to " + tps);
		return tps;
	}
	
	private static final Logger logger = Logger.getLogger(ApplicationSimple.class);
	
	
	private final String[] _arguments;
}
