package com.shimizukenta.hokuyoopticalparallel;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractHokuyoOpticalParallel<T, U> implements HokuyoOpticalParallel<T, U> {

	public AbstractHokuyoOpticalParallel() {
	}
	
	private final Collection<ReceiveListener<T>> receiveDataListeners = new CopyOnWriteArrayList<>();
	
	@Override
	public boolean addReceiveListener(ReceiveListener<T> l) {
		return receiveDataListeners.add(l);
	}
	
	@Override
	public boolean removeReceiveListener(ReceiveListener<T> l) {
		return receiveDataListeners.remove(l);
	}
	
	protected void putReceiveData(T data) {
		receiveDataListeners.forEach(l -> {l.received(data);});
	}
	
	private final Collection<CommunicateStateChangeListener<U>> communicateStateChangedListeners = new CopyOnWriteArrayList<>();
	
	@Override
	public boolean addCommunicateStateChangeListener(CommunicateStateChangeListener<U> l) {
		return communicateStateChangedListeners.add(l);
	}
	
	@Override
	public boolean removeCommunicateStateChangeListener(CommunicateStateChangeListener<U> l) {
		return communicateStateChangedListeners.remove(l);
	}
	
	protected void putCommunicateStateChanged(U v) {
		communicateStateChangedListeners.forEach(l -> {l.changed(v);});
	}
	
	private final Collection<IOLogListener> ioLogListeners = new CopyOnWriteArrayList<>();
	
	@Override
	public boolean addIOLogListener(IOLogListener l) {
		return ioLogListeners.add(l);
	}
	
	@Override
	public boolean removeIOLogListener(IOLogListener l) {
		return ioLogListeners.remove(l);
	}
	
	protected void putIOLog(IOLog log) {
		ioLogListeners.forEach(l -> {l.received(log);});
	}
	
	protected void putIOLog(Throwable t) {
		putIOLog(new IOLog(t));
	}
}
