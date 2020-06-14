package com.shimizukenta.hokuyoopticalparallel.dme;

public enum DMEMode {
	
	RESET_ON(6, true), RESET_OFF(6, false),
	MODE_ON(5, true), MODE_OFF(5, false),
	SELECT_ON(4, true), SELECT_OFF(4, false),
	GO_ON(0, true), GO_OFF(0, false),
	
	;
	
	private final int shift;
	private final boolean high;
	
	private DMEMode(int shift, boolean high) {
		this.shift = shift;
		this.high = high;
	}
	
	public int shift() {
		return this.shift;
	}
	
	public boolean high() {
		return this.high;
	}
}
