package cadelac.framework.blade.core.component.shell;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cadelac.framework.blade.code.CodeGenHelper;
import cadelac.framework.blade.core.Framework;
import cadelac.framework.blade.core.component.app.Application;
import cadelac.framework.blade.core.component.rack.Rack;
import cadelac.framework.blade.core.component.rack.RackSimple;
import cadelac.framework.blade.core.service.dispatch.Dispatcher;
import cadelac.framework.blade.core.service.object.ObjectFactorySimple;
import cadelac.framework.blade.custom.RequestRouteHandler;
import cadelac.framework.blade.custom.SendRouteHandler;
import cadelac.framework.blade.handler.Handler;
import cadelac.framework.blade.pack.Pack;
import cadelac.framework.blade.pack.PackBase;
import cadelac.framework.blade.plug.OutputPlug;
import cadelac.lib.primitive.arg.Arg;
import cadelac.lib.primitive.arg.ArgSimple;
import cadelac.lib.primitive.code.DefaultCompiler;
import cadelac.lib.primitive.code.DynamicCompiler;
import cadelac.lib.primitive.concept.Containable;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.exception.FrameworkException;
import cadelac.lib.primitive.exception.InitializationException;
import cadelac.lib.primitive.monitor.Monitor;
import cadelac.lib.primitive.monitor.MonitorSimple;
import cadelac.lib.primitive.object.ObjectFactory;
import cadelac.lib.primitive.object.Prototype2ConcreteMap;
import cadelac.lib.primitive.object.Prototype2ConcreteMapSimple;
import cadelac.lib.primitive.property.PropertiesManager;


public class ShellSimple implements Shell {

	public static final String PROPERTIES_PATH_ARG = "properties.path";
	public static final String LOGGER_CONFIG_FILE_PATH = "logger.config.file";
	public static final String THREAD_POOL_SIZE = "thread.pool.size";
	public static final String QUANTUM_LENGTH = "quantum";
	public static final String COMPILER_USER_DIR = "compiler.user.dir";	
	
	public static final long DEFAULT_QUANTUM = 120000L;

	public static final String DEFAULT_SEND_ROUTE_HANDLER = "defaultSendRouteHandler";
	public static final String DEFAULT_REQUEST_ROUTE_HANDLER = "defaultRequestRouteHandler";
	
	public ShellSimple(final String[] args_) {
		_args = args_;
		_rack = null;
		_application = null;
	}
	
	@Override
	public Rack getRack() {
		return _rack;
	}
	@Override
	public void setRack(final Rack rack_) {
		_rack = rack_;
	}


	@Override
	public Application getApplication() {
		return _application;
	}
	@Override
	public void setApplication(Application application_)  {
		_application = application_;
	}


	@Override
	public void registerClass(final Class<? extends Message> protoClass_) throws Exception {

		final Package pkg = protoClass_.getPackage();
		final String packageName = pkg.getName();
		final String concreteClassName = protoClass_.getSimpleName() + "Simple";
		
		/* generate code */
		final String sourceCode = CodeGenHelper.generateSourceCode(protoClass_);
		logger.debug("generated code for concrete class " + packageName + "." + concreteClassName + " for prototype " + protoClass_.getSimpleName());
		final DynamicCompiler compiler = _rack.getCompiler();
		
		/* create source file */
		final File sourceFile = CodeGenHelper.writeSourceFile(compiler.getSourceDir(), compiler.getClassDir(), concreteClassName, sourceCode);		

		/* compile file */
		logger.debug("compiling concrete class " + packageName + "." + concreteClassName + " for prototype " + protoClass_.getSimpleName());
		compiler.compile(sourceFile);

		/* load the class */
		URL url = compiler.getClassDir().toURI().toURL();
		URLClassLoader classLoader = new URLClassLoader(new URL[] {url});
		@SuppressWarnings("unchecked")
		final Class<? extends Message> aClass = (Class<? extends Message>) classLoader.loadClass(packageName + "." + concreteClassName);
		logger.debug("loaded concrete class " + packageName + "." + concreteClassName + " for prototype " + protoClass_.getSimpleName());
		classLoader.close();
		/* install loaded class into map */
		getRack().getPrototype2ConcreteMap().put(protoClass_, aClass);
	}

	@Override
	public void start()   {
		try {
			initShell();
			
			if (_rack == null)
				throw new InitializationException("Rack is null");
			
			if (_application == null)
				throw new InitializationException("Application is null");
			
			/* register classes */
			logger.info("registering classes");
			_application.registerClasses();
			
			/* initialize application */
			
			final Pack rootPack = _rack.getRootPack();		
			final SendRouteHandler defaultSendRouteHandler = new SendRouteHandler(DEFAULT_SEND_ROUTE_HANDLER);
			rootPack.addContainable(defaultSendRouteHandler);
			Framework.setSendRouteHandler(defaultSendRouteHandler);

			final RequestRouteHandler defaultRequestRouteHandler = new RequestRouteHandler(DEFAULT_REQUEST_ROUTE_HANDLER);
			rootPack.addContainable(defaultRequestRouteHandler);
			Framework.setRequestRouteHandler(defaultRequestRouteHandler);
			
			
			logger.info("initializing application [" + _application.getId() + "]");
			_application.init();
			
			/* check all output plugs are connected */
			checkPlugs(_rack.getRootPack());
		
		} 
		catch (FrameworkException e_) {
			logger.error("Framework Exception: " + e_.getMessage() + "\nStacktrace:\n" + e_.getStringStackTrace());
		} 
		catch (Exception e_) {
			logger.error("Exception: " + e_.getMessage() + "\nStacktrace:\n" + FrameworkException.getStringStackTrace(e_));
		}
	}

	
	public String getMandatoryProperty(final String property_) throws InitializationException {
		final Rack rack = getRack();
		final Arg arg = rack.getArg();
		final Properties props = rack.getPropertiesManager().getProperties();
		String value = arg.getArgument(property_);
		if (value == null && (value = props.getProperty(property_)) == null)
			throw new InitializationException("property [" + property_ + "] not found as command-line argument or property in configuration file.");
		return value;
	}
	
	
	protected void initShell() throws FrameworkException, Exception {
		
		/* create logger */
		final Logger logger = Logger.getLogger(Shell.class);
		/* configure logger with basic configuration */
		BasicConfigurator.configure();
		logger.info("configured logger with basic configuration");
		
		/* create rack */
		final Rack rack = new RackSimple();
		setRack(rack);
		
		/* capture command line arguments */
		final Arg arg = new ArgSimple();
		arg.populate(_args);
		rack.setArg(arg);
		
		/* get full path of properties file */
		final String propertiesPathArg = rack.getArg().getArgument(PROPERTIES_PATH_ARG);
		if (propertiesPathArg == null)
			throw new InitializationException("Properties full path must not be empty");
		rack.setPropertiesFullPathName(propertiesPathArg);
		
		/* create PropertiesManager */
		final PropertiesManager pm = new PropertiesManager(propertiesPathArg);
		rack.setPropertiesManager(pm);
		
		/* configure logger */
		final Properties props = pm.getProperties();
		final String loggerConfigFile = props.getProperty(LOGGER_CONFIG_FILE_PATH);
		if (loggerConfigFile != null) {
			PropertyConfigurator.configure(loggerConfigFile);
			logger.info("configured logger with log file configuration");
		}
		
		/* create compiler */
		final String argRepo = rack.getArg().getArgument(COMPILER_USER_DIR);
		final String compilerUserDir = props.getProperty(COMPILER_USER_DIR);
		if (argRepo != null && !argRepo.isEmpty())
			rack.setCompiler(new DefaultCompiler(argRepo));
		else if (compilerUserDir != null && !compilerUserDir.isEmpty())
			rack.setCompiler(new DefaultCompiler(compilerUserDir));
		else
			rack.setCompiler(new DefaultCompiler());
//		final DynamicCompiler compiler = (compilerUserDir == null) ? new DefaultCompiler() : new DefaultCompiler(compilerUserDir);
//		rack.setCompiler(compiler);
		
		/* create mapping from prototye class to its concrete class */
		Prototype2ConcreteMap typeMap = new Prototype2ConcreteMapSimple();
		rack.setPrototype2ConcreteMap(typeMap);
		
		/* create object factory */
		ObjectFactory objectFactory = new ObjectFactorySimple(typeMap);
		//rack.setObjectFactory(objectFactory);
		Framework.setObjectFactory(objectFactory);

		/* create and set root pack */
		final Pack rootPack = new PackBase("rootPack");
		rack.setRootPack(rootPack);
		
		/* create monitor */
		final Monitor monitor = new MonitorSimple();
		rack.setMonitor(monitor);
		
		/* calculate quantum */
		calculateQuantum();
		
		/* initialize Dispatcher */
		Dispatcher.init();
	}
	
	
	protected void checkPlugs(final Pack pack_) {
		for (Containable containable : pack_.getAllContainables()) {
			if (containable instanceof Handler) {
				Handler<?,?> handler = (Handler<?,?>) containable;
				Collection<OutputPlug> plugs = handler.getOutputPlugs();
				for (OutputPlug op : plugs) {
					if (op.getInputPlug() == null) {
						logger.warn("output plug ["+ handler.getId() + "." + op.getId() +"] is not connected");
					}
				}
			}
			else if (containable instanceof Pack) {
				checkPlugs((Pack)containable);
			}
		}
	}
	
	protected void calculateQuantum() {
		long quantum = DEFAULT_QUANTUM;
		
		String quantumStr = _rack.getArg().getArgument(QUANTUM_LENGTH);
		if (quantumStr != null)
			// get value from command line argument
			quantum = Long.parseLong(quantumStr);
		else {
			// get value from configuration file
			final Properties props = _rack.getPropertiesManager().getProperties();
			if ((quantumStr = props.getProperty(QUANTUM_LENGTH)) != null)
				quantum = Long.parseLong(quantumStr);
		}
		
		_rack.setQuantum(quantum);
		logger.debug("quantum set to (milliseconds) " + quantum);
	}

	
	private static final Logger logger = Logger.getLogger(Shell.class);
	
	private final String[] _args;
	private Rack _rack;
    private Application _application;


}
