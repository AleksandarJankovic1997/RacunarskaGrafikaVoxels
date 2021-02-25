package graphics3d.renderers;

import graphics3d.*;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.drawingx.gadgets.annotations.GadgetInteger;
import mars.functions.interfaces.Function2;
import mars.geometry.Vector;
import mars.random.sampling.Sampler;

public class PathTracer extends RendererBase {
	
	public static class Factory implements Function2<Renderer, Scene, Vector> {
		@GadgetInteger(min = 0, max = 1000)
		int depthLimit = 9;
		
		@GadgetDouble(p = 0.0, q = 1.0)
		double rLens = 0.0;
		
		@GadgetDouble(p = 0, q = 10)
		double focalLength = 1.0;
		
		@Override
		public Renderer at(Scene scene, Vector imageSize) {
			return new PathTracer(scene, imageSize, depthLimit, rLens, focalLength);
		}
	}
	
	
	private final int depthLimit;
	
	
	public PathTracer(Scene scene, Vector imageSize, int depthLimit, double rLens, double focalLength) {
		super(scene, imageSize, rLens, focalLength);
		this.depthLimit = depthLimit;
	}
	
	
	@Override
	protected Color sampleRay(Ray r) {
		return sampleRay(r, depthLimit);
	}
	
	
	Sampler sampler = new Sampler();
	
	private Color sampleRay(Ray ray, int depthRemaining) {
		if (depthRemaining <= 0) return Color.BLACK;
		
		Collision collision = collider.collide(ray);
		
		Body body = collision.body();
		if (body == null) return scene.colorBackground();
		
		Vec3 p = ray.at(collision.hit().t());                    // Point of collision
		Vec3 i = ray.d().inverse();                              // Incoming direction
		Vec3 n_ = collision.hit().n_();                          // Normal to the body surface at the point of collision
		
		Material material = body.materialAt(p);
		
		
		Color result = material.emittance();
		
		BSDFResult bsdfResult = material.bsdf().sample(sampler, n_, i);
		
		if (bsdfResult.color().notZero()) {
			Color follow = sampleRay(Ray.pd(p, bsdfResult.out()), depthRemaining - 1);
			result = result.add(follow.mul(bsdfResult.color()));
		}
		
		return result;
	}
	
}

