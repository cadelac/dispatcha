package cadelac.framework.blade.comm.rpc;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import cadelac.framework.blade.Framework;
import cadelac.framework.blade.core.message.Message;


public class RpcTicket<R extends Message> {
	
	public static class RpcClaimStub<M extends Message> {
		
		public RpcClaimStub(final long sequenceNo_) {
			_futureTask = new FutureTask<M>(
					new Callable<M>() {
						public M call() {
							@SuppressWarnings("unchecked")
							final RpcTicket<M> rpc = 
								(RpcTicket<M>) RpcTicket.removeRpc(sequenceNo_);
							return rpc._responseMsg;
						}
					}
			);	
		}

		public FutureTask<M> getClaimStub() {
			return _futureTask;
		}
		
		public void execute() {
			final Thread thread = new Thread(_futureTask);
			thread.start();
		}


		private final FutureTask<M> _futureTask;
	}
	
	public RpcTicket(final long sequenceNo_) {
		_sequenceNo = sequenceNo_;
		_rpcClaimStub = new RpcClaimStub<R>(sequenceNo_);
		_responseMsg = null;
	}

	public long getSequenceNo() {
		return _sequenceNo;
	}
	
	public R get() 
			throws InterruptedException, ExecutionException { 
		return _rpcClaimStub.getClaimStub().get(); 
	}
	
	public void execute() {
		_rpcClaimStub.execute();
	}
	
	public static <R extends Message> void sowForLater(
			final RpcTicket<R> rpcTicket_) {
		putRpc(rpcTicket_.getSequenceNo(), rpcTicket_);
	}
	
	public static <R extends Message> void reapNow(
			final long sequenceNo_
			, final R msg) 
					throws Exception {

		@SuppressWarnings("unchecked")
		final RpcTicket<R> rpc = 
			(RpcTicket<R>) getRpc(sequenceNo_);
		
		// embed endorse msg into rpc
		rpc._responseMsg = msg;
		rpc.execute();
	}

	
	private static RpcTicket<? extends Message> getRpc(final long asyncKey) {
		return Framework.getRpcTable().get(asyncKey);
	}

	private static void putRpc(
			final long asyncKey, final RpcTicket<? extends Message> rpcTicket_) {
		Framework.getRpcTable().put(asyncKey, rpcTicket_);
	}

	private static RpcTicket<? extends Message> removeRpc(final long asyncKey) {
		return Framework.getRpcTable().remove(asyncKey);
	}
	
	
	private final long _sequenceNo;
	public RpcClaimStub<R> _rpcClaimStub;
	public R _responseMsg;
}
