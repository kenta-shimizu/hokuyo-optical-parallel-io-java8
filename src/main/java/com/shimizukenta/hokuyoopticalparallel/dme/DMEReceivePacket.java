package com.shimizukenta.hokuyoopticalparallel.dme;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.Arrays;

import com.shimizukenta.hokuyoopticalparallel.ReceiveData;

public final class DMEReceivePacket implements ReceiveData, Serializable {
	
	private static final long serialVersionUID = -3804889113227041914L;
	
	private final byte[] bs;
	private final SocketAddress remote;
	
	private DMEOutputData outputDataProxy;
	private DMEModeData modeDataProxy;
	
	private String toStringProxy;
	
	public DMEReceivePacket(byte[] bs, SocketAddress remote) {
		this.bs = bs;
		this.remote = remote;
		this.outputDataProxy = null;
		this.modeDataProxy = null;
		this.toStringProxy = null;
	}
	
	@Override
	public byte[] getBytes() {
		return Arrays.copyOf(bs, bs.length);
	}
	
	public SocketAddress remoteAddress() {
		return this.remote;
	}
	
	private static final byte empty = (byte)0x0;
	
	private static final int DataSize = 4;
	
	public boolean isCorrect() {
		return bs.length == DataSize;
	}
	
	public char type() {
		return isCorrect() ? (char)(bs[0]) : (char)empty;
	}
	
	private static final char R = 'R';
	
	public boolean isR() {
		return type() == R;
	}
	
	public byte id() {
		return isCorrect() ? bs[1] : empty;
	}
	
	public DMEOutputData outputData() {
		synchronized ( this ) {
			if ( this.outputDataProxy == null ) {
				if ( isCorrect() ) {
					this.outputDataProxy = new DMEOutputData(bs[2]);
				} else {
					this.outputDataProxy = new DMEOutputData(empty);
				}
			}
			return this.outputDataProxy;
		}
	}
	
	public DMEModeData modeData() {
		synchronized ( this ) {
			if ( this.modeDataProxy == null ) {
				if ( isCorrect() ) {
					this.modeDataProxy = new ReadonlyDMEModeData(bs[3]);
				} else {
					this.modeDataProxy = new ReadonlyDMEModeData(empty);
				}
			}
			return modeDataProxy;
		}
	}
	
	private final class ReadonlyDMEModeData extends DMEModeData {
		
		private static final long serialVersionUID = 7367020987218798159L;

		private ReadonlyDMEModeData(byte mode) {
			super(mode);
		}
		
		@Override
		public void set(byte mode) {
			throw new UnsupportedOperationException("Readonly");
		}
	}
	
	@Override
	public String toString() {
		synchronized ( this ) {
			if ( this.toStringProxy == null ) {
				StringBuilder sb = new StringBuilder();
				
				sb.append(isR() ? "R" : "?")
				.append(" ")
				.append(String.format("%03d", ((int)(id()) & 0xFF)))
				.append(" ")
				.append(outputData().toString())
				.append(" ")
				.append(modeData().toString())
				.append(" ")
				.append(remoteAddress().toString());
				
				this.toStringProxy = sb.toString();
			}
			return this.toStringProxy;
		}
	}
	
}
