package graphics3d;


public class Light {

	private final Vec3 p;
	private final Color c;

	
	
	private Light(Vec3 p, Color c) {
		this.p = p;
		this.c = c;
	}
	
	public static Light pc(Vec3 p, Color c) {
		return new Light(p, c);
	}
	
	/** The light's position. */
	public Vec3 p() {
		return p;
	}
	
	/** The light's color. Can be arbitrarily bright (i.e. have values greater than 1). */
	public Color c() {
		return c;
	}
}
