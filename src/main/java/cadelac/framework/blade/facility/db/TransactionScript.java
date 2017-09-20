package cadelac.framework.blade.facility.db;

@FunctionalInterface
public interface TransactionScript {
	public void run(final DbCommConnection dbCommConnection_) throws Exception;
}
