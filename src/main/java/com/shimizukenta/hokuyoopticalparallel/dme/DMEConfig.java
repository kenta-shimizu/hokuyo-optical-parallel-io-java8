package com.shimizukenta.hokuyoopticalparallel.dme;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/** 
 * This class is DME config Setter/Getter.
 * 
 * <p>
 * To set bind SocketAddress,
 * {@link #bindSocketAddress(InetAddress)},
 * {@link #bindSocketAddress(SocketAddress)}.<br />
 * To set connect SocketAddress,
 * {@link #addConnect(SocketAddress)}.<br />
 * </p>
 * 
 * @author kenta-shimizu
 *
 */
public final class DMEConfig implements Serializable {
	
	private static final long serialVersionUID = -1972558893335831312L;

	public static final float DEFAULT_REBIND_SECONDS = 5.0F;
	
	private SocketAddress bind;
	private float rebindSeconds;
	
	public DMEConfig() {
		this.bind = null;
		this.rebindSeconds = DEFAULT_REBIND_SECONDS;
	}
	
	/**
	 * Return binding SocketAddress.
	 * 
	 * @return Binding SocketAddress if exist, and {@code Optional.empty()} otherwise
	 */
	public Optional<SocketAddress> bindSocketAddress() {
		synchronized ( this ) {
			return this.bind == null ? Optional.empty() : Optional.of(this.bind);
		}
	}
	
	/**
	 * Binding SocketAddress setter.
	 * 
	 * @param a binding SocketAddress
	 */
	public void bindSocketAddress(SocketAddress a) {
		synchronized ( this ) {
			this.bind = a;
		}
	}
	
	/**
	 * Stter of Binding SocketAddress by InetAddress, set Port is 0.
	 * 
	 * @param a Binding InetAddress
	 */
	public void bindSocketAddress(InetAddress a) {
		this.bindSocketAddress(new InetSocketAddress(a, 0));
	}
	
	/**
	 * Rebind seconds setter.
	 * 
	 * @param v seconds
	 */
	public void rebindSeconds(float v) {
		synchronized ( this ) {
			this.rebindSeconds = v;
		}
	}
	
	/**
	 * Returns rebind seconds.
	 * 
	 * @return rebind seconds
	 */
	public float rebindSeconds() {
		synchronized ( this ) {
			return this.rebindSeconds;
		}
	}
	
	private final Set<SocketAddress> connects = new CopyOnWriteArraySet<>();
	
	/**
	 * Add Connecting SocketAddress.
	 * 
	 * @param a connecting SocketAddress
	 * @return {@code true} if add success
	 */
	public boolean addConnect(SocketAddress a) {
		return connects.add(a);
	}
	
	/**
	 * Remove Connecting SokcetAddress.
	 * 
	 * @param a connecting SocketAddress
	 * @return {@code true} if remove success
	 */
	public boolean removeConnect(SocketAddress a) {
		return connects.remove(a);
	}
	
	/**
	 * Returns connect SocketAddress set.
	 * 
	 * <p>
	 * Return Set is UnmodifiableSet.<br />
	 * </p>
	 * 
	 * @return connect SocketAddress Set.
	 */
	public Set<SocketAddress> connects() {
		return Collections.unmodifiableSet(connects);
	}
	
}
