package cadelac.framework.blade.core.dispatch;

import cadelac.framework.blade.core.IdentifiedBase;
import cadelac.framework.blade.core.message.Dispatchable;
import cadelac.framework.blade.core.state.State;

public class PushBase<D extends Dispatchable,S extends State>
	extends IdentifiedBase implements Push<D,S> {

	public PushBase(
			final String id_
			, final Routine<D,S> routine_
			, final StateIdMapper<D> stateIdMapper_
			, final StateCreator<D,S> stateCreator_) {
		super(id_);
		setRoutine(routine_);
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
	public Routine<D,S> getRoutine() {
		return _routine;
	}
	
	@Override
	public void setRoutine(final Routine<D,S> routine_) {
		_routine = routine_;
	}
	
	@Override
	public StateIdMapper<D> getStateIdMapper() {
		return _stateIdMapper;
	}


	@Override
	public StateCreator<D,S> getStateCreator() {
		return _stateCreator;
	}
	
	
	private Routine<D,S> _routine;	
	private final StateIdMapper<D> _stateIdMapper;
	private final StateCreator<D,S> _stateCreator;
}
