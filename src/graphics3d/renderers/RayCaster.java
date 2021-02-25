package graphics3d.renderers;

import graphics3d.*;
import mars.functions.interfaces.Function2;
import mars.geometry.Vector;

public class RayCaster extends RendererBase {
	
	public static class Factory implements Function2<Renderer, Scene, Vector> {
		@Override
		public Renderer at(Scene scene, Vector imageSize) {
			return new RayCaster(scene, imageSize);
		}
	}
	
	
	public RayCaster(Scene scene, Vector imageSize) {
		super(scene, imageSize);
	}
	

	
	@Override
	Color sampleRay(Ray ray) {
		Collision collision = collider.collide(ray);
		Body body = collision.body();                 // The first body the ray will hit.
		
		if (body == null) return scene.colorBackground();
		
		Vec3 p = ray.at(collision.hit().t());         // The point of the collision.
		Vec3 n_ = collision.hit().n_();               // The normal at the point of the collision.
		
		Material material = body.materialAt(p);       // The material at the point of the intersection.
		
		Color totalLight = Color.BLACK;               // Accumulating the total light contributions from all light sources into this variable.
		
		for (Light light : scene.lights()) {
			Vec3 l = light.p().sub(p);                // The Vector to the light.
			
			double lLSqr = l.lengthSquared();
			double lL = Math.sqrt(lLSqr);
			
			double cosLN = l.dot(n_) / lL;
			
			if (cosLN > 0) {                          // If the light is above the surface.
				totalLight = totalLight.add(light.c().mul(cosLN / lLSqr)); // The brightness is proportional to the cosine. Using the inverse square law.
			}
		}
		
		return totalLight.mul(material.diffuse());
	}
}
