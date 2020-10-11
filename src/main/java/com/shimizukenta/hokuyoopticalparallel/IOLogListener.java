package com.shimizukenta.hokuyoopticalparallel;

import java.util.EventListener;

public interface IOLogListener extends EventListener {
	public void received(IOLog log);
}
