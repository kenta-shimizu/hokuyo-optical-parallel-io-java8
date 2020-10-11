package com.shimizukenta.hokuyoopticalparallel.dme;

import java.io.Serializable;

/**
 * This class is DMEOutputData.
 * 
 * @author kenta-shimizu
 *
 */
public class DMEOutputData implements Serializable {
	
	private static final long serialVersionUID = -5830636600096369683L;
	
	private final byte output;
	
	private String toStringProxy;
	
	public DMEOutputData(byte output) {
		this.output = output;
		this.toStringProxy = null;
	}
	
	/**
	 * Returns output byte.
	 * 
	 * @return output byte
	 */
	public byte get() {
		return this.output;
	}
	
	private boolean isHigh(int shift) {
		byte b = (byte)0x1;
		b <<= shift;
		return (this.output & b) != 0x0;
	}
	
	/**
	 * Returns is bit high.
	 * 
	 * @param output
	 * @return {@code true} is high
	 */
	public boolean isHigh(DMEOutput output) {
		return isHigh(output.shift());
	}
	
	/**
	 * Returns is bit low.
	 * 
	 * @param output
	 * @return {@code true} is low
	 */
	public boolean isLow(DMEOutput output) {
		return ! isHigh(output);
	}
	
	/**
	 * Equivalent to isHigh
	 * 
	 * @param output
	 * @return Equivalent to isHigh
	 */
	public boolean isOn(DMEOutput output) {
		return isHigh(output);
	}
	
	/**
	 * Equivalent to isLow
	 * 
	 * @param output
	 * @return Equivalent to isLow
	 */
	public boolean isOff(DMEOutput output) {
		return isLow(output);
	}
	
	@Override
	public String toString() {
		synchronized ( this ) {
			if ( this.toStringProxy == null ) {
				StringBuilder sb = new StringBuilder();
				for (int i = 8; i > 0;) {
					--i;
					sb.append(isHigh(i) ? "1" : "0");
				}
				this.toStringProxy = sb.toString();
			}
			return this.toStringProxy;
		}
	}
}
