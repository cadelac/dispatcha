package cadelac.framework.blade.facility.db;

import static cadelac.framework.blade.Framework.$dispatch;
import static cadelac.framework.blade.Framework.$store;

import java.sql.SQLException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
				runScript(script_, serializedTransactionState);
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
				, (SerializedTransactionState state_) 
					-> runScript(script_, state_));
	}
	
	private static void runScript(
			final TransactionScript script_
			, final SerializedTransactionState state_) 
					throws Exception {
		
		final DbCommConnection dbCommConnection = state_.getDbCommConnection();
		if (dbCommConnection == null 
				|| !dbCommConnection.getIsInitialized()
				|| !dbCommConnection.getIsConnected()) {
			logger.warn(String.format(
					"%s exception: database not initialized/open; stateId %s"
					, SerializedTransaction.class.getSimpleName()
					, state_.getId()));
		}
		else {
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


	private static final Logger logger = LogManager.getLogger(SerializedTransaction.class);
}
