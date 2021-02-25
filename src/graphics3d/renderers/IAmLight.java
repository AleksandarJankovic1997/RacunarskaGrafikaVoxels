package graphics3d.renderers;

import graphics3d.*;
import mars.functions.interfaces.Function2;
import mars.geometry.Vector;

public class IAmLight extends RendererBase {
	
	public static class Factory implements Function2<Renderer, Scene, Vector> {
		@Override
		public Renderer at(Scene scene, Vector imageSize) {
			return new IAmLight(scene, imageSize);
		}
	}
	
	
	public IAmLight(Scene scene, Vector imageSize) {
		super(scene, imageSize);
	}
	

	
	@Override
	Color sampleRay(Ray ray) {
		Collision collision = collider.collide(ray);
		Body body = collision.body();                     // The first body the ray will hit.
		
		if (body == null) return Color.BLACK;
		
		Vec3 p = ray.at(collision.hit().t());         // The point of the collision.
		Vec3 n_ = collision.hit().n_();               // The normal at the point of the collision.
		Vec3 l = ray.p().sub(p);                          // The vector to the light (which equals the ray origin for this renderer).
		
		double lLSqr = l.lengthSquared();
		double lL = Math.sqrt(lLSqr);
		
		Material material = body.materialAt(p);           // The material at the point of the intersection.
		
		double cosLN = l.dot(n_) / lL;
		
		if (cosLN < 0) {
			return Color.BLACK;
		} else {
			return material.diffuse().mul(cosLN / lLSqr); // The brightness is proportional to the cosine. Using the inverse square law.
		}
	}
}
