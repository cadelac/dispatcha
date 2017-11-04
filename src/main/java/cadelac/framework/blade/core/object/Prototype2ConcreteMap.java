package cadelac.framework.blade.core.object;

import java.io.IOException;

import cadelac.framework.blade.core.exception.SystemException;
import cadelac.framework.blade.core.message.Generated;

/**
 * This interface is used to map a prototype (interface) to its associated concrete class.
 * @author cadelac
 *
 */
public interface Prototype2ConcreteMap {
	
	public Class<? extends Generated> prototypeClassOf(
			final Class<? extends Generated> concreteClass_) ;
	
	public Class<? extends Generated> get(
			final Class<? extends Generated> prototypeClass_) 
					throws ClassNotFoundException, IOException, SystemException;

	public void put(
			final Class<? extends Generated> prototypeClass_
			, final Class<? extends Generated> concreteClass_);
	
	public Class<? extends Generated> register(
			final Class<? extends Generated> protoClass_) 
					throws IOException, ClassNotFoundException, SystemException;
}
