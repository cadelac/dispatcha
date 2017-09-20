package cadelac.framework.blade.core.monitor;

import java.util.concurrent.atomic.AtomicLong;

public class MonitorSimple implements Monitor {

	public MonitorSimple() {
		_jobId = new AtomicLong(1L);
		_sequenceId = new AtomicLong(1L);
	}
	
	@Override
	public long getNextJobId() {
		return _jobId.getAndIncrement();
	}

	@Override
	public long getNextSequenceId() {
		return _sequenceId.getAndIncrement();
	}
	
	private final AtomicLong _jobId;
	private final AtomicLong _sequenceId;

}
