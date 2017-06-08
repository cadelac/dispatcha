package cadelac.framework.blade.handler2;

import cadelac.framework.blade.core.Framework;
import cadelac.framework.blade.core.component.ComponentBase;
import cadelac.lib.primitive.concept.Container;
import cadelac.lib.primitive.exception.ArgumentException;

public class FacilityBase extends ComponentBase implements Facility {

	public FacilityBase(final String id_) {
		super(id_, Framework.getShell());
		_container = null;
	}

	@Override
	public Container getContainer() {
		return _container;
	}

	@Override
	public void setContainer(Container container_) throws ArgumentException {
		_container = container_;
	}
	
	private Container _container; /* not final since this is populated after construction */
}
