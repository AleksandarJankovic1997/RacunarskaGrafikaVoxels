package graphics3d.renderers;

import graphics3d.*;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.drawingx.gadgets.annotations.GadgetInteger;
import mars.functions.interfaces.Function2;
import mars.geometry.Vector;

public class RayTracer extends RendererBase {
	
	
	public static class Factory implements Function2<Renderer, Scene, Vector> {
		@GadgetInteger(min = 0, max = 1000)
		int depthLimit = 9;
		
		@GadgetDouble(p = 0.0, q = 1.0)
		double rLens = 0.0;
		
		@GadgetDouble(p = 0, q = 10)
		double focalLength = 1.0;
		
		@Override
		public Renderer at(Scene scene, Vector imageSize) {
			return new RayTracer(scene, imageSize, depthLimit, rLens, focalLength);
		}
	}
	
	
	private final int depthLimit;
	
	
	public RayTracer(Scene scene, Vector imageSize, int depthLimit, double rLens, double focalLength) {
		super(scene, imageSize, rLens, focalLength);
		this.depthLimit = depthLimit;
	}
	
	
	
	@Override
	Color sampleRay(Ray ray) {
		return sampleRay(ray, depthLimit);
	}
	
	
	Color sampleRay(Ray ray, int depthRemaining) {
		if (depthRemaining == 0) {
			return Color.BLACK;
		}
		
		Collision collision = collider.collide(ray);
		Body body = collision.body();                 // The first body the ray will hit.
		
		if (body == null) return scene.colorBackground();
		
		Vec3 p = ray.at(collision.hit().t());         // The point of the collision.
		Vec3 n_ = collision.hit().n_();               // The normal at the point of the collision.
		Vec3 i = ray.d().inverse();
		Vec3 r = Utils.reflectN(i, n_);
		double lI = r.length(); // Equals the length of r
		
		Material material = body.materialAt(p);       // The material at the point of the intersection.
		
		Color totalDiffuse  = Color.BLACK;            // Accumulating the total light contributions from all light sources into this variable.
		Color totalSpecular = Color.BLACK;
		
		for (Light light : scene.lights()) {
			Vec3 l = light.p().sub(p);                // The Vector to the light.
			
			Ray rayLight = Ray.pd(p, l);
			
			if (!collider.collidesIn01(rayLight)) {   // Checking if there is an object between p and light. If yes, we do not consider this light source.
				double lLSqr = l.lengthSquared();
				double lL = Math.sqrt(lLSqr);
				
				// Diffuse
				
				double cosLN = l.dot(n_) / lL;
				
				if (cosLN > 0) {                      // If the light is above the surface.
					totalDiffuse = totalDiffuse.add(light.c().mul(cosLN / lLSqr));
				}
				
				// Specular
				
				if (material.specular().notZero()) {
					double cosLR = l.dot(r) / (lL * lI);
					if (cosLR > 0) {
						double phong = Math.pow(cosLR, material.shininess());
						totalSpecular = totalSpecular.add(light.c().mul(phong / lLSqr));
					}
				}
			}
		}
		
		Color result = Color.BLACK;
		
		result = result.add(totalDiffuse .mul(material.diffuse ()));
		result = result.add(totalSpecular.mul(material.specular()));
		

		// Reflection
		
		if (material.reflective().notZero()) {
			Color reflection = sampleRay(Ray.pd(p, r), depthRemaining - 1);
			result = result.add(reflection.mul(material.reflective()));
		}
		
		
		// Refraction
		
		if (material.refractive().notZero()) {
			double ri = material.refractiveIndex();
			double k = 1;
			
			double c1 = i.dot(n_) / lI;
			if (c1 < 0) {
				// We are exiting the object
				ri = 1.0 / ri;
				k = -1;
			}
//			double s1 = Math.sqrt(1 - c1*c1);
//			double s2 = s1 / ri;
//			double c2 = Math.sqrt(1 - s2 * s2);
			// That is ok, but we'll get one square root less if we inline s2 (because we are squaring it)
			// s2^2 = (s1 / ri)^2 = s1^2 / ri^2 = (1 - c1^2) / ri^2
			// c2^2 = 1 - s2^2 = 1 - (1 - c1^2) / ri^2
			double c2Sqr = 1 - (1 - c1 * c1) / (ri * ri);
			
			Vec3 f;
			if (c2Sqr > 0) {
				double c2 = Math.sqrt(c2Sqr);
				f = n_.mul(c1/ri - k * c2).sub(i.div(ri*lI)); // refraction
			} else {
				// total reflection
				f = r;
			}
			
			Color refraction = sampleRay(Ray.pd(p, f), depthRemaining - 1);
			result = result.add(refraction.mul(material.refractive()));
		}
		
		
		return result;
	}
}
