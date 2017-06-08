package cadelac.framework.blade.handler2;

import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.handler.ChannelBase;

public class ChannelOutputTypedSubmitBase<M extends Message> 
	extends ChannelBase implements ChannelOutputTypedSubmit<M> {

	public ChannelOutputTypedSubmitBase(final String id_) {
		super(id_);
	}

	@Override
	public void submit(M msg_) throws Exception {
		_connectedInput.submit(msg_);
	}

	@Override
	public ChannelInputTypedSubmit<M> getConnectedInput() {
		return _connectedInput;
	}

	@Override
	public void setConnectedInput(final ChannelInputTypedSubmit<M> connectedInput_) {
		_connectedInput = connectedInput_;
		
	}

	private ChannelInputTypedSubmit<M> _connectedInput;
}
