package cadelac.framework.blade.handler2;

import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.handler.ChannelCanSubmit;
import cadelac.lib.primitive.handler.ChannelOutput;

/**
 * An output-channel with parameterized types that do not return a reply.
 * @author cadelac
 *
 */
public interface ChannelOutputTypedSubmit<M extends Message> extends ChannelOutput, ChannelCanSubmit<M> {
	public ChannelInputTypedSubmit<M> getConnectedInput();
	public void setConnectedInput(final ChannelInputTypedSubmit<M>  connectedInput_);
}
