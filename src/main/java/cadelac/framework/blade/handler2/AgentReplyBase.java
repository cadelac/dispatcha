package cadelac.framework.blade.handler2;

import java.util.concurrent.Future;

import cadelac.framework.blade.core.Framework;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.exception.InitializationException;
import cadelac.lib.primitive.handler.AgentReply;
import cadelac.lib.primitive.invocation.Response;
import cadelac.lib.primitive.object.ObjectFactory;

public abstract class AgentReplyBase<R,M extends Message,S extends State> implements AgentReply<R,M,S> {

	@Override
	public abstract R respond(final M msg_, final S state_) throws Exception;

	@Override
	public Future<Response<R>> executeRequest(final M msg_) throws InterruptedException {
		return Executor.executeRequest(new ActivationReplyBase<R,M,S>(this, msg_));
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
