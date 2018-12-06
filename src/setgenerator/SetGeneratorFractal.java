package setgenerator;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
			z = z.power(power-1).plus(c);
		}
		return maxIterations;
	}
	
	public static void main(String[] args) {
		float xPos = 0.0f;
		float yPos = 0.7f;
		SetGeneratorFractal sgf = new SetGeneratorFractal (2048, 64, xPos, yPos, 0.1f);
		sgf.prepare();
		sgf.calculate();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
//		for (float f = 0.5f; f < 3.0; f+= 0.5) {
//			System.out.println("Beginning analysis of zoom value " + f + ", at position: " + xPos + ", " + yPos);
//			sgf.setZoom(f);
//			sgf.calculate();
//		}
		
		System.out.println("Zooming finished.");
	}
	
	public SetGeneratorFractal (int imageSizee, int maxIterationss, float xPoss, float yPoss, float zoomm) {
		this.imageSize = imageSizee;
		this.maxIterations = maxIterationss;
		this.xPos = xPoss;
		this.yPos = yPoss;
		this.zoom = zoomm;
		
		JFrame controlFrame = new JFrame ("Control Frame");
		controlFrame.setLayout(new GridLayout (8, 1));
		
		JLabel l1 = new JLabel ("Zoom value");
		JTextField tf1 = new JTextField ();
		tf1.setText("" + zoom);
		JLabel l2 = new JLabel ("Iteration depth limit");
		JTextField tf2 = new JTextField ();
		tf2.setText("" + maxIterations);
		JLabel l3 = new JLabel ("Position");
		
		JTextField xField = new JTextField ();
		xField.setText("" + xPos);
		
		JTextField yField = new JTextField ();
		yField.setText("" + yPos);
		
		JPanel pan = new JPanel ();
		pan.setLayout(new GridLayout (1, 2));
		pan.add(xField);
		pan.add(yField);
		
		JTextField powerField = new JTextField ();
		powerField.setText("" + power);
		
		JButton b1 = new JButton ("Redraw image");
		b1.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					zoom = Float.parseFloat(tf1.getText());
					maxIterations = Integer.parseInt(tf2.getText());
					xPos = Float.parseFloat(xField.getText());
					yPos = Float.parseFloat(yField.getText());
					power = Integer.parseInt(powerField.getText());
					Thread t = new Thread (new Runnable () {
						@Override
						public void run() {
							calculate ();
						}
					});
					t.start();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		JButton b2 = new JButton ("Save Image");
		b2.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				save ();
			}
		});
		
		controlFrame.add(l1);
		controlFrame.add(tf1);
		controlFrame.add(l2);
		controlFrame.add(tf2);
		controlFrame.add(l3);
		controlFrame.add(pan);
		controlFrame.add(powerField);
		controlFrame.add(b1);
		controlFrame.add(b2);
		
		controlFrame.setSize(200, 300);
		controlFrame.setVisible(true);
	}
	
	private int imageSize;
	private int maxIterations;
	private float xPos;
	private float yPos;
	private float zoom;
	private int power = 2;
	
	public void setPosition (float newXPos, float newYPos) {
		xPos = newXPos;
		yPos = newYPos;
	}
	
	public void setZoom (float newZoom) {
		zoom = newZoom;
	}
	
	public void save () {
		
		File folder = new File(".");
		File[] listOfFiles = folder.listFiles();
		int num = 0;
		
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].getName().length() > 10) {
				System.out.println(listOfFiles[i].getName().substring(0, 11));
				if (listOfFiles[i].getName().substring(0, 11).equals("Mandelbrot-")) {
					int n = Integer.parseInt(listOfFiles[i].getName().substring(11, 12));
					if (n > num) num = n;
				}
			}
		}
		
		num++;
		try {
			File outputfile = new File("Mandelbrot-" + num + ".png");
		    ImageIO.write(i, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private BufferedImage i;
	
	private void processPixel (int xLoc, int yLoc) {
		double xValue = (float)xLoc;
		xValue -= imageSize/2;
		
		xValue /= ((float)imageSize);
		
		xValue /= zoom;
		xValue -= xPos;
		
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
	}
	
}
