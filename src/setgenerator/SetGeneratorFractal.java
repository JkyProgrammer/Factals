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
		float xPos = 0.0f;
		float yPos = 0.6f;
		SetGeneratorFractal sgf = new SetGeneratorFractal (1024, 64, xPos, yPos, 0.1f);
		sgf.prepare();
		sgf.calculate();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		for (float f = 0.5f; f < 3.0; f+= 0.5) {
			System.out.println("Beginning analysis of zoom value " + f + ", at position: " + xPos + ", " + yPos);
			sgf.setZoom(f);
			sgf.calculate();
		}
		
		System.out.println("Zooming finished.");
	}
	
	public SetGeneratorFractal (int imageSize, int maxIterations, float xPos, float yPos, float zoom) {
		this.imageSize = imageSize;
		this.maxIterations = maxIterations;
		this.xPos = xPos;
		this.yPos = yPos;
		this.zoom = zoom;
	}
	
	private int imageSize;
	private int maxIterations;
	private float xPos;
	private float yPos;
	private float zoom;
	
	public void setZoom (float newZoom) {
		zoom = newZoom;
	}
	
	private BufferedImage i;
	
	private void processPixel (int xLoc, int yLoc) {
		double xValue = (float)xLoc;
		xValue -= imageSize/2;
		
		xValue /= ((float)imageSize);
		xValue /= zoom;
		
		double yValue = (float)(yLoc);
		yValue -= imageSize/2;
		
		yValue /= ((float)imageSize);
		
		yValue /= zoom;
		yValue -= yPos;
		
		int result = runSet (new Complex (xValue, yValue), maxIterations);
		float mappedGrey = 1f - (float)result/(float)maxIterations;
		Color col = new Color (mappedGrey, mappedGrey, mappedGrey);
		int rgb = col.getRGB();
		i.setRGB(xLoc, yLoc, rgb);
		
		displayImage.getImage().flush();
		f.repaint();
	}
	
	private ImageIcon displayImage;
	private JFrame f;
	
	public void prepare () {
		i = new BufferedImage (imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);
		displayImage = new ImageIcon (i);
		
		f = new JFrame ("Result");
		f.setSize(800, 800);
		f.setLayout(new GridLayout ());
		JLabel l = new JLabel(displayImage);
	
		f.add(l);
		
		f.setVisible(true);
		f.pack();
	}
	
	public void calculate () {
		Thread t1 = new Thread (new Runnable () {
			@Override
			public void run() {
				for (int yLoc = 0; yLoc < imageSize/4; yLoc++) {
					for (int xLoc = 0; xLoc < imageSize; xLoc++) {
						processPixel (xLoc, yLoc);
					}
				}
			}
		});
		Thread t2 = new Thread (new Runnable () {
			@Override
			public void run() {
				for (int yLoc = imageSize/4; yLoc < imageSize/2; yLoc++) {
					for (int xLoc = 0; xLoc < imageSize; xLoc++) {
						processPixel (xLoc, yLoc);
					}
				}
			}
		});
		Thread t3 = new Thread (new Runnable () {
			@Override
			public void run() {
				for (int yLoc = imageSize/2; yLoc < imageSize*3/4; yLoc++) {
					for (int xLoc = 0; xLoc < imageSize; xLoc++) {
						processPixel (xLoc, yLoc);
					}
				}
			}
		});
		Thread t4 = new Thread (new Runnable () {
			@Override
			public void run() {
				for (int yLoc = imageSize*3/4; yLoc < imageSize; yLoc++) {
					for (int xLoc = 0; xLoc < imageSize; xLoc++) {
						processPixel (xLoc, yLoc);
					}
				}
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
		
		
		System.out.println("Calculations done.");
		
		try {
			File outputfile = new File("Mandelbrot.png");
		    ImageIO.write(i, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
