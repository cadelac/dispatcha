package cadelac.framework.blade.handler2;

import java.util.concurrent.Future;

import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.handler.ChannelBase;
import cadelac.lib.primitive.invocation.Response;

public class ChannelInputTypedReplyBase<R,M extends Message>
	extends ChannelBase implements ChannelInputTypedReply<R,M> {
	
	ChannelInputTypedReplyBase(final String id_) {
		super(id_);
	}
	
	@Override
	public Response<R> requestReply(final M msg_) throws Exception {
		final Future<Response<R>> future = getFacility().getAgent().executeRequest(msg_);
		final Response<R> response = future.get();
		return response;
	}

	@Override
	public FacilityReply<R,M> getFacility() {
		return _facility;
	}
	
	@Override
	public void setFacility(final FacilityReply<R,M> facility_) {
		_facility = facility_;
	}
	
	private FacilityReply<R,M> _facility;
}
