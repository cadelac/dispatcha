package cadelac.framework.blade.app;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cadelac.framework.blade.Framework;
import cadelac.framework.blade.comm.rpc.RpcTicket;
import cadelac.framework.blade.core.BootMsg;
import cadelac.framework.blade.core.CommandSwitch;
import cadelac.framework.blade.core.Identified;
import cadelac.framework.blade.core.IdentifiedBase;
import cadelac.framework.blade.core.Log;
import cadelac.framework.blade.core.PropertiesManager;
import cadelac.framework.blade.core.Utilities;
import cadelac.framework.blade.core.code.compiler.DefaultCompiler;
import cadelac.framework.blade.core.config.ConfigParameters;
import cadelac.framework.blade.core.config.Configurator;
import cadelac.framework.blade.core.dispatch.Dispatch;
import cadelac.framework.blade.core.dispatch.Push;
import cadelac.framework.blade.core.dispatch.PushBase;
import cadelac.framework.blade.core.exception.FrameworkException;
import cadelac.framework.blade.core.exception.InitializationException;
import cadelac.framework.blade.core.message.Message;
import cadelac.framework.blade.core.monitor.MonitorSimple;
import cadelac.framework.blade.core.object.ObjectFactorySimple;
import cadelac.framework.blade.core.object.Prototype2ConcreteMapSimple;
import cadelac.framework.blade.core.state.StateLess;


public abstract 
class ApplicationSimple extends IdentifiedBase implements Application, Identified 
{
	public ApplicationSimple(final String applicationName_, final String[] arguments_) {
		super(applicationName_);
		_arguments = arguments_;
		
	}
	
	public void start() throws FrameworkException, Exception {
		// boot up the framework
		boot(); 
		logger.info("booted up framework");
		
		// initialize the application
		logger.info(String.format("initializing application [%s]", getId()));
		init();
	}
	
	private void boot() throws Exception {
		// basic logger configuration
		Log.configure();
		
		Framework.setApplication(this);

		Framework.setRpcTable(new ConcurrentHashMap<Long,RpcTicket<? extends Message>>());
		
		// capture command line arguments/switches
		final CommandSwitch commandSwitch = new CommandSwitch();
		commandSwitch.populate(_arguments);
		Framework.setCommandSwitch(commandSwitch);
		
		// get full path of properties file
		final String propertiesPathArg = commandSwitch.getArgument(ConfigParameters.PROPERTIES_PATH_ARG);
		if (propertiesPathArg == null)
			throw new InitializationException("Properties full path must not be empty");
		Framework.setPropertiesFullPathName(propertiesPathArg);
		
		// create properties manager
		final PropertiesManager pm = new PropertiesManager(propertiesPathArg);
		Framework.setPropertiesManager(pm);
		
		// configure logger
		final Properties props = pm.getProperties();
		final String loggerConfigFile = props.getProperty(ConfigParameters.LOGGER_CONFIG_FILE_PATH);
		if (loggerConfigFile != null) {
			PropertyConfigurator.configure(loggerConfigFile);
			logger.info("configured logger with log file configuration");
		}
		
		// create and set compiler
		final String argRepo = commandSwitch.getArgument(ConfigParameters.COMPILER_USER_DIR);
		final String compilerUserDir = props.getProperty(ConfigParameters.COMPILER_USER_DIR);
		if (argRepo != null && !argRepo.isEmpty())
			Framework.setCompiler(new DefaultCompiler(argRepo));
		else if (compilerUserDir != null && !compilerUserDir.isEmpty())
			Framework.setCompiler(new DefaultCompiler(compilerUserDir));
		else
			Framework.setCompiler(new DefaultCompiler());
		
		// create and set object look up
		Framework.setPrototype2ConcreteMap(new Prototype2ConcreteMapSimple());
		
		// create and set object factory
		Framework.setObjectFactory(new ObjectFactorySimple());
		
		Framework.setMonitor(new MonitorSimple());

		Framework.setQuantum(calculateQuantum());
		
		Framework.setThreadpoolSize(getThreadPoolSize());
		
		Framework.setScheduledThreadpoolSize(getScheduledThreadPoolSize());
		
		Framework.setMessageDigestIsEnabled(Configurator.getMessageDigestIsEnabled());
		
		// complete boot process by sending a message (prevents application from exiting)
		final Push<BootMsg,StateLess> bootPushHandler = 
				new PushBase<BootMsg,StateLess>(
						"BootHandler"
						, (msg_,state) -> {
							if (Framework.getBootTime()!=0) {
								final long bootTime = Utilities.getTimestamp();
								Framework.setBoottime(bootTime);
								logger.info(String.format("booting: timestamp: %d", bootTime));
							}
						}
						, m -> StateLess.STATELESS_STATE_ID
						, m -> StateLess.STATELESS_STATE
				);
		Dispatch.bind(BootMsg.class, bootPushHandler);
		Dispatch.push(Framework.getObjectFactory().fabricate(
				BootMsg.class
				, p -> {
					p.setBootTime(Utilities.getTimestamp());
				}));
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
