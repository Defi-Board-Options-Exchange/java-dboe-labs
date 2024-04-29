package com.ngontro86.server.dboe.volsurface;

class MDouble {
	public double v;
	private double min = Double.NaN;
	private double max = Double.NaN;


	public MDouble(double p_v, double p_min, double p_max) {
		v = p_v;
		min = p_min;
		max = p_max;
		trim();
	}

	public MDouble(double p_v) {
		v = p_v;
	}

	public void set(double p_v, double p_min, double p_max) {
		v = p_v;
		min = p_min;
		max = p_max;
	}

	//captures initial case when min=max = NaN as well as where val is NaN
	public void set(double p_v) {
		v = p_v;
		if (v > max || Double.isNaN(max)) max = v;
		if (v < min || Double.isNaN(min)) min = v;
	}

	//if v,min,max not initialized won't do anything
	public void trim() {
		if (v < min) v = min;
		if (v > max) v = max;
	}

	@Override
	public String toString() {
		return String.format("v=%.12f (%.12f,%.12f)", v, min, max);
	}
}
