package graphics3d.cameras;

import graphics3d.Camera;
import graphics3d.Ray;
import graphics3d.Vec3;
import mars.geometry.Vector;

public class Perspective implements Camera {
	
	private final double z;
	
	public Perspective(double z) {
		this.z = z;
	}
	
	
	public Perspective() {
		this(1.0);
	}
	
	
	@Override
	public Ray sampleExitingRay(Vector pixel) {
		return Ray.pd(
				Vec3.ZERO,
				Vec3.pz(pixel, z)
		);
	}
	
}
