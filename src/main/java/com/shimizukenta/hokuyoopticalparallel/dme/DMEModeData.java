package com.shimizukenta.hokuyoopticalparallel.dme;

import java.io.Serializable;

/**
 * This class is Mode Data.
 * 
 * @author kenta-shimizu
 *
 */
public class DMEModeData implements Serializable {

	private static final long serialVersionUID = 1349926879655591769L;
	
	private byte mode;
	
	protected DMEModeData(byte mode) {
		this.mode = mode;
	}
	
	protected DMEModeData() {
		this((byte)0x0);
	}
	
	/**
	 * Returns  mode byte.
	 * 
	 * @return mode byte
	 */
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
	
	/**
	 * Returns bit is high.
	 * 
	 * @param mode
	 * @return {@code true} if bit is high
	 */
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
	
	/**
	 * mode Setter
	 * 
	 * @param mode
	 */
	public void set(byte mode) {
		synchronized ( this ) {
			this.mode = mode;
		}
	}
	
	/**
	 * modes Setter.
	 * 
	 * @param modes
	 */
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
	
	/**
	 * Returns new instance.
	 * 
	 * @return DMEModeData instance
	 */
	public static DMEModeData initial() {
		return new DMEModeData();
	}
	
	/**
	 * Returns new instance from byte.
	 * 
	 * @param mode
	 * @return DMEModeData instance
	 */
	public static DMEModeData from(byte mode) {
		return new DMEModeData(mode);
	}
	
	/**
	 * Returns new instance from modes.
	 * 
	 * @param modes
	 * @return DMEModeData instance.
	 */
	public static DMEModeData from(DMEMode... modes) {
		DMEModeData m = initial();
		m.set(modes);
		return m;
	}
	
}
