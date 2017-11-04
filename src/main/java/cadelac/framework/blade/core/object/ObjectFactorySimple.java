package cadelac.framework.blade.core.object;

import java.io.IOException;

import cadelac.framework.blade.Framework;
import cadelac.framework.blade.core.exception.SystemException;
import cadelac.framework.blade.core.message.Generated;

public class ObjectFactorySimple implements ObjectFactory {

	@Override
	public <T extends Generated> T fabricate(Class<T> type_) throws Exception {
		return fabricate(type_, null);
	}


	@Override
	public <T extends Generated> T fabricate(Class<T> type_, ObjectPopulator<T> objectPopulator_) 
			throws Exception {
		final Class<? extends Generated> concreteType = Framework.getPrototype2ConcreteMap().get(type_);
		
		@SuppressWarnings("unchecked")
		final T createdObject = (T) concreteType.newInstance();
		
		if (objectPopulator_ != null) {
			// populate concrete object
			objectPopulator_.populate(createdObject);			
		}

		return createdObject;
	}


	@Override
	public Class<? extends Generated> register(Class<? extends Generated> protoClass_)
			throws IOException, ClassNotFoundException, SystemException {
		return Framework.getPrototype2ConcreteMap().register(protoClass_);
	}

}
