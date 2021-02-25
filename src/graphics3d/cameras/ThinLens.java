package graphics3d.cameras;

import graphics3d.Camera;
import graphics3d.Ray;
import graphics3d.Vec3;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.geometry.Vector;
import mars.random.sampling.Sampler;

public class ThinLens implements Camera {
	
	@GadgetDouble
	private final double rLens, focalLength;
	private final Sampler sampler = new Sampler();
	
	
	public ThinLens(double rLens, double focalLength) {
		this.rLens = rLens;
		this.focalLength = focalLength;
	}
	
	
	@Override
	public Ray sampleExitingRay(Vector pixel) {
		Vec3 q = Vec3.pz(pixel, 1).mul(focalLength);
		Vec3 o = Vec3.pz(sampler.randomInDisk(rLens), 0);
		return Ray.pq(o, q);
	}
	
}
