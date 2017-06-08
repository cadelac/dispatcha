package cadelac.framework.blade.handler2;

import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.handler.ChannelBase;
import cadelac.lib.primitive.invocation.Response;

public class ChannelOutputTypedReplyBase<R,M extends Message> 
	extends ChannelBase implements ChannelOutputTypedReply<R,M> {

	public ChannelOutputTypedReplyBase(final String id_) {
		super(id_);
	}

	@Override
	public Response<R> requestReply(final M msg_) throws Exception {
		return _connectedInput.requestReply(msg_);
	}

	@Override
	public ChannelInputTypedReply<R, M> getConnectedInput() {
		return _connectedInput;
	}

	@Override
	public void setConnectedInput(final ChannelInputTypedReply<R, M> connectedInput_) {
		_connectedInput = connectedInput_;
		
	}

	private ChannelInputTypedReply<R,M> _connectedInput;
}
