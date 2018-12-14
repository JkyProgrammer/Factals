package anybrot;

import org.apache.commons.math3.complex.Complex;

public class Operation {
	public Operation (OperationType ot, ArgumentType a1t, Object a1, ArgumentType a2t, Object a2) {
		opType = ot;
		a1Type = a1t;
		a2Type = a2t;
		
		try {
			if (a1t == ArgumentType.complex) {
				a1Complex = Complex.class.cast(a1);
			} else if (a1t == ArgumentType.operationReference) {
				a1Operation = Operation.class.cast(a1);
			}
			
			if (a2t == ArgumentType.complex) {
				a2Complex = Complex.class.cast(a2);
			} else if (a2t == ArgumentType.operationReference) {
				a2Operation = Operation.class.cast(a2);
			}
		} catch (NullPointerException e1) {
			e1.printStackTrace();
		} catch (ClassCastException e2) {
			e2.printStackTrace();
		}
	}
	
	public Complex evaluate (Complex zFeed, Complex cFeed) {
		Complex a1C = a1Complex;
		if (a1Type == ArgumentType.operationReference) {
			a1C = a1Operation.evaluate(zFeed, cFeed);
		} else if (a1Type == ArgumentType.zReference) {
			a1C = zFeed;
		} else if (a1Type == ArgumentType.cReference) {
			a1C = cFeed;
		}
		
		Complex a2C = a2Complex;
		if (a2Type == ArgumentType.operationReference) {
			a2C = a2Operation.evaluate(zFeed, cFeed);
		} else if (a2Type == ArgumentType.zReference) {
			a2C = zFeed;
		} else if (a2Type == ArgumentType.cReference) {
			a2C = cFeed;
		}
		
		Complex output = null;
		
		if (opType == OperationType.add) {
			output = a1C.add(a2C);
		} else if (opType == OperationType.subtract) {
			output = a1C.subtract(a2C);
		} else if (opType == OperationType.multiply) {
			output = a1C.multiply(a2C);
		} else if (opType == OperationType.divide) {
			output = a1C.divide(a2C);
		} else if (opType == OperationType.exponent) {
			output = a1C.pow(a2C);
		}
		
		return output;
	}
	
	OperationType opType;
	
	// Argument 1 Storage
	ArgumentType a1Type;
	
	Complex a1Complex = null;
	Operation a1Operation = null;
	
	// Argument 2 Storage
	ArgumentType a2Type;
		
	Complex a2Complex = null;
	Operation a2Operation = null;
}
