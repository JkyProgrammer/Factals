package anybrot;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JOptionPane;

import org.apache.commons.math3.complex.Complex;

public class EquationProcessor {
	public static Operation generateFunction (String raw) {
		String simplified = raw.replaceAll(" ", "");
		ArrayList<Character> valids = new ArrayList<Character> (Arrays.asList('Z', 'z', 'C', 'c', '^', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '-', '(', ')', '*', '/', '.'));
		
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
	
	private static int countOccurrences (String s, char c) {
		int count = 0;
		for (char cc : s.toCharArray()) {
			if (c == cc) {
				count++;
			}
		}
		
		return count;
	}
	
	private static Operation decode (String simplified) {
		int openBracks = countOccurrences (simplified, '(');
		int closeBracks = countOccurrences (simplified, ')');
		if (openBracks != closeBracks) syntaxException ("Bracket error in equation.");
		ArrayList<Operation> ops = new ArrayList<Operation> ();
		
		int exp = 0;
		while (openBracks > 0) {
			String sub = simplified.substring(simplified.indexOf("("), simplified.indexOf("("));
			simplified.replaceFirst(sub, "exp"+exp);
			ops.add (decode (sub));
			openBracks = countOccurrences (simplified, '(');
			closeBracks = countOccurrences (simplified, ')');
		}
		
		// What's left has no brackets
		// Each bracketed expression has been processed and is represented in the ops array
		// HERE
		
		return null;
	}
	
	private static void syntaxException (String charRef) {
		JOptionPane.showMessageDialog(null, "Syntax error: " + charRef + ".", "Equation Error", JOptionPane.ERROR_MESSAGE);
	}
}
