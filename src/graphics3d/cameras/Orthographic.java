package graphics3d.cameras;

import graphics3d.Camera;
import graphics3d.Ray;
import graphics3d.Vec3;
import mars.geometry.Vector;

public class Orthographic implements Camera {
	
	private final double z;
	
	
	public Orthographic(double z) {
		this.z = z;
	}
	
	
	public Orthographic() {
		this(1.0);
	}
	
	
	@Override
	public Ray sampleExitingRay(Vector pixel) {
		return Ray.pd(
				Vec3.pz(pixel.mul(z), 0.0),
				Vec3.EZ
		);
	}
}
