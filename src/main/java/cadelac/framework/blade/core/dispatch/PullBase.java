package cadelac.framework.blade.core.dispatch;

import cadelac.framework.blade.core.IdentifiedBase;
import cadelac.framework.blade.core.message.Dispatchable;
import cadelac.framework.blade.core.state.State;

public class PullBase<R,D extends Dispatchable,S extends State> 
	extends IdentifiedBase implements Pull<R,D,S> {

	public PullBase(
			final String id_
			, final Calculation<R,D,S> calculation_
			, final StateIdMapper<D> stateIdMapper_
			, final StateCreator<D,S> stateCreator_) {
		super(id_);
		_calculation = calculation_;
		_stateIdMapper = stateIdMapper_;
		_stateCreator = stateCreator_;
	}
	
	
	@Override
	public String getStateId(D dispatchable_) {
		return getStateIdMapper().getStateId(dispatchable_);
	}


	@Override
	public S createState(D dispatchable_) throws Exception {
		return getStateCreator().createState(dispatchable_);
	}

	
	@Override
	public Calculation<R,D,S> getCalculation() {
		return _calculation;
	}
	
	
	@Override
	public StateIdMapper<D> getStateIdMapper() {
		return _stateIdMapper;
	}


	@Override
	public StateCreator<D,S> getStateCreator() {
		return _stateCreator;
	}

	private final Calculation<R,D,S> _calculation;
	private final StateIdMapper<D> _stateIdMapper;
	private final StateCreator<D,S> _stateCreator;
}
