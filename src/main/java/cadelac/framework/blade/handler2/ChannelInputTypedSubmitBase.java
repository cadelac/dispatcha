package cadelac.framework.blade.handler2;

import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.handler.ChannelBase;

public class ChannelInputTypedSubmitBase<M extends Message> 
	extends ChannelBase implements ChannelInputTypedSubmit<M> {

	public ChannelInputTypedSubmitBase(final String id_) {
		super(id_);
	}

	@Override
	public void submit(final M msg_) throws Exception {
		getFacility().getAgent().executeSubmit(msg_);
	}

	@Override
	public FacilitySubmit<M> getFacility() {
		return _facility;
	}
	
	@Override
	public void setFacility(final FacilitySubmit<M> facility_) {
		_facility = facility_;
	}

	private FacilitySubmit<M> _facility;
}
