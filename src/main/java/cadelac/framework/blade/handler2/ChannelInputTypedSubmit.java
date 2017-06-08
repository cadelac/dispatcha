package cadelac.framework.blade.handler2;

import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.handler.ChannelCanSubmit;
import cadelac.lib.primitive.handler.ChannelInput;

/**
 * An input-channel with parameterized types that do not return a reply.
 * @author cadelac
 *
 */
public interface ChannelInputTypedSubmit<M extends Message> extends ChannelInput, ChannelCanSubmit<M> {
	public FacilitySubmit<M> getFacility();
	public void setFacility(final FacilitySubmit<M> facility_);
}
