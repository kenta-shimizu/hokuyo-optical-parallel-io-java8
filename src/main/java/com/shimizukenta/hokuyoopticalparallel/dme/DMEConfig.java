package com.shimizukenta.hokuyoopticalparallel.dme;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public final class DMEConfig implements Serializable {
	
	private static final long serialVersionUID = -1972558893335831312L;

	public static final float DEFAULT_REBIND_SECONDS = 5.0F;
	
	private SocketAddress bind;
	private float rebindSeconds;
	
	public DMEConfig() {
		this.bind = null;
		this.rebindSeconds = DEFAULT_REBIND_SECONDS;
	}
	
	public Optional<SocketAddress> bindSocketAddress() {
		synchronized ( this ) {
			return this.bind == null ? Optional.empty() : Optional.of(this.bind);
		}
	}
	
	public void bindSocketAddress(SocketAddress a) {
		synchronized ( this ) {
			this.bind = a;
		}
	}
	
	public void bindSocketAddress(InetAddress a) {
		this.bindSocketAddress(new InetSocketAddress(a, 0));
	}
	
	public void rebindSeconds(float v) {
		synchronized ( this ) {
			this.rebindSeconds = v;
		}
	}
	
	public float rebindSeconds() {
		synchronized ( this ) {
			return this.rebindSeconds;
		}
	}
	
	private final Set<SocketAddress> connects = new CopyOnWriteArraySet<>();
	
	public boolean addConnect(SocketAddress a) {
		return connects.add(a);
	}
	
	public boolean removeConnect(SocketAddress a) {
		return connects.remove(a);
	}
	
	public Set<SocketAddress> connects() {
		return Collections.unmodifiableSet(connects);
	}
	
}
