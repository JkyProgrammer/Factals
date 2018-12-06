package quadruplebranching;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

public class QuadrupleBranchingFractal {
	
	public static void main(String[] args) {
		QuadrupleBranchingFractal f = new QuadrupleBranchingFractal (12, 0.5d);
		f.begin ();
	}

	public QuadrupleBranchingFractal (int rd, double rf) {
		recursionDepth = rd;
		reductionFactor = rf;
	}
	
	public void begin() {
		baseGenerate ();
		System.out.println("We did it! We escaped the return chain.");
		System.out.println("Writing...");
		write ();
		System.out.println("Done.");
	}

	int recursionDepth;
	double reductionFactor;
	
	public void write () {
		try {
			PrintStream fStream = new PrintStream ("Test.svg");
			File f = new File ("Test.svg");
			System.out.println(f.getAbsolutePath());
			fStream.println("<svg viewBox=\"0 0 8192 8192\">");
			
			fStream.print("<path fill=\"none\" stroke=\"black\" stroke-width=\"0.01\" d=\"");
			long numItems = 0;
			for (Line line : verticalLines) {
				if (line != null) {
					fStream.print ("\n" + line.svgDataValue());
					numItems++;
				}
			}
			fStream.println ("\" />");
			
			fStream.print("<path fill=\"none\" stroke=\"black\" stroke-width=\"0.01\" d=\"");
			for (Line line : horizontalLines) {
				if (line != null) {
					fStream.print ("\n" + line.svgDataValue());
					numItems++;
				}
			}
			fStream.println ("\" />");
			
			fStream.println ("</svg>");
			fStream.close();
			System.out.println(numItems);
		} catch (IOException e) {
			System.out.println("Write error!");
			e.printStackTrace();
		}
	}
	
	public void baseGenerate () {
		double lineLength = 4096;
		verticalLines.add(new Line (0, -4096, 0, 4096));
		//verticalLines.add(new Line (0, 0, 0, -128));
		horizontalLines.add(new Line (-4096, 0, 4096, 0));
		//horizontalLines.add(new Line (0, 0, -128, 0));
		double newLineLength = lineLength*reductionFactor;
		
		
		Thread t1 = new Thread (new Runnable () {
			@Override
			public void run() {
				verticalGenerate (0d, newLineLength, 0, newLineLength);
			}
		});
		Thread t2 = new Thread (new Runnable () {
			@Override
			public void run() {
				verticalGenerate (0d, -newLineLength, 0, newLineLength);
			}
		});
		Thread t3 = new Thread (new Runnable () {
			@Override
			public void run() {
				horizontalGenerate (newLineLength, 0d, 0, newLineLength);
			}
		});
		Thread t4 = new Thread (new Runnable () {
			@Override
			public void run() {
				horizontalGenerate (-newLineLength, 0d, 0, newLineLength);
			}
		});
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		
		try {
			t1.join();
			t2.join();
			t3.join();
			t4.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void verticalGenerate (double xOrigin, double yOrigin, int currentDepth, double lineLength) {
		currentDepth++;
		
		horizontalLines.add(new Line (xOrigin - lineLength, yOrigin, xOrigin + lineLength, yOrigin));
		//horizontalLines.add(new Line (xOrigin, yOrigin, xOrigin - lineLength, yOrigin));
		double newLineLength = lineLength*reductionFactor;
		
		if (currentDepth < recursionDepth) {
			horizontalGenerate (xOrigin + newLineLength, yOrigin, currentDepth, newLineLength);
			horizontalGenerate (xOrigin - newLineLength, yOrigin, currentDepth, newLineLength);
		}
	}
	
	public void horizontalGenerate (double xOrigin, double yOrigin, int currentDepth, double lineLength) {
		currentDepth++;
		
		verticalLines.add(new Line (xOrigin, yOrigin - lineLength, xOrigin, yOrigin + lineLength));
		//verticalLines.add(new Line (xOrigin, yOrigin, xOrigin, yOrigin - lineLength));
		double newLineLength = lineLength*reductionFactor;
		
		if (currentDepth < recursionDepth) {
			verticalGenerate (xOrigin, yOrigin + newLineLength, currentDepth, newLineLength);
			verticalGenerate (xOrigin, yOrigin - newLineLength, currentDepth, newLineLength);
		}
	}
	
	Vector<Line> verticalLines = new Vector<Line> ();
	Vector<Line> horizontalLines = new Vector<Line> ();
}
