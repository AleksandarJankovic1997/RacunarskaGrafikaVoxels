package graphics3d;

public class BSDFResult {
	public static final BSDFResult ABSORBED = new BSDFResult(null, Color.BLACK);
	
	private final Vec3 out;
	private final Color color;
	
	
	
	public BSDFResult(Vec3 out, Color color) {
		this.out = out;
		this.color = color;
	}
	
	
	public Vec3 out() {
		return out;
	}
	
	
	public Color color() {
		return color;
	}
	
}
