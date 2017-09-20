package cadelac.framework.blade.core.dispatch;

import cadelac.framework.blade.core.IdentifiedBase;
import cadelac.framework.blade.core.message.Message;
import cadelac.framework.blade.core.state.State;

public class PullBase<R,M extends Message,S extends State> 
	extends IdentifiedBase implements Pull<R,M,S> {

	public PullBase(
			final String id_
			, final Calculation<R,M,S> calculation_
			, final StateIdMapper<M> stateIdMapper_
			, final StateCreator<M,S> stateCreator_) {
		super(id_);
		_calculation = calculation_;
		_stateIdMapper = stateIdMapper_;
		_stateCreator = stateCreator_;
	}
	
	
	@Override
	public String getStateId(M message) {
		return getStateIdMapper().getStateId(message);
	}


	@Override
	public S createState(M message_) throws Exception {
		return getStateCreator().createState(message_);
	}

	
	@Override
	public Calculation<R,M,S> getCalculation() {
		return _calculation;
	}
	
	
	@Override
	public StateIdMapper<M> getStateIdMapper() {
		return _stateIdMapper;
	}


	@Override
	public StateCreator<M, S> getStateCreator() {
		return _stateCreator;
	}

	private final Calculation<R,M,S> _calculation;
	private final StateIdMapper<M> _stateIdMapper;
	private final StateCreator<M,S> _stateCreator;
}
