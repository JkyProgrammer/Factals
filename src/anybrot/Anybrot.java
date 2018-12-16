package anybrot;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.math3.complex.Complex;

public class Anybrot {
	public int runSet (double x, double y, int maxIterations) {
		Complex c = new Complex (x, y);
		switch (equationMode) {
		case CUSTOM:
			return Equations.custom(c, maxIterations, operation);
		case MANDELBROT:
			return Equations.mandelbrot (c, maxIterations);
		case MULTIBROT:
			return Equations.multibrot (c, maxIterations, power);
		case RECIPROCALPOWER:
			return Equations.reciprocalPower(c, maxIterations, power);
		case ZPOWER:
			return Equations.zPower(c, maxIterations);
		case POWERDIFFERENCE:
			return Equations.powerDifference(c, maxIterations, power);
		}
		return 0;
	}
	
	
	public static void main(String[] args) {
		runZoomDemo3();
		
		runNormal();
	}
	
	public static void runNormal () {
		float xPos = 0.0f;
		float yPos = 0.0f;
		Anybrot sgf = new Anybrot (1024, 100, xPos, yPos, 0.3f, true);
		sgf.setPower(2.0f);
		sgf.prepare();
		//sgf.calculate();
		
//		Anybrot sgf = new Anybrot (4096, 512, 1.0353411f, 0.10399851f, 6000f, true);
//		sgf.prepare();
//		sgf.setPower (-5);
//		sgf.calculate();
//		sgf.save("High calc");
	}
	
	public static void runZoomDemo3() {
		Anybrot sgf = new Anybrot (1024, 800, 0.65771735f, -1.2012954f, 0.2f, true);
		sgf.prepare();
		sgf.setPower (-2.10f);
		sgf.setEquationMode(Equation.RECIPROCALPOWER);
		sgf.setColourPallet(ColourPallet.greenToYellow);
		sgf.setColourGraduations(100);
		
		try {
			ImageOutputStream output = new FileImageOutputStream(new File("Images/Reciprocal Zoom Gif.gif"));
			GifSequenceWriter writer = new GifSequenceWriter(output, sgf.i.getType(), 50, false);
			for (float z = 0.2f; z <= 50000; z += z/10) {
				System.out.println("Beginning analysis of zoom value " + z);
				sgf.setZoom(z);
				sgf.calculate();
				writer.writeToSequence(sgf.i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void runPowerDemo3 () {
		Anybrot sgf = new Anybrot (256, 200, 0, 0, 0.2f, false);
		sgf.prepare();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sgf.setEquationMode(Equation.RECIPROCALPOWER);
		sgf.setColourPallet(ColourPallet.greenToYellow);
		sgf.setColourGraduations(20);
		int num = 0;
		ImageOutputStream output;
		try {
			output = new FileImageOutputStream(new File("Power Scroll 3/Combined Gif.gif"));
			GifSequenceWriter writer = new GifSequenceWriter(output, sgf.i.getType(), 20, false);
			for (float p = -8.0f; p <= 8.0; p += 0.02) {
				System.out.println("Beginning analysis of power value " + p + ", at position: " + 0.0 + ", " + 0.0);
				sgf.setPower (p);
				sgf.calculate();
				//sgf.save("Power Scroll 3/Power " + num);
				writer.writeToSequence(sgf.i);
				num++;
			}
			writer.close();
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	
	// Requires multibrot fractal
	public static void runZoomDemo2() { 
		Anybrot sgf = new Anybrot (256, 512, 1.0353411f, 0.10399851f, 0.2f, true);
		sgf.prepare();
		sgf.setPower (-5);
		int num = 0;
		for (float z = 20f; z <= 200000000; z += z/10) {
			System.out.println("Beginning analysis of zoom value " + z);
			sgf.setZoom(z);
			sgf.calculate();
			sgf.save("Zoom Scroll 2/Zoom " + num);
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
			setupControlGUI ();
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
	private int colourGraduationDetail = 50;
	private Equation equationMode;
	Operation operation = EquationProcessor.generateFunction("(Z^2) + C");
	
	public void setPosition (float newXPos, float newYPos) {
		xPos = newXPos;
		yPos = newYPos;
		if (shouldBeVisual) {
			xField.setText("" + xPos);
			yField.setText("" + yPos);
		}
	}
	
	public void setZoom (float newZoom) {
		zoom = newZoom;
		if (shouldBeVisual) {
			tf1.setText("" + zoom);
		}
	}
	
	public void setPower (float newPower) {
		power = newPower;
		if (shouldBeVisual) {
			powerField.setText("" + power);
		}
	}
	
	public void setMaxIterations (int newMaxIterations) {
		maxIterations = newMaxIterations;
		if (shouldBeVisual) {
			tf2.setText("" + maxIterations);
		}
	}
	
	public void setColourPallet (int cp) {
		selectedPallet = cp;
		if (shouldBeVisual) {
			palletSelector.setSelectedIndex(selectedPallet);
		}
	}
	
	public void setEquationMode (Equation e) {
		setEquationMode (e, null);
	}
	
	public void setColourGraduations (int g) {
		colourGraduationDetail = g;
		if (shouldBeVisual) {
			tf3.setText("" + colourGraduationDetail);
		}
		colourSetup();
	}
	
	public void setEquationMode (Equation e, Operation customEquation) {
		equationMode = e;
		if (shouldBeVisual) {
			switch (e) {
			case MANDELBROT:
				equationField.setEnabled(false);
				powerField.setEnabled (false);
				break;
			case MULTIBROT:
				equationField.setEnabled(false);
				powerField.setEnabled (true);
				break;
			case RECIPROCALPOWER:
				equationField.setEnabled(false);
				powerField.setEnabled (true);
				break;
			case ZPOWER:
				equationField.setEnabled(false);
				powerField.setEnabled (false);
				break;
			case POWERDIFFERENCE:
				equationField.setEnabled(false);
				powerField.setEnabled (true);
				break;
			case CUSTOM:
				equationField.setEnabled(true);
				powerField.setEnabled (false);
				break;
			default:
				System.out.println("Oh."); // WHAT?????????
			}
		}
		if (e == Equation.CUSTOM) {
			operation = customEquation;
		}
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
		
		int result = runSet (xValue, yValue, maxIterations);
		
		float mappedGrey = 1f - (float)result/(float)maxIterations;
		
		
		
		Color col = getColour (mappedGrey);
		//Color col = new Color (mappedGrey, mappedGrey, mappedGrey);
		int rgb = col.getRGB();
		i.setRGB(xLoc, yLoc, rgb);
		
		if (shouldBeVisual) {
			displayImage.getImage().flush();
			f.repaint();
		}
	}
	
	private int selectedPallet = ColourPallet.greenToYellow;
	private ArrayList<Color> colours;
	private Color getColour (float initialMapping) {
		float g = initialMapping * maxIterations;
		if (initialMapping != 0.0) {
			int m = (int)(g-0.5);
			m = m % colours.size();
			return colours.get(m);
		} else {return new Color (0,0,0);}
	}

	private void colourSetup () {
		colours.clear();
		ArrayList<Color> handles = new ArrayList<Color> ();
		if (selectedPallet == ColourPallet.greenToYellow) {
			handles.add(new Color (255, 215, 0));
			handles.add(new Color (255, 184, 28));
			handles.add(new Color (246, 141, 46));
			handles.add(new Color (205, 84, 91));
			handles.add(new Color (135, 24, 157));
			handles.add(new Color (0, 171, 132));
		} else if (selectedPallet == ColourPallet.greyScale) {
			handles.add(new Color (255, 255, 255));
			handles.add(new Color (0, 0, 0));
		} else if (selectedPallet == ColourPallet.blueToWhite) {
			handles.add(new Color (0, 125, 255));
			handles.add(new Color (0, 0, 255));
			handles.add(new Color (120, 120, 255));
			handles.add(new Color (255, 255, 255));
		}
		
		int numTotalColours = colourGraduationDetail;
		int numColoursPerHandle = numTotalColours/(handles.size()-1);
		
		for (int l = 0; l < handles.size()-1; l++) {
			Color c1 = handles.get(l);
			Color c2 = handles.get(l+1);
			
			float rStep = (float)(c2.getRed() - c1.getRed())/(float)numColoursPerHandle;
			float gStep = (float)(c2.getGreen() - c1.getGreen())/(float)numColoursPerHandle;
			float bStep = (float)(c2.getBlue() - c1.getBlue())/(float)numColoursPerHandle;
			
			for (int i = 0; i < numColoursPerHandle; i++) {
				float r = c1.getRed() + (rStep*i);
				r /= 255;
				float g = c1.getGreen() + (gStep*i);
				g /= 255;
				float b = c1.getBlue() + (bStep*i);
				b /= 255;
				
				colours.add(0, new Color (r, g, b));
			}
		}
		
	}
	
	public void prepare () {
		colours = new ArrayList<Color> ();
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
		// Set up colours
		colourSetup ();
		
		// Set up threads
		ArrayList<Thread> ts = new ArrayList<Thread> ();
		
		int numThreads = Runtime.getRuntime().availableProcessors();
		System.out.println("Starting calculation with " + numThreads + " threads.");
		int step = imageSize/numThreads;
		
		int startLoc = 0;
		int endLoc = 0 + step;
		
		for (int i = 0; i < numThreads; i++) {
			int sLoc = startLoc;
			int eLoc = endLoc;
			Thread t = new Thread (new Runnable () {
				@Override
				public void run() {
					for (int yLoc = sLoc; yLoc < eLoc; yLoc++) {
						for (int xLoc = 0; xLoc < imageSize; xLoc++) {
							processPixel (xLoc, yLoc);
						}
					}
				}
			});
			ts.add(t);
			
			startLoc = endLoc;
			endLoc = startLoc + step;
		}
		for (Thread t : ts) {
			t.start();
		}
		
		try {
			for (Thread t : ts) {
				t.join();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Calculations done.");
	}
	
	
	public void handleMouseClick (MouseEvent e) {
		float xPosition = e.getX()-(float)f.getWidth()/2f;
		xPosition /= (float)f.getWidth();
		
		float yPosition = (float)e.getY()-(float)f.getHeight()/2f;
		yPosition /= (float)f.getHeight();
		
		xPosition /= zoom;
		yPosition /= zoom;
		
		xPosition -= this.xPos;
		yPosition -= this.yPos;
		
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
	
	private ImageIcon displayImage;
	private JFrame f;
	private JTextField tf1;
	private JTextField tf2;
	private JTextField xField;
	private JTextField yField;
	private JTextField powerField;
	private JComboBox<String> palletSelector;
	private JTextField tf3;
	private JComboBox<String> equationSelector;
	private JTextField equationField;
	
	private void setupControlGUI () {
		JFrame controlFrame = new JFrame ("Control Frame");
		controlFrame.setLayout(new GridLayout (16, 1));
		controlFrame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		JLabel l1 = new JLabel ("Zoom Value");
		tf1 = new JTextField ();
		tf1.setText("" + zoom);
		JLabel l2 = new JLabel ("Iteration Limit");
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
		JLabel l4 = new JLabel ("Exponent Factor");
		
		palletSelector = new JComboBox<String> ();
		
		palletSelector.addItem("Greyscale");
		palletSelector.addItem("Green to Yellow");
		palletSelector.addItem("Blue to White");
		
		palletSelector.setSelectedIndex(selectedPallet);
		
		palletSelector.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedPallet = palletSelector.getSelectedIndex();
			}
		});
		JLabel l5 = new JLabel ("Colour Scheme");
		
		tf3 = new JTextField ();
		tf3.setText ("" + this.colourGraduationDetail);
		JLabel l6 = new JLabel ("Number of Colour Graduations");
		
		JLabel l7 = new JLabel ("Custom Equation Z = ");
		equationField = new JTextField ("(Z^2) + C");
		JPanel pan2 = new JPanel ();
		pan2.setLayout(new GridLayout (1, 2));
		pan2.add(l7);
		pan2.add(equationField);
		
		
		equationSelector = new JComboBox<String> ();
		equationSelector.addItem("Mandelbrot Set");
		equationSelector.addItem("Multibrot Base");
		equationSelector.addItem("Reciprocal Power Base");
		equationSelector.addItem("Z Power Set");
		equationSelector.addItem("Power Difference Base");
		equationSelector.addItem("Custom");
		
		equationSelector.addItemListener(new ItemListener () {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					switch ((String)e.getItem()) {
					case "Mandelbrot Set":
						equationField.setEnabled(false);
						powerField.setEnabled (false);
						equationMode = Equation.MANDELBROT;
						break;
					case "Multibrot Base":
						equationField.setEnabled(false);
						powerField.setEnabled (true);
						equationMode = Equation.MULTIBROT;
						break;
					case "Reciprocal Power Base":
						equationField.setEnabled(false);
						powerField.setEnabled (true);
						equationMode = Equation.RECIPROCALPOWER;
						break;
					case "Z Power Set":
						equationField.setEnabled(false);
						powerField.setEnabled (false);
						equationMode = Equation.ZPOWER;
						break;
					case "Power Difference Base":
						equationField.setEnabled(false);
						powerField.setEnabled (true);
						equationMode = Equation.POWERDIFFERENCE;
						break;
					case "Custom":
						equationField.setEnabled(true);
						powerField.setEnabled (false);
						equationMode = Equation.CUSTOM;
						break;
					default:
						System.out.println("Oh."); // WHAT?????????
					}
				}
			}
		});
		
		equationSelector.setSelectedIndex(0);
		equationField.setEnabled(false);
		powerField.setEnabled (false);
		equationMode = Equation.MANDELBROT;
		
		
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
					colourGraduationDetail = Integer.parseInt(tf3.getText());
					if (equationMode == Equation.CUSTOM) {
						operation = EquationProcessor.generateFunction(equationField.getText());
					}
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
		
		controlFrame.add(l4);
		controlFrame.add(powerField);
		
		controlFrame.add(l5);
		controlFrame.add(palletSelector);
		
		controlFrame.add(l6);
		controlFrame.add(tf3);
		
		controlFrame.add(equationSelector);
		controlFrame.add(pan2);
		
		controlFrame.add(b1);
		controlFrame.add(b2);
		
		controlFrame.setSize(300, 400);
		controlFrame.setVisible(true);
	}
}
