package com.shimizukenta.hokuyoopticalparallel;

import java.util.EventListener;

public interface CommunicateStateChangedListener<T> extends EventListener {
	public void changed(T v);
}
