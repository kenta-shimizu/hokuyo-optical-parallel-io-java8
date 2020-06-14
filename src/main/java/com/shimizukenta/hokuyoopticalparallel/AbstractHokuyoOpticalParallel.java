package com.shimizukenta.hokuyoopticalparallel;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractHokuyoOpticalParallel<T extends ReceiveData, U> implements HokuyoOpticalParallel<T, U> {

	public AbstractHokuyoOpticalParallel() {
	}
	
	private final Collection<ReceiveDataListener<T>> receiveDataListeners = new CopyOnWriteArrayList<>();
	
	@Override
	public boolean addReceiveDataListener(ReceiveDataListener<T> l) {
		return receiveDataListeners.add(l);
	}
	
	@Override
	public boolean removeReceiveDataListener(ReceiveDataListener<T> l) {
		return receiveDataListeners.remove(l);
	}
	
	protected void putReceiveData(T data) {
		receiveDataListeners.forEach(l -> {l.receive(data);});
	}
	
	private final Collection<CommunicateStateChangedListener<U>> communicateStateChangedListeners = new CopyOnWriteArrayList<>();
	
	@Override
	public boolean addCommunicateStateChangedListener(CommunicateStateChangedListener<U> l) {
		return communicateStateChangedListeners.add(l);
	}
	
	@Override
	public boolean removeCommunicateStateChangedListener(CommunicateStateChangedListener<U> l) {
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
		ioLogListeners.forEach(l -> {l.recieve(log);});
	}
	
	protected void putIOLog(Throwable t) {
		putIOLog(new IOLog(t));
	}
}
