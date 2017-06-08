package cadelac.framework.blade.handler2;

import cadelac.framework.blade.core.Framework;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.exception.InitializationException;
import cadelac.lib.primitive.handler.AgentSubmit;
import cadelac.lib.primitive.object.ObjectFactory;

public abstract class AgentSubmitBase<M extends Message,S extends State> implements AgentSubmit<M,S> {
	
	@Override
	public abstract void perform(final M msg_, final S state_) throws Exception;

	
	@Override
	public void executeSubmit(final M msg_) throws InterruptedException {
		Executor.execute(new ActivationSubmitBase<M,S>(this, msg_));
	}
	
	protected ObjectFactory getObjectFactory() {

		return Framework.getObjectFactory();
	}
	
	protected String getMandatoryProperty(final String property_) throws InitializationException {
		return Framework.getShell().getMandatoryProperty(property_);
	}
	
	protected String getProperty(final String property_) throws InitializationException {
		return Framework.getShell().getRack().getPropertiesManager().getProperties().getProperty(property_);
	}
	
}
