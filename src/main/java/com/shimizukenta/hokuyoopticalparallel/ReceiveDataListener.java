package com.shimizukenta.hokuyoopticalparallel;

import java.util.EventListener;

public interface ReceiveDataListener<T extends ReceiveData> extends EventListener {
	public void receive(T data);
}
