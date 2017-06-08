package cadelac.framework.blade.handler2;

import org.apache.log4j.Logger;

import cadelac.framework.blade.core.service.object.MessageMap;
import cadelac.lib.primitive.Provider;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.exception.RouteException;
import cadelac.lib.primitive.invocation.Response;



public class Router {
	
	public static <M extends Message>
	void submit(final M msg_) throws Exception {
		final Class<? extends Message> concreteClass = msg_.getClass();
		@SuppressWarnings("unchecked")
		final FacilitySubmit<M> facilitySubmit = (FacilitySubmit<M>) _providerFacilitySubmit.getElement(concreteClass);
		if (facilitySubmit == null)
			throw new RouteException(String.format("FacilitySubmit not found for message [%s]", concreteClass.getSimpleName()));
		final ChannelInputTypedSubmit<M> input = facilitySubmit.getInput();
		input.submit(msg_);
	}
	
	public static <R,M extends Message>
	Response<R> requestReply(final M msg_) throws Exception {
		final Class<? extends Message> concreteClass = msg_.getClass();
		@SuppressWarnings("unchecked")
		final FacilityReply<R,M> facilityReply = (FacilityReply<R,M>) _providerFacilityReply.getElement(concreteClass);
		if (facilityReply == null)
			throw new RouteException(String.format("FacilityReply not found for message [%s]", concreteClass.getSimpleName()));
		final ChannelInputTypedReply<R,M> input = facilityReply.getInput();
		return input.requestReply(msg_);
	}

	public static <M extends Message>
	void registerFacility(final Class<M> prototype_, final FacilitySubmit<M> facility_)
			throws Exception {
		final Class<? extends Message> concreteClass = MessageMap.getMatchingConcreteClass(prototype_);
		if (_providerFacilitySubmit.getElement(concreteClass) == null) // not present
			_providerFacilitySubmit.safeAdd(concreteClass, facility_);
		else {
			final String diagnostic = String.format("FacilitySubmit [%s] for type [%s] not added: already exists", 
					facility_.getId(), prototype_.getSimpleName());
			logger.warn(diagnostic);
			throw new RouteException(diagnostic);
		}
	}
	
	public static <R,M extends Message>
	void registerFacility(final Class<? extends Message> prototype_, final FacilityReply<R,M> facility_)
			throws Exception {
		final Class<? extends Message> concreteClass = MessageMap.getMatchingConcreteClass(prototype_);
		if (_providerFacilityReply.getElement(concreteClass) == null) // not present
			_providerFacilityReply.safeAdd(concreteClass, facility_);
		else {
			final String diagnostic = String.format("FacilityReply [%s] for type [%s] not added: already exists", 
					facility_.getId(), prototype_.getSimpleName());
			logger.warn(diagnostic);
			throw new RouteException(diagnostic);
		}
	}
	
	
	private static final Logger logger = Logger.getLogger(Router.class);
	
	private static 
	Provider<Class<? extends Message>,FacilitySubmit<? extends Message>> _providerFacilitySubmit = 
		new Provider<Class<? extends Message>,FacilitySubmit<? extends Message>>();
	
	private static 
	Provider<Class<? extends Message>,FacilityReply<?,? extends Message>> _providerFacilityReply = 
		new Provider<Class<? extends Message>,FacilityReply<?,? extends Message>>();
}
