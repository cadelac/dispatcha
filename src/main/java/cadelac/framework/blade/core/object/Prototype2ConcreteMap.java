package cadelac.framework.blade.core.object;

import java.io.IOException;

import cadelac.framework.blade.core.exception.SystemException;
import cadelac.framework.blade.core.message.Message;

/**
 * This interface is used to map a prototype (interface) to its associated concrete class.
 * @author cadelac
 *
 */
public interface Prototype2ConcreteMap {
	
	public Class<? extends Message> prototypeClassOf(
			final Class<? extends Message> concreteClass_) ;
	
	public Class<? extends Message> get(
			final Class<? extends Message> prototypeClass_) 
					throws ClassNotFoundException, IOException, SystemException;

	public void put(
			final Class<? extends Message> prototypeClass_
			, final Class<? extends Message> concreteClass_);
	
	public Class<? extends Message> register(
			final Class<? extends Message> protoClass_) 
					throws IOException, ClassNotFoundException, SystemException;
}
