package cadelac.framework.blade.core.object;

import cadelac.framework.blade.Framework;
import cadelac.framework.blade.core.exception.FrameworkException;
import cadelac.framework.blade.core.message.Generated;

public interface ObjectFactory {
	/**
	 * Creates a new instance of a class. Throws exceptions.
	 * @param type_
	 * @return
	 * @throws Exception 
	 * @throws FrameworkException 
	 */
	public <T extends Generated> T fabricate(Class<T> type_) 
			throws FrameworkException, Exception;
	
	
	/**
	 * Creates a new instance of a class. Throws exceptions. Provides an object populator.
	 * @param type_
	 * @return
	 * @throws Exception 
	 * @throws FrameworkException 
	 */
	public <T extends Generated> 
	T fabricate(Class<T> type_, ObjectPopulator<T> objectPopulator_)
			throws Exception;
	
	
	static ObjectFactory create() {
		return new ObjectFactory() {
			@Override
			public <T extends Generated> 
			T fabricate(Class<T> type_) throws Exception {
				return fabricate(type_, p -> {});
			}

			@Override
			public <T extends Generated> 
			T fabricate(Class<T> type_, ObjectPopulator<T> objectPopulator_) 
					throws Exception {
				final Class<? extends Generated> concreteType = 
						Framework.getPrototype2ConcreteMap().get(type_);
				
				@SuppressWarnings("unchecked")
				final T createdObject = (T) concreteType.newInstance();
				objectPopulator_.populate(createdObject);
				return createdObject;
			}
		};
	}
}
