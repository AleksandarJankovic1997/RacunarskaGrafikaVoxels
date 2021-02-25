package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Ball;
import graphics3d.solids.HalfSpace;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.functions.interfaces.Function0;
import mars.geometry.Vector;

import java.util.List;

public class Mirrors implements Scene {
	
	public static class Factory implements Function0<Scene> {
		@GadgetDouble
		double k = 1.0;
		
		@GadgetDouble
		double phi = 0.0;
		
		@GadgetDouble(p = -10, q = 10)
		double cameraZ = -3.0;
		
		@Override
		public Scene at() {
			return new Mirrors(k, phi, cameraZ);
		}
	}
	
	
	private final List<Body> bodies;
	private final List<Light> lights;
	private final Transform cameraTransform;
	
	
	public Mirrors(double k, double phi, double cameraZ) {
		Material material  = Material.diffuse(Color.gray(0.750));
		Material materialR = Material.diffuse(Color.hsb(  0, 0.75, 0.750));
		Material materialG = Material.diffuse(Color.hsb(120, 0.75, 0.750));
		
		Material materialBall = new Material(Color.gray(1-k), Color.BLACK, 0, Color.gray(k));
		
		
		
		double r = 0.5;
		
		bodies = List.of(
				Body.uniform(HalfSpace.pn(Vec3.xyz(-1, 0, 0), Vec3.xyz( 1, 0, 0)), materialR),
				Body.uniform(HalfSpace.pn(Vec3.xyz( 1, 0, 0), Vec3.xyz(-1, 0, 0)), materialG),
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0,-1, 0), Vec3.xyz( 0, 1, 0)), material),
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0, 1, 0), Vec3.xyz( 0,-1, 0)), material),
//		        Body.uniform(HalfSpace.pn(Vec3.xyz( 0, 0,-1), Vec3.xyz( 0, 0, 1)), material),
//				Body.uniform(HalfSpace.pn(Vec3.xyz( 0, 0, 1), Vec3.xyz( 0, 0,-1)), material),
				
				Body.uniform(Ball.cr(Vec3.pz(Vector.polar(r, phi + 0.0/3), 0), 0.4), materialBall),
				Body.uniform(Ball.cr(Vec3.pz(Vector.polar(r, phi + 1.0/3), 0), 0.4), materialBall),
				Body.uniform(Ball.cr(Vec3.pz(Vector.polar(r, phi + 2.0/3), 0), 0.4), materialBall)
				
//				Body.uniform(Ball.cr(Vec3.xyz(-0.5, -0.5, -0.5), 0.3), materialBall),
//				Body.uniform(Ball.cr(Vec3.xyz( 0.5, -0.5, -0.5), 0.3), materialBall)
//				Body.uniform(Ball.cr(Vec3.xyz( 0.0,  0.0, -0.5), 0.3), materialBall),
//				Body.uniform(Ball.cr(Vec3.xyz( 0.0,  0.0,  0.5), 0.3), materialBall)
		);
		
		
		double d = 0.6;
		
		lights = List.of(
				Light.pc(Vec3.xyz(-d, 0.9, -d), Color.hsb(0, 0, 1)),
				Light.pc(Vec3.xyz(-d, 0.9,  d), Color.hsb(0, 0, 1)),
				Light.pc(Vec3.xyz( d, 0.9, -d), Color.hsb(0, 0, 1)),
				Light.pc(Vec3.xyz( d, 0.9,  d), Color.hsb(0, 0, 1))
		);
		
		
		cameraTransform = Transform.IDENTITY
				.andThen(Transform.translation(Vec3.xyz(0, 0, cameraZ))
		);
	}
	
	
	@Override
	public Transform cameraTransform() {
		return cameraTransform;
	}
	
	@Override
	public List<Body> bodies() {
		return bodies;
	}
	
	
	// Raytracer
	
	
	@Override
	public List<Light> lights() {
		return lights;
	}
}
