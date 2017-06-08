package cadelac.framework.blade.core.service.object;

import cadelac.framework.blade.core.Framework;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.exception.SystemException;
import cadelac.lib.primitive.object.ObjectFactory;
import cadelac.lib.primitive.object.ObjectPopulator;
import cadelac.lib.primitive.object.Prototype2ConcreteMap;

public class ObjectFactorySimple implements ObjectFactory {

	public ObjectFactorySimple(final Prototype2ConcreteMap map_) {
		_map = map_;
	}

	@Override
	public <T extends Message> T fabricate(Class<T> type_) throws Exception {
		return fabricate(type_, null);
	}


	@Override
	public <T extends Message> T fabricate(Class<T> type_, ObjectPopulator<T> objectPopulator_) throws Exception {
		final Class<? extends Message> concreteType = getConcreteClass(type_);
		
		@SuppressWarnings("unchecked")
		final T createdObject = (T) concreteType.newInstance();
		
		if (objectPopulator_ != null) {
			// populate concrete object
			objectPopulator_.populate(createdObject);			
		}

		return createdObject;
	}
	
	private <T extends Message> Class<? extends Message> getConcreteClass(final Class<T> type_) 
			throws Exception {
		final Class<? extends Message> concreteType = _map.get(type_);
		if (concreteType != null)
			return concreteType;
		
		// it is not yet registered; well, let's register right here...
		Framework.getShell().registerClass(type_);
		// as side effect of registration, it should be in the map
		final Class<? extends Message> concreteType2 = _map.get(type_);
		if (concreteType2 != null)
			return concreteType2;
		
		// some kind of failure, throw exception...
		throw new SystemException(String.format("failed to register class: %s", type_.getSimpleName()));
	}
	
	private final Prototype2ConcreteMap _map;

}
