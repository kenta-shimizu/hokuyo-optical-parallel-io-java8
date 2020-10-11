package com.shimizukenta.hokuyoopticalparallel.dme;

import java.io.Serializable;

/**
 * This class is DME InputData.
 * 
 * @author kenta-shimizu
 *
 */
public final class DMEInputData implements Serializable {
	
	private static final long serialVersionUID = -4009350247279673915L;
	
	private byte input;
	
	private DMEInputData(byte input) {
		this.input = input;
	}
	
	private DMEInputData() {
		this((byte)0x0);
	}
	
	/**
	 * Returns Input byte
	 * 
	 * @return Input byte
	 */
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
	
	/**
	 * Returns input is high.
	 * 
	 * @param input
	 * @return {@code true} is high
	 */
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
	
	/**
	 * byte setter.
	 * 
	 * @param input
	 */
	public void set(byte input) {
		synchronized ( this ) {
			this.input = input;
		}
	}
	
	/**
	 * inputs Setter.
	 * 
	 * @param inputs
	 */
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
	
	/**
	 * Returns new instance.
	 * 
	 * @return DMEInputData instance
	 */
	public static DMEInputData initial() {
		return new DMEInputData();
	}
	
	/**
	 * Returns new instance from byte.
	 * 
	 * @param input
	 * @return DMEInputData instance
	 */
	public static DMEInputData from(byte input) {
		return new DMEInputData(input);
	}
	
	/**
	 * Returns DMEInputData from inputs.
	 * 
	 * @param inputs
	 * @return DMEInputData instance
	 */
	public static DMEInputData from(DMEInput... inputs) {
		DMEInputData i = initial();
		i.set(inputs);
		return i;
	}
}
