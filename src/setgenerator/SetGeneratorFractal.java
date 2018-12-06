package setgenerator;

import javax.swing.*;

public class SetGeneratorFractal {

	public float runSet (Double c, int maxIterations) {
		Double z = 0d;
		for (int its = 0; its < maxIterations; its++) {
			if (Math.abs(z) > 2.0) return its;
			z = (z*z) + c;
		}
		return maxIterations;
	}
	
	public static void main(String[] args) {
		
	}

	JFrame frame = new JFrame ("SetGenerator");
	
	public void start () {
		frame.setSize(400, 400);
		
		Picture p = new Picture ()
	}
	
}
