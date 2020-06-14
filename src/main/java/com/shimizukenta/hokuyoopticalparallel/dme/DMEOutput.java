package com.shimizukenta.hokuyoopticalparallel.dme;

public enum DMEOutput {
	
	Output1(0),
	Output2(1),
	Output3(2),
	Output4(3),
	Output5(4),
	Output6(5),
	Output7(6),
	Output8(7),
	
	;
	
	private final int shift;
	
	private DMEOutput(int shift) {
		this.shift = shift;
	}
	
	public int shift() {
		return this.shift;
	}
}
