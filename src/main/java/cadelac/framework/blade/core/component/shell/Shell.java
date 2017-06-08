package cadelac.framework.blade.core.component.shell;

import cadelac.framework.blade.core.component.app.Application;
import cadelac.framework.blade.core.component.rack.Rack;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.exception.InitializationException;

public interface Shell {
	public Rack getRack();
	public void setRack(final Rack rack_);
	
	public Application getApplication();
	public void setApplication(final Application application_);
	
	/**
	 * This method will register the specified prototype class. 
	 * The framework dynamically generates the source code for the corresponding 
	 * concrete class, compile and load the resulting class. Once the class is loaded, an
	 * instance of the prototype class can be created using the ObjectFactory
	 */
	public void registerClass(final Class<? extends Message> protoClass_) throws Exception;

	public void start();
	
	/** Get mandatory property from command-line argument. If not found, get from properties
	 */
	public String getMandatoryProperty(final String property_) throws InitializationException;
}
