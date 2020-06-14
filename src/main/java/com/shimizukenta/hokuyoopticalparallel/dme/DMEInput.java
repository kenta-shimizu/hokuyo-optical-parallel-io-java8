package com.shimizukenta.hokuyoopticalparallel.dme;

public enum DMEInput {
	
	Input1_ON(0, true),	Input1_OFF(0, false),
	Input2_ON(1, true),	Input2_OFF(1, false),
	Input3_ON(2, true),	Input3_OFF(2, false),
	Input4_ON(3, true),	Input4_OFF(3, false),
	Input5_ON(4, true),	Input5_OFF(4, false),
	Input6_ON(5, true),	Input6_OFF(5, false),
	Input7_ON(6, true),	Input7_OFF(6, false),
	Input8_ON(7, true),	Input8_OFF(7, false),
	
	;
	
	private final int shift;
	private final boolean high;
	
	private DMEInput(int shift, boolean high) {
		this.shift = shift;
		this.high = high;
	}
	
	public int shift() {
		return shift;
	}
	
	public boolean high() {
		return high;
	}
}
