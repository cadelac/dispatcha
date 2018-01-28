package cadelac.framework.blade.lock;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import cadelac.framework.blade.core.object.ObjectPopulator;

public class ReadWriteLockable<T> {

	public ReadWriteLockable(final T lockable_) {
		this._lockable = lockable_;
		this._rwLock = new ReentrantReadWriteLock();
	}
	
	
	public void communityReadAccess(
			final ObjectPopulator<T> populator_) 
					throws Exception {
		try { _rwLock.readLock().lock(); populator_.populate(_lockable); }
		catch (Exception e) { throw e; }
		finally { _rwLock.readLock().unlock(); }
	}
	
	
	public void singularWriteAccess(
			final ObjectPopulator<T> populator_) 
					throws Exception {
		try { _rwLock.writeLock().lock(); populator_.populate(_lockable); }
		catch (Exception e) { throw e; }
		finally { _rwLock.writeLock().unlock(); }
	}
	
	
	private volatile T _lockable;
	private final ReadWriteLock _rwLock;
}
