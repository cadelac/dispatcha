package cadelac.framework.blade.core.state;

import org.apache.log4j.Logger;

public abstract class StateBase implements State {
	
	public StateBase(final String id_) {
		_id = id_;
		logger.info(String.format("created state [%s]", id_));
	}
	
	@Override
	public String getId() {
		return _id;
	}

	private final String _id;
	
	private static final Logger logger = Logger.getLogger(State.class);

}
