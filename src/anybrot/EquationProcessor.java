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
			String sub = simplified.substring(simplified.indexOf("("), simplified.indexOf(")")+1);
			simplified = simplified.replace(sub, "exp"+exp);
			ops.add (decode (sub.replaceAll("\\(", "").replaceAll("\\)", "")));
			openBracks = countOccurrences (simplified, '(');
			closeBracks = countOccurrences (simplified, ')');
		}
		
		// What's left has no brackets
		OperationType ot = OperationType.add;
		int opTypeLoc = 1;
		if (simplified.substring(0, 3).equals("exp")) opTypeLoc = 4;
		switch (simplified.charAt(opTypeLoc)) {
		case '^':
			ot = OperationType.exponent;
			break;
		case '*':
			ot = OperationType.multiply;
			break;
		case '/':
			ot = OperationType.divide;
			break;
		case '-':
			ot = OperationType.subtract;
			break;
		default:
			System.out.println("Oh. Well that shouldn't be possible.");	
			break;
		}
		
		// HERE
		
		return null;
	}
	
	private static void syntaxException (String charRef) {
		JOptionPane.showMessageDialog(null, "Syntax error: " + charRef + ".", "Equation Error", JOptionPane.ERROR_MESSAGE);
	}
}
