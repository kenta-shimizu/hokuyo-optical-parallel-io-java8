package com.shimizukenta.hokuyoopticalparallel;

import java.util.EventListener;

public interface ReceiveListener<T> extends EventListener {
	public void received(T data);
}
