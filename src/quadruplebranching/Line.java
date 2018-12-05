package quadruplebranching;

public class Line {
	
	public double x1, y1;
	public double x2, y2;
	
	public Line (double x1, double y1, double x2, double y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	public String svgDataValue () {
		return "<line stroke=\"black\" stroke-width=\"0.005\" x1=\"" + (x1 + 128) + "\" y1=\"" + (y1 + 128) + "\" x2=\"" + (x2 + 128) + "\" y2=\"" + (y2 + 128) + "\" />";
	}
}
