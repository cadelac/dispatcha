package cadelac.framework.blade.core.object;

import java.io.IOException;

import cadelac.framework.blade.core.exception.FrameworkException;
import cadelac.framework.blade.core.exception.SystemException;
import cadelac.framework.blade.core.message.Message;

public interface ObjectFactory {
	/**
	 * Creates a new instance of a class. Throws exceptions.
	 * @param type_
	 * @return
	 * @throws Exception 
	 * @throws FrameworkException 
	 */
	public <T extends Message> T fabricate(Class<T> type_) 
			throws FrameworkException, Exception;
	
	
	/**
	 * Creates a new instance of a class. Throws exceptions. Provides an object populator.
	 * @param type_
	 * @return
	 * @throws Exception 
	 * @throws FrameworkException 
	 */
	public <T extends Message> T fabricate(Class<T> type_, ObjectPopulator<T> objectPopulator_) 
			throws Exception;
	
	public Class<? extends Message> register(final Class<? extends Message> protoClass_) 
			throws IOException, ClassNotFoundException, SystemException;
}
