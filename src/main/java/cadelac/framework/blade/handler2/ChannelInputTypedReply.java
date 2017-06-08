package cadelac.framework.blade.handler2;

import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.handler.ChannelCanReply;
import cadelac.lib.primitive.handler.ChannelInput;

/**
 * An input-channel with parameterized types that returns a reply.
 * @author cadelac
 *
 * @param <R> the type of reply
 * @param <M> the type of message
 */
public interface ChannelInputTypedReply<R,M extends Message> extends ChannelInput, ChannelCanReply<R,M> {
	public FacilityReply<R,M> getFacility();
	public void setFacility(final FacilityReply<R,M> facility_);
}
