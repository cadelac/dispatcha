package cadelac.framework.blade.core.component.rack;

import cadelac.framework.blade.pack.Pack;
import cadelac.lib.primitive.arg.Arg;
import cadelac.lib.primitive.code.DynamicCompiler;
import cadelac.lib.primitive.monitor.Monitor;
import cadelac.lib.primitive.object.ObjectFactory;
import cadelac.lib.primitive.object.Prototype2ConcreteMap;
import cadelac.lib.primitive.property.PropertiesManager;


/**
 * This is an interface to the container of all components of the framework.
 * @author cadelac
 *
 */
public interface Rack {
	
	public Arg getArg();
	public void setArg(final Arg arg_);
	
	public String getPropertiesFullPathName();
	public void setPropertiesFullPathName(final String propertiesFullPathName);
	
	public PropertiesManager getPropertiesManager();
	public void setPropertiesManager(final PropertiesManager propertiesManager_);
	
	public Prototype2ConcreteMap getPrototype2ConcreteMap();
	public void setPrototype2ConcreteMap(final Prototype2ConcreteMap map_);
	
//	public ObjectFactory getObjectFactory();
//	public void setObjectFactory(final ObjectFactory objectFactory_);
	
	public DynamicCompiler getCompiler();
	public void setCompiler(final DynamicCompiler compiler_);
	
	public Pack getRootPack();
	public void setRootPack(final Pack rootPack_);
	
	public Monitor getMonitor();
	public void setMonitor(final Monitor monitor_);
	
	public long getQuantum();
	public void setQuantum(final long quantum_);
}
