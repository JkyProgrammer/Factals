package anybrot;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import org.apache.commons.math3.complex.*;

public class Anybrot {

	// Equations:
	// Z = (Z^2) + c              Mandelbrot
	// Z = (Z^d) + c              Multibrot
	// Z = (Z^d) - (Z^(d-1)) + c  Power Difference
	// Z = (Z^2) - (Z^(1/2))      Inverse Power
	
	public int runSet (Complex c, int maxIterations) {
		Complex z = c;
		for (int its = 0; its < maxIterations; its++) {
			if (z.abs() > 2.0) return its;
			//Complex tmp = z.pow(power-1);
			z = z.pow(2).subtract (z.pow(0.5));
		}
		return maxIterations;
	}
	
	public static void main(String[] args) {
		runZoomDemo ();         
	}
	
	public static void runNormal () {
		float xPos = 0.0f;
		float yPos = 0.0f;
		Anybrot sgf = new Anybrot (1024, 20, xPos, yPos, 0.2f, true);
		sgf.prepare();
		sgf.calculate();
	}
	
	public static void runPowerDemo () {
		Anybrot sgf = new Anybrot (2048, 32, 0, 0, 0.3f, false);
		sgf.prepare();
		sgf.calculate();
		int num = 0;
		for (float p = -4.0f; p <= 4.0; p += 0.05) {
			System.out.println("Beginning analysis of power value " + p + ", at position: " + 0.0 + ", " + 0.0);
			sgf.setPower (p);
			sgf.calculate();
			sgf.save("Power Scroll 2/Power " + num);
			num++;
		}
	}
	
	// Requires inverse power fractal, 
	public static void runZoomDemo () {
		Anybrot sgf = new Anybrot (2048, 22, -1.7548777f, -1.4133075E-9f, 0.2f, false);
		sgf.prepare();
		int num = 0;
		for (float z = 0.2f; z <= 20000000; z += z/5) {
			System.out.println("Beginning analysis of zoom value " + z);
			sgf.setZoom(z);
			sgf.calculate();
			sgf.save("Zoom Scroll 1/Zoom " + num);
			num++;
		}
	}
	
	public Anybrot (int imageSizee, int maxIterationss, float xPoss, float yPoss, float zoomm, boolean shouldBeVisuall) {
		this.imageSize = imageSizee;
		this.maxIterations = maxIterationss;
		this.xPos = xPoss;
		this.yPos = yPoss;
		this.zoom = zoomm;
		this.shouldBeVisual = shouldBeVisuall;
		
		if (shouldBeVisual) {
			JFrame controlFrame = new JFrame ("Control Frame");
			controlFrame.setLayout(new GridLayout (8, 1));
			controlFrame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
			JLabel l1 = new JLabel ("Zoom value");
			tf1 = new JTextField ();
			tf1.setText("" + zoom);
			JLabel l2 = new JLabel ("Iteration depth limit");
			tf2 = new JTextField ();
			tf2.setText("" + maxIterations);
			JLabel l3 = new JLabel ("Position");
			
			xField = new JTextField ();
			xField.setText("" + xPos);
			
			yField = new JTextField ();
			yField.setText("" + yPos);
			
			JPanel pan = new JPanel ();
			pan.setLayout(new GridLayout (1, 2));
			pan.add(xField);
			pan.add(yField);
			
			powerField = new JTextField ();
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
						power = Float.parseFloat(powerField.getText());
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
					save ("Image Latest");
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
	}
	
	private int imageSize;
	private int maxIterations;
	private float xPos;
	private float yPos;
	private float zoom;
	private float power = 2;
	private BufferedImage i;
	private boolean shouldBeVisual;
	
	public void setPosition (float newXPos, float newYPos) {
		xPos = newXPos;
		yPos = newYPos;
	}
	
	public void setZoom (float newZoom) {
		zoom = newZoom;
	}
	
	public void setPower (float newPower) {
		power = newPower;
	}
	
	public void save (String name) {
		try {
			File outputfile = new File(name + ".png");
		    ImageIO.write(i, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void save () {
		save ("Mandelbrot-" + maxIterations + "-" + xPos + ":" + yPos + "-" + power + "-" + zoom);
	}
	
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
		
		if (shouldBeVisual) {
			displayImage.getImage().flush();
			f.repaint();
		}
	}
	
	private ImageIcon displayImage;
	private JFrame f;
	private JTextField tf1;
	private JTextField tf2;
	private JTextField xField;
	private JTextField yField;
	private JTextField powerField;
	
	public void prepare () {
		i = new BufferedImage (imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);
		if (shouldBeVisual) {
			displayImage = new ImageIcon (i);
			
			f = new JFrame ("Result");
			f.setSize(800, 800);
			f.setLayout(new GridLayout ());
			JLabel l = new JLabel(displayImage);
			
			f.add(l);
			f.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
			
			f.addMouseListener(new MouseListener () {

				@Override
				public void mouseClicked(MouseEvent e) {
					handleMouseClick (e);
				}

				@Override
				public void mousePressed(MouseEvent e) {}

				@Override
				public void mouseReleased(MouseEvent e) {}

				@Override
				public void mouseEntered(MouseEvent e) {}

				@Override
				public void mouseExited(MouseEvent e) {}
			});
			f.setVisible(true);
			f.pack();
		}
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
//		if (!shouldBeVisual) {
//			save ();
//		}
	}
	
	
	public void handleMouseClick (MouseEvent e) {
		float xPosition = e.getX()-(float)f.getWidth()/2f;
		xPosition /= (float)f.getWidth();
		
		float yPosition = (float)e.getY()-(float)f.getHeight()/2f;
		yPosition /= (float)f.getHeight();
		
		xPosition /= zoom;
		yPosition /= zoom;
		
		xPosition -= this.xPos;//*(float)this.zoom;
		yPosition -= this.yPos;//*(float)this.zoom;
		
		this.xPos = 0-xPosition;
		this.yPos = 0-yPosition;
		
		xField.setText("" + this.xPos);
		yField.setText("" + this.yPos);
		
		Thread t = new Thread (new Runnable () {
			@Override
			public void run() {
				calculate ();
			}
		});
		t.start();
	}
}
