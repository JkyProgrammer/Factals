package setgenerator;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class SetGeneratorFractal {

	public int runSet (Complex c, int maxIterations) {
		Complex z = c;
		for (int its = 0; its < maxIterations; its++) {
			if (z.abs() > 2.0) return its;
			z = z.times(z).plus(c);
		}
		return maxIterations;
	}
	
	public static void main(String[] args) {
		SetGeneratorFractal sgf = new SetGeneratorFractal ();
		sgf.start(512, 256);
	}
	
	public void start (int imageSize, int maxIterations) {
		BufferedImage i = new BufferedImage (imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);
		for (int yLoc = 0; yLoc < imageSize; yLoc++) {
			for (int xLoc = 0; xLoc < imageSize; xLoc++) {
				int result = runSet (new Complex (xLoc, yLoc), maxIterations);
				float mappedGrey = result/maxIterations;
				Color col = new Color (mappedGrey, mappedGrey, mappedGrey);
				int rgb = col.getRGB();
				i.setRGB(xLoc, yLoc, rgb);
			}
		}
		
		JFrame f = new JFrame ("Result");
		f.setSize(800, 800);
		f.setLayout(new GridLayout ());
		JLabel l = new JLabel(new ImageIcon (i));
		f.add(l);
		
		//f.setVisible(true);
		
		try {
			File outputfile = new File("Mandelbrot.png");
		    ImageIO.write(i, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
