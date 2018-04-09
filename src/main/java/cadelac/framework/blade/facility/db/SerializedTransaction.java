package cadelac.framework.blade.facility.db;

import static cadelac.framework.blade.Framework.$dispatch;
import static cadelac.framework.blade.Framework.$store;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import cadelac.framework.blade.core.state.StateId;
import cadelac.framework.blade.core.state.StatePolicy;


public class SerializedTransaction {

	public static void execute(
			final String stateId_
			, final TransactionScript script_) 
					throws Exception {
		
		final SerializedTransactionState serializedTransactionState =
				$store.getValue(stateId_);
		
		if (serializedTransactionState != null) {
			synchronized (serializedTransactionState) {
				DbCommConnection dbCommConnection = 
						serializedTransactionState.getDbCommConnection();
				try {
					script_.run(dbCommConnection);
					dbCommConnection.getConnection().commit();
				}
				catch (SQLException e) {
					logger.warn(String.format(
							"transaction exception: rolling back\n%s"
							, e.getMessage()));
					dbCommConnection.getConnection().rollback();
				}
			}
		}
	}
	
	
	public static void push(
			final String stateId_
			, final TransactionScript script_) 
					throws Exception {
		
		$dispatch.push(
				StatePolicy.MUST_PRE_EXIST
				, () -> StateId.build(stateId_)
				, () -> null
				, (SerializedTransactionState state_) -> {
					DbCommConnection dbCommConnection = 
							state_.getDbCommConnection();
					try {
						script_.run(dbCommConnection);
						dbCommConnection.getConnection().commit();
					}
					catch (SQLException e) {
						logger.warn(String.format(
								"transaction exception: rolling back\n%s"
								, e.getMessage()));
						dbCommConnection.getConnection().rollback();
					}
				});
	}


	private static final Logger logger = Logger.getLogger(SerializedTransaction.class);
}
