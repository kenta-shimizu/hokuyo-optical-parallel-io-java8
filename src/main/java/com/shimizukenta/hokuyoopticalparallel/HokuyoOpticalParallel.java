package com.shimizukenta.hokuyoopticalparallel;

import java.io.Closeable;
import java.io.IOException;

/**
 * This interface is impelements Hokuyo-Optical-IO, open/close, write/receive.
 * 
 * <p>
 * To start communicating, {@link #open()}.<br />
 * To stop communicating, {@link #clone()}.<br />
 * </p>
 * <p>
 * To write bytes, {@link #write(byte[])}.<br />
 * </p>
 * <p>
 * To receive data, {@link #addReceiveListener(ReceiveListener)}.<br />
 * </p>
 * <p>
 * To get communicate log, {@link #addIOLogListener(IOLogListener)}.<br />
 * </p>
 * 
 * @author kenta-shimizu
 *
 * @param <T>
 * @param <U>
 */
public interface HokuyoOpticalParallel<T, U> extends Closeable {
	
	public void open() throws IOException;
	
	public void write(byte[] bs) throws IOException, InterruptedException;
	
	public boolean addReceiveListener(ReceiveListener<T> l);
	public boolean removeReceiveListener(ReceiveListener<T> l);
	
	public boolean addCommunicateStateChangeListener(CommunicateStateChangeListener<U> l);
	public boolean removeCommunicateStateChangeListener(CommunicateStateChangeListener<U> l);
	
	public boolean addIOLogListener(IOLogListener l);
	public boolean removeIOLogListener(IOLogListener l);
	
}
