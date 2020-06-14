package com.shimizukenta.hokuyoopticalparallel.dme;

import java.io.Serializable;

public class DMEOutputData implements Serializable {
	
	private static final long serialVersionUID = -5830636600096369683L;
	
	private final byte output;
	
	private String toStringProxy;
	
	public DMEOutputData(byte output) {
		this.output = output;
		this.toStringProxy = null;
	}
	
	public byte get() {
		return this.output;
	}
	
	private boolean high(int shift) {
		byte b = (byte)0x1;
		b <<= shift;
		return (this.output & b) != 0x0;
	}
	
	public boolean high(DMEOutput output) {
		return high(output.shift());
	}
	
	public boolean low(DMEOutput output) {
		return ! high(output);
	}
	
	public boolean on(DMEOutput output) {
		return high(output);
	}
	
	public boolean off(DMEOutput output) {
		return low(output);
	}
	
	@Override
	public String toString() {
		synchronized ( this ) {
			if ( this.toStringProxy == null ) {
				StringBuilder sb = new StringBuilder();
				for (int i = 8; i > 0;) {
					--i;
					sb.append(high(i) ? "1" : "0");
				}
				this.toStringProxy = sb.toString();
			}
			return this.toStringProxy;
		}
	}
}
