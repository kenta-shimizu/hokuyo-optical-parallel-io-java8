package com.shimizukenta.hokuyoopticalparallel.dme;

import java.io.Serializable;

public class DMEModeData implements Serializable {

	private static final long serialVersionUID = 1349926879655591769L;
	
	private byte mode;
	
	protected DMEModeData(byte mode) {
		this.mode = mode;
	}
	
	protected DMEModeData() {
		this((byte)0x0);
	}
	
	public byte get() {
		return mode;
	}
	
	private boolean isHigh(int shift) {
		synchronized ( this ) {
			byte b = (byte)0x1;
			b <<= shift;
			return (this.mode & b) != 0x0;
		}
	}
	
	public boolean isMode(DMEMode mode) {
		synchronized ( this ) {
			return isHigh(mode.shift()) == mode.high();
		}
	}
	
	@Override
	public String toString() {
		synchronized ( this ) {
			StringBuilder sb = new StringBuilder();
			for ( int i = 8; i > 0; ) {
				--i;
				sb.append(isHigh(i) ? "1" : "0");
			}
			return sb.toString();
		}
	}
	
	public void set(byte mode) {
		synchronized ( this ) {
			this.mode = mode;
		}
	}
	
	public void set(DMEMode... modes) {
		
		synchronized ( this ) {
			
			byte ref = get();
			
			for ( DMEMode m : modes ) {
				
				byte b = (byte)0x1;
				b <<= m.shift();
				
				if ( m.high() ) {
					
					ref |= b;
					
				} else {
					
					ref &= ~b;
				}
			}
			
			set(ref);
		}
	}
	
	public static DMEModeData initial() {
		return new DMEModeData();
	}
	
	public static DMEModeData from(byte mode) {
		return new DMEModeData(mode);
	}
	
	public static DMEModeData from(DMEMode... modes) {
		DMEModeData m = initial();
		m.set(modes);
		return m;
	}
	
}
