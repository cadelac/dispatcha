package cadelac.framework.blade.core.message;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import cadelac.framework.blade.core.dispatch.Calculation;
import cadelac.framework.blade.core.dispatch.Dispatch;
import cadelac.framework.blade.core.dispatch.PullBase;
import cadelac.framework.blade.core.dispatch.PushBase;
import cadelac.framework.blade.core.dispatch.Routine;
import cadelac.framework.blade.core.dispatch.StateCreator;
import cadelac.framework.blade.core.dispatch.StateIdMapper;
import cadelac.framework.blade.core.exception.FrameworkException;
import cadelac.framework.blade.core.exception.InitializationException;
import cadelac.framework.blade.core.state.State;
import cadelac.framework.blade.core.state.StateAware;
import cadelac.framework.blade.facility.db.DbCommConnection;

/**
 * The framework is message based. A message is passed between handlers.
 * @author cadelac
 *
 */
public interface Message {
	
	public void marshall(final JsonObjectBuilder jsonBuilder_);
	
	public void marshall(final DbCommConnection connection_) 
			throws InitializationException
			, SQLException
			, NoSuchMethodException
			, SecurityException
			, IllegalAccessException
			, IllegalArgumentException
			, InvocationTargetException;
	
	public void demarshall(final JsonObject jsonObject_) 
			throws FrameworkException, Exception;
	
	public void demarshall(final ResultSet resultSet_) 
			throws Exception;
	
	
	// TODO: maybe create interface Dispatchable???
	
	// push using message-type as discriminator...
	
	/**
	 * Delivers message immediately. Uses the message's type as discriminator.
	 * @return
	 * @throws Exception
	 */
	default Message push() throws Exception {
		Dispatch.push(this);
		return this;
	}
	
	/**
	 * Delivers message after a delay. Uses the message's type as discriminator.
	 * @return
	 * @throws Exception
	 */
	default Message push(long delay_) throws Exception {
		System.out.println("delaying..." + delay_);
		Dispatch.push(this, delay_);
		return this;
	}
	
	/**
	 * Delivers message repeatedly. Uses the message's type as discriminator.
	 * @return
	 * @throws Exception
	 */
	default Message push(final long period_, long delay_) 
			throws Exception {
		System.out.println("periodic..." + period_);
		Dispatch.push(this, delay_, period_);
		return this;
	}
	
	// push using hash id as discriminator...
	
	/**
	 * Delivers message immediately. Uses the hash id as discriminator.
	 * @return
	 * @throws Exception
	 */
	default Message push(String hashId_) throws Exception {
		Dispatch.push(hashId_, this);
		return this;
	}
	
	/**
	 * Delivers message after a delay. Uses the hash id as discriminator.
	 * @return
	 * @throws Exception
	 */
	default Message push(long delay_, String hashId_) 
			throws Exception {
		Dispatch.push(hashId_, this, delay_);
		return this;
	}
	
	/**
	 * Delivers message repeatedly. Uses the hash id as discriminator.
	 * @return
	 * @throws Exception
	 */
	default Message push(final long period_, long delay_, String hashId_) 
			throws Exception {
		Dispatch.push(hashId_, this, delay_, period_);
		return this;
	}

	
	/**
	 * Inline push: accepts StateAware
	 * @param stateAware_
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	default <M extends Message, S extends State> M push(
			final StateAware<M,S> stateAware_) 
			throws Exception {
		Dispatch.inlinePush(stateAware_, (M)this);
		return (M) this;
	}
	
	// 
	
	/**
	 * Inline push: accepts pushbase components
	 * @param routine_
	 * @param stateIdMapper_
	 * @param stateCreator_
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	default <M extends Message, S extends State> M push(
			final Routine<M,S> routine_
			, final StateIdMapper<M> stateIdMapper_
			, final StateCreator<M,S> stateCreator_) 
					throws Exception {
		final StateAware<M,S> stateAware_ = 
				new PushBase<M,S>(
						"inline push base"
						, routine_
						, stateIdMapper_
						, stateCreator_);
		Dispatch.inlinePush(stateAware_, (M)this);
		return (M) this;
	}
	
	// pull using message-type as discriminator...
	
	/**
	 * Delivers pull message immediately. Uses the message's type as discriminator.
	 * 
	 * @return message response from handler
	 * @throws Exception
	 */
	default <R> R pull() throws Exception {
		/*
		final Future<Response<M>> future = Dispatch.pull(this);
		final M m = Dispatch.extractResponse(future);
		return m;
		*/
		return Dispatch.extractResponse(
				Dispatch.pull(this));
	}
	
	// TODO: pull using hash-id as discriminator
	
	/**
	 * Delivers inline pull-message immediately
	 * 
	 * @return message response from handler
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	default <R, M extends Message, S extends State> R pull(
			final Calculation<R,M,S> calculation_
			, final StateIdMapper<M> stateIdMapper_
			, final StateCreator<M,S> stateCreator_) 
					throws Exception {
		final StateAware<M,S> stateAware_ = 
				new PullBase<R,M,S>(
						"inline pull base"
						, calculation_
						, stateIdMapper_
						, stateCreator_);

		return Dispatch.extractResponse(
				Dispatch.inlinePull(stateAware_, (M)this));
	}
	
}
