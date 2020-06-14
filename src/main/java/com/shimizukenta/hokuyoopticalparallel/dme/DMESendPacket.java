package com.shimizukenta.hokuyoopticalparallel.dme;

import java.io.Serializable;

public class DMESendPacket implements Serializable {
	
	private static final long serialVersionUID = -1950819347390821732L;
	
	private static final byte S = (byte)0x53;
	private static final byte LF = (byte)0xA;
	
	private final byte[] bs;
	private String toStringProxy;
	
	private DMESendPacket(byte input) {
		this.bs = new byte[] {S, input, LF};
		this.toStringProxy = null;
	}
	
	public byte[] getBytes() {
		return this.bs;
	}
	
	private boolean high(int shift) {
		synchronized ( this ) {
			byte b = (byte)0x1;
			b <<= shift;
			return (this.bs[1] & b) != 0x0;
		}
	}
	
	@Override
	public String toString() {
		synchronized ( this ) {
			if ( this.toStringProxy == null ) {
				StringBuilder sb = new StringBuilder();
				sb.append("S ");
				for ( int i = 8; i > 0; ) {
					--i;
					sb.append(high(i) ? "1" : "0");
				}
				this.toStringProxy = sb.toString();
			}
			return this.toStringProxy;
		}
	}

	public static DMESendPacket from(byte input) {
		return new DMESendPacket(input);
	}
	
	public static DMESendPacket from(DMEInputData input) {
		return new DMESendPacket(input.get());
	}
	
	public static DMESendPacket from(DMEInput... inputs) {
		return from(DMEInputData.from(inputs));
	}

}
