package com.shimizukenta.hokuyoopticalparallel;

import java.io.Closeable;
import java.io.IOException;

public interface HokuyoOpticalParallel<T, U> extends Closeable {
	
	public void open() throws IOException;
	
	public void write(byte[] bs) throws IOException, InterruptedException;
	
	public boolean addReceiveListener(ReceiveListener<T> l);
	public boolean removeReceiveListener(ReceiveListener<T> l);
	
	public boolean addCommunicateStateChangedListener(CommunicateStateChangedListener<U> l);
	public boolean removeCommunicateStateChangedListener(CommunicateStateChangedListener<U> l);
	
	public boolean addIOLogListener(IOLogListener l);
	public boolean removeIOLogListener(IOLogListener l);
	
}
