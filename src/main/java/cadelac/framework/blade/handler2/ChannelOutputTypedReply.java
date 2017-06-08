package cadelac.framework.blade.handler2;

import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.handler.ChannelCanReply;
import cadelac.lib.primitive.handler.ChannelOutput;

/**
 * An output-channel with parameterized types that returns a reply.
 * @author cadelac
 *
 * @param <R>
 * @param <M>
 */
public interface ChannelOutputTypedReply<R,M extends Message> extends ChannelOutput, ChannelCanReply<R,M> {
	public ChannelInputTypedReply<R,M> getConnectedInput();
	public void setConnectedInput(final ChannelInputTypedReply<R,M>  connectedInput_);
}
