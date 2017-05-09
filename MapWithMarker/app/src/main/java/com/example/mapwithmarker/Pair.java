package com.example.mapwithmarker;

class Pair<X, Y> {
	public X first;
	public Y second;
	public Pair( X f, Y s) {
		first = f;
		second = s;
	}
	@Override
	public String toString() {
		return "("+first+", " + second + ")";
	}
}
