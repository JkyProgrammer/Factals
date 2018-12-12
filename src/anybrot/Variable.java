package anybrot;

import org.apache.commons.math3.complex.Complex;

public class Variable {
	public String name;
	public Complex value;
	
	public Variable (String n, Complex v) {
		name = n;
		value = v;
	}
}
