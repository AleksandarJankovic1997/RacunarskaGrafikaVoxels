package graphics3d.renderers;

import graphics3d.*;
import mars.functions.interfaces.Function2;
import mars.geometry.Vector;

public class Sonar extends RendererBase {
	
	public static class Factory implements Function2<Renderer, Scene, Vector> {
		@Override
		public Renderer at(Scene scene, Vector imageSize) {
			return new Sonar(scene, imageSize);
		}
	}
	
	
	public Sonar(Scene scene, Vector imageSize) {
		super(scene, imageSize);
	}
	

	
	@Override
	Color sampleRay(Ray ray) {
		Collision collision = collider.collide(ray);
		Body body = collision.body();                   // The first body the ray will hit.
		double d = collision.hit().t() / ray.d().length();    // The time of the hit
		Vec3 p = ray.at(d);                             // The point of the hit
		
		if (body == null) {
			return Color.BLACK;
		} else {
			Material material = body.materialAt(p);     // The material at the hit point.
			return material.diffuse().mul(1 / d);       // A quick hack to show distance - has no basis in reality.
		}
	}
}
