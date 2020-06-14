package com.shimizukenta.hokuyoopticalparallel.dme;

import java.io.Serializable;

public final class DMEModePacket implements Serializable {
	
	private static final long serialVersionUID = -6488331488009310966L;
	
	private static final byte M = (byte)0x4D;
	private static final byte LF = (byte)0xA;
	
	private final byte[] bs;
	private String toStringProxy;
	
	private DMEModePacket(byte b) {
		this.bs = new byte[] {M, b, LF};
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
				sb.append("M ");
				for ( int i = 8; i > 0; ) {
					--i;
					sb.append(high(i) ? "1" : "0");
				}
				this.toStringProxy = sb.toString();
			}
			return this.toStringProxy;
		}
	}
	
	public static DMEModePacket from(byte b) {
		return new DMEModePacket(b);
	}
	
	public static DMEModePacket from(DMEModeData m) {
		return from(m.get());
	}
	
	public static DMEModePacket from(DMEMode... modes) {
		return from(DMEModeData.from(modes));
	}
	
}
