package com.shimizukenta.hokuyoopticalparallel.dme;

import java.io.Serializable;

public final class DMEInputData implements Serializable {
	
	private static final long serialVersionUID = -4009350247279673915L;
	
	private byte input;
	
	private DMEInputData(byte input) {
		this.input = input;
	}
	
	private DMEInputData() {
		this((byte)0x0);
	}
	
	public byte get() {
		synchronized ( this ) {
			return this.input;
		}
	}
	
	private boolean isHigh(int shift) {
		synchronized ( this ) {
			byte b = (byte)0x1;
			b <<= shift;
			return (this.input & b) != 0x0;
		}
	}
	
	public boolean isInput(DMEInput input) {
		synchronized ( this ) {
			return isHigh(input.shift()) == input.high();
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
	
	public void set(byte input) {
		synchronized ( this ) {
			this.input = input;
		}
	}
	
	public void set(DMEInput... inputs) {
		
		synchronized ( this ) {
			
			byte ref = get();
			
			for ( DMEInput i : inputs ) {
				
				byte b = (byte)0x1;
				b <<= i.shift();
				
				if ( i.high() ) {
					
					ref |= b;
					
				} else {
					
					ref &= ~b;
				}
			}
			
			set(ref);
		}
	}
	
	public static DMEInputData initial() {
		return new DMEInputData();
	}
	
	public static DMEInputData from(byte input) {
		return new DMEInputData(input);
	}
	
	public static DMEInputData from(DMEInput... inputs) {
		DMEInputData i = initial();
		i.set(inputs);
		return i;
	}
}
