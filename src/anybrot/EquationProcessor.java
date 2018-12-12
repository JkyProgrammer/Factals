package anybrot;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JOptionPane;

import org.apache.commons.math3.complex.Complex;

public class EquationProcessor {
	public static Function generateFunction (String raw) {
		String simplified = raw.replaceAll(" ", "");
		ArrayList<Character> valids = new ArrayList<Character> (Arrays.asList('Z', 'z', '^', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '-', '(', ')', '*', '/', '.'));
		
		// Verify the input for invalid characters
		for (char chr : simplified.toCharArray()) {
			if (!valids.contains(new Character (chr))) {
				JOptionPane.showMessageDialog(null, "There was an error processing your equation. It contained an unsupported character " + chr + ".", "Equation Error", JOptionPane.ERROR_MESSAGE);
				return null;
			}
		}
		
		// Decode the user input into a function
		return decode (simplified);
	}
	
	private static Function decode (String simplified) {
		// WIP
		return null;
	}
	
	private static void syntaxException (String charRef) {
		JOptionPane.showMessageDialog(null, "Syntax error: " + charRef + ".", "Equation Error", JOptionPane.ERROR_MESSAGE);
	}
}
