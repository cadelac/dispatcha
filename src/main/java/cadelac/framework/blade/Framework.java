package cadelac.framework.blade;

import cadelac.framework.blade.app.ApplicationSimple;
import cadelac.framework.blade.core.CommandSwitch;
import cadelac.framework.blade.core.PropertiesManager;
import cadelac.framework.blade.core.Store;
import cadelac.framework.blade.core.code.compiler.DynamicCompiler;
import cadelac.framework.blade.core.dispatch.Patch;
import cadelac.framework.blade.core.monitor.Monitor;
import cadelac.framework.blade.core.object.ObjectFactory;
import cadelac.framework.blade.core.object.Prototype2ConcreteMap;


public class Framework {
	
	public static CommandSwitch getCommandSwitch() {
		return $arg;
	}

	public static PropertiesManager getPropertiesManager() {
		return _propertiesManager;
	}

	public static void setPropertiesManager(PropertiesManager propertiesManager_) {
		_propertiesManager = propertiesManager_;
	}
	public static Prototype2ConcreteMap getPrototype2ConcreteMap() {
		return _prototype2ConcreteMap;
	}

	public static void setPrototype2ConcreteMap(Prototype2ConcreteMap prototype2ConcreteMap_) {
		_prototype2ConcreteMap = prototype2ConcreteMap_;
	}
	
	public static DynamicCompiler getCompiler() {
		return _compiler;
	}

	public static void setCompiler(final DynamicCompiler compiler_) {
		_compiler = compiler_;
	}
	
	public static ObjectFactory getObjectFactory() {
		return _objectFactory;
	}

	public static void setObjectFactory(ObjectFactory objectFactory_) {
		_objectFactory = objectFactory_;
	}

	public static Monitor getMonitor() {
		return _monitor;
	}

	public static void setMonitor(final Monitor monitor_) {
		_monitor = monitor_;
	}
	
	public static long getBootTime() {
		return _bootTime;
	}

	public static void setBootTime(final long bootTime_) {
		_bootTime = bootTime_;
	}
	
	public static long getQuantum() {
		return _quantum;
	}

	public static void setQuantum(final long quantum_) {
		_quantum = quantum_;
	}
	
	public static int getThreadpoolSize() {
		return _threadpool_size;
	}

	public static void setThreadpoolSize(final int threadpool_size_) {
		_threadpool_size = threadpool_size_;
	}
	
	public static int getScheduledThreadpoolSize() {
		return _scheduled_threadpool_size;
	}

	public static void setScheduledThreadpoolSize(final int scheduled_threadpool_size_) {
		_scheduled_threadpool_size = scheduled_threadpool_size_;
	}
	
	public static ApplicationSimple getApplication() {
		return _application;
	}

	public static void setApplication(final ApplicationSimple application_) {
		_application = application_;
	}


	
	// CONSTANT VALUES
	public static final String LOGICAL_TRUE		= "true";
	public static final String LOGICAL_FALSE		= "false";
	public static final String EXCEPTION			= "exception";
	
	
	private static ApplicationSimple _application;
	private static PropertiesManager _propertiesManager;
	private static Prototype2ConcreteMap _prototype2ConcreteMap;
	private static DynamicCompiler _compiler;
	private static ObjectFactory _objectFactory;
	private static Monitor _monitor;
	private static long _bootTime;
	private static long _quantum;
	private static int _threadpool_size;
	private static int _scheduled_threadpool_size; /* for delayed calls */

	

	public static final CommandSwitch $arg = new CommandSwitch();
	public static final Patch $dispatch = new Patch();
	public static final Store $store = Store.create();
}
