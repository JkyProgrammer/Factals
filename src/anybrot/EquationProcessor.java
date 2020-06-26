package anybrot;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JOptionPane;

import org.apache.commons.math3.complex.Complex;

public class EquationProcessor {
	public static Operation generateFunction (String raw) {
		String simplified = raw.replaceAll(" ", "");
		ArrayList<Character> valids = new ArrayList<Character> (Arrays.asList('Z', 'z', 'C', 'c', '^', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '-', '(', ')', '*', '/', '.', 'i'));
		
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
		while (simplified.charAt(opTypeLoc) != '+' && simplified.charAt(opTypeLoc) != '-' && simplified.charAt(opTypeLoc) != '*' && simplified.charAt(opTypeLoc) != '/' && simplified.charAt(opTypeLoc) != '^') opTypeLoc++;
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
		case '+':
			ot = OperationType.add;
			break;
		default:
			System.out.println("Oh. Well that shouldn't be possible.");	
			break;
		}
		
		int length = simplified.length();
		
		ArgumentType at1 = null;
		Object a1 = null;
		
		ArgumentType at2 = null;
		Object a2 = null;
		
		if (simplified.substring(0, 3).equals("exp")) {
			at1 = ArgumentType.operationReference;
			a1 = ops.get(0);
		} else if (simplified.charAt(0) == 'C' || simplified.charAt(0) == 'c') {
			at1 = ArgumentType.cReference;
		} else if (simplified.charAt(0) == 'Z' || simplified.charAt(0) == 'z') {
			at1 = ArgumentType.zReference;
		} else if (simplified.charAt(opTypeLoc-1) == 'i') {
			Double d = Double.parseDouble (simplified.substring(0, opTypeLoc-1));
			Complex c = new Complex (0, d);
			at1 = ArgumentType.complex;
			a1 = c;
		} else {
			Double d = Double.parseDouble (simplified.substring(0, opTypeLoc));
			Complex c = new Complex (d);
			at1 = ArgumentType.complex;
			a1 = c;
		}
		
		if (simplified.contains("exp") && simplified.substring(length-4, length-1).equals("exp")) {
			at2 = ArgumentType.operationReference;
			a2 = ops.get(ops.size()-1);
		} else if (simplified.charAt(length-1) == 'C' || simplified.charAt(length-1) == 'c') {
			at2 = ArgumentType.cReference;
		} else if (simplified.charAt(length-1) == 'Z' || simplified.charAt(length-1) == 'z') {
			at2 = ArgumentType.zReference;
		} else if (simplified.charAt(length-1) == 'i') {
			Double d = Double.parseDouble (simplified.substring(opTypeLoc+1, length-1));
			Complex c = new Complex (0, d);
			at2 = ArgumentType.complex;
			a2 = c;
		} else {
			Double d = Double.parseDouble (simplified.substring(opTypeLoc+1, length));
			Complex c = new Complex (d);
			at2 = ArgumentType.complex;
			a2 = c;
		}
		
		Operation op = new Operation (ot, at1, a1, at2, a2);
		
		return op;
	}
	
	private static void syntaxException (String charRef) {
		JOptionPane.showMessageDialog(null, "Syntax error: " + charRef + ".", "Equation Error", JOptionPane.ERROR_MESSAGE);
	}
}
