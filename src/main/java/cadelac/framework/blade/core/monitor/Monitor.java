package cadelac.framework.blade.core.monitor;

import java.util.concurrent.atomic.AtomicLong;

public interface Monitor {
	
	public long getNextJobId();
	public long getNextSequenceId();
	
	
	static Monitor create() {
		
		class MonitorImpl implements Monitor {
			
			public MonitorImpl() {
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
		
		return new MonitorImpl();
	}
}
