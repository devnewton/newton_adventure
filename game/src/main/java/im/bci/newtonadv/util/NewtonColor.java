package im.bci.newtonadv.util;

public enum NewtonColor {
	white(1.0f, 1.0f, 1.0f, 0), red(1.0f,0.0f,0.0f, 4), green(0.0f,1.0f,0.0f, 8), blue(0.0f,0.0f,1.0f, 16);

	public final float r, g, b;
	public final int collisionBitmask;
	
	private NewtonColor(float r, float g, float b, int collisionBitmask) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.collisionBitmask = collisionBitmask;
	}
}
