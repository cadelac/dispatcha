package cadelac.framework.blade.core.component.rack;

import cadelac.framework.blade.pack.Pack;
import cadelac.lib.primitive.arg.Arg;
import cadelac.lib.primitive.code.DynamicCompiler;
import cadelac.lib.primitive.monitor.Monitor;
import cadelac.lib.primitive.object.Prototype2ConcreteMap;
import cadelac.lib.primitive.property.PropertiesManager;

public class RackSimple implements Rack {

	public RackSimple() {
		_arg = null;
		_propertiesFullPathName = null;
		_propertiesManager = null;
		_prototype2ConcreteMap = null;
//		_objectFactory = null;
		_compiler = null;
		_rootPack = null;
		_monitor = null;
		_quantum = 0L;
	}


	@Override
	public Arg getArg() {
		return _arg;
	}
	@Override
	public void setArg(final Arg arg_) {
		_arg = arg_;
	}
	
	
	@Override
	public String getPropertiesFullPathName() {
		return _propertiesFullPathName;
	}
	@Override
	public void setPropertiesFullPathName(String propertiesFullPathName_) {
		_propertiesFullPathName = propertiesFullPathName_;	
	}
	
	
	@Override
	public PropertiesManager getPropertiesManager() {
		return _propertiesManager;
	}
	@Override
	public void setPropertiesManager(PropertiesManager propertiesManager_) {
		_propertiesManager = propertiesManager_;
	}
	
	
	@Override
	public Prototype2ConcreteMap getPrototype2ConcreteMap() {
		return _prototype2ConcreteMap;
	}
	@Override
	public void setPrototype2ConcreteMap(Prototype2ConcreteMap prototype2ConcreteMap_) {
		_prototype2ConcreteMap = prototype2ConcreteMap_;
		
	}
	

//	@Override
//	public ObjectFactory getObjectFactory() {
//		return _objectFactory;
//	}
//	@Override
//	public void setObjectFactory(ObjectFactory objectFactory_) {
//		_objectFactory = objectFactory_;
//	}
	
	
	@Override
	public DynamicCompiler getCompiler() {
		return _compiler;
	}
	@Override
	public void setCompiler(DynamicCompiler compiler_) {
		_compiler = compiler_;
	}
	
	
	@Override
	public Pack getRootPack() {
		return _rootPack;
	}
	@Override
	public void setRootPack(final Pack rootPack_) {
		_rootPack = rootPack_;
	}
	
	
	@Override
	public Monitor getMonitor() {
		return _monitor;
	}
	@Override
	public void setMonitor(final Monitor monitor_) {
		_monitor = monitor_;
	}
	
	
	@Override
	public long getQuantum() {
		return _quantum;
	}
	@Override
	public void setQuantum(final long quantum_) {
		_quantum = quantum_;
	}
	
	
	private Arg _arg;
	private String _propertiesFullPathName;
	private PropertiesManager _propertiesManager;
	private Prototype2ConcreteMap _prototype2ConcreteMap;
	//private ObjectFactory _objectFactory;
	private DynamicCompiler _compiler;
	private Pack _rootPack;
	private Monitor _monitor;
	private long _quantum;
}
