package cadelac.framework.blade.core.dispatch;

import cadelac.framework.blade.core.IdentifiedBase;
import cadelac.framework.blade.core.message.Message;
import cadelac.framework.blade.core.state.State;

public class PushBase<M extends Message,S extends State>
	extends IdentifiedBase implements Push<M,S> {

	public PushBase(
			final String id_
			, final Routine<M,S> routine_
			, final StateIdMapper<M> stateIdMapper_
			, final StateCreator<M,S> stateCreator_) {
		super(id_);
		setRoutine(routine_);
		_stateIdMapper = stateIdMapper_;
		_stateCreator = stateCreator_;
	}

	@Override
	public String getStateId(M message_) {
		return getStateIdMapper().getStateId(message_);
	}

	@Override
	public S createState(M message_) throws Exception {
		return getStateCreator().createState(message_);
	}
	
	@Override
	public Routine<M,S> getRoutine() {
		return _routine;
	}
	
	@Override
	public void setRoutine(final Routine<M,S> routine_) {
		_routine = routine_;
	}
	
	@Override
	public StateIdMapper<M> getStateIdMapper() {
		return _stateIdMapper;
	}


	@Override
	public StateCreator<M, S> getStateCreator() {
		return _stateCreator;
	}
	
	
	private Routine<M,S> _routine;	
	private final StateIdMapper<M> _stateIdMapper;
	private final StateCreator<M,S> _stateCreator;
}
