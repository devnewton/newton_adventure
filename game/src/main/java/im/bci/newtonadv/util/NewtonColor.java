package im.bci.newtonadv.util;

public enum NewtonColor {
	white(1.0f, 1.0f, 1.0f), red(1.0f,0.0f,0.0f), green(0.0f,1.0f,0.0f), blue(0.0f,0.0f,1.0f);

	public final float r, g, b;
	
	private NewtonColor(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
}
