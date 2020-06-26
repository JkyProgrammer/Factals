package anybrot;

import org.apache.commons.math3.complex.Complex;

public class Equations {
	
	public static int custom (Complex c, int maxIterations, Operation operation) {
		Complex z = c;
		for (int its = 0; its < maxIterations; its++) {
			if (z.abs() > 2.0) return its;
			z = operation.evaluate(z, c);
		}
		return maxIterations;
	}
	
	// Equations:
	// Z = (Z^2) + c              Mandelbrot
	public static int mandelbrot (Complex c, int maxIterations) {
		Complex z = c;
		for (int its = 0; its < maxIterations; its++) {
			if (z.abs() > 2.0) return its;
			//Complex tmp = z.pow(power-1);
			z = z.pow(2).add(c);
		}
		return maxIterations;
	}

	// Z = (Z^d) + k  Julia 2
	public static int julia2 (Complex c, int maxIterations, float power) {
		Complex z = c;
		for (int its = 0; its < maxIterations; its++) {
			if (z.abs() > 2.0) return its;
			double k = -0.1948;
			z = z.pow(power).add(k);
		}
		return maxIterations;
	}

	// Z = (Z^d) + c              Multibrot
	public static int multibrot (Complex c, int maxIterations, float power) {
		Complex z = c;
		for (int its = 0; its < maxIterations; its++) {
			if (z.abs() > 2.0) return its;
			z = z.pow(power).add(c);
		}
		return maxIterations;
	}

	// Z = (Z^Z) + C              Z Power
	public static int zPower (Complex c, int maxIterations) {
		Complex z = c;
		for (int its = 0; its < maxIterations; its++) {
			if (z.abs() > 2.0) return its;
			z = z.pow(z).add(c);
		}
		return maxIterations;
	}

	// Z = (Z^d) - (Z^(d-1)) + c  Power Difference
	public static int powerDifference (Complex c, int maxIterations, float power) {
		Complex z = c;
		for (int its = 0; its < maxIterations; its++) {
			if (z.abs() > 2.0) return its;
			Complex tmp = z.pow(power-1);
			z = z.pow(power).subtract (tmp).add(c);
		}
		return maxIterations;
	}

	// Z = (Z^Z) + C              Z Power
		public static int zRecPower (Complex c, int maxIterations) {
			Complex z = c;
			Complex one = new Complex (1);
			for (int its = 0; its < maxIterations; its++) {
				if (z.abs() > 2.0) return its;
				z = z.pow(one.divide(z)).add(c);
			}
			return maxIterations;
		}
	

	// Z = (Z^d) - (Z^(1/d))      Reciprocal Power
		public static int reciprocalPower (Complex c, int maxIterations, float power) {
			Complex z = c;
			for (int its = 0; its < maxIterations; its++) {
				if (z.abs() > 2.0) return its;
				z = z.pow(power).subtract (z.pow(1/power));
			}
			return maxIterations;
		}
}
