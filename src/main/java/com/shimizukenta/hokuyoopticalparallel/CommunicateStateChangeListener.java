package com.shimizukenta.hokuyoopticalparallel;

import java.util.EventListener;

public interface CommunicateStateChangeListener<T> extends EventListener {
	public void changed(T v);
}
