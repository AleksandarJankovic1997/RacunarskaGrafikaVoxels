package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Ball;
import graphics3d.solids.HalfSpace;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.drawingx.gadgets.annotations.GadgetInteger;
import mars.functions.interfaces.Function0;
import mars.geometry.Vector;

import java.util.ArrayList;
import java.util.List;

public class CampfireGlass implements Scene {
	
	public static class Factory implements Function0<Scene> {
		@GadgetInteger(min = 0)
		int n = 12;
		
		@GadgetDouble(p = 0, q = 1)
		double cameraAngle = 0.0625;
		
		@GadgetDouble(p = 0, q = 4)
		double refractiveIndex = 1.5;

		@Override
		public Scene at() {
			return new CampfireGlass(n, cameraAngle, refractiveIndex);
		}
	}

	
	private final List<Body> bodies;
	private final List<Light> lights;
	private final Transform cameraTransform;

	
	
	public CampfireGlass(int n, double cameraAngle, double refractiveIndex) {
		bodies = new ArrayList<>();
		lights = new ArrayList<>();
		
		Material materialFloor = new Material(Color.gray(0.8));
		
		bodies.add(Body.uniform(HalfSpace.pn(Vec3.xyz( 0, -1, 0), Vec3.xyz( 0, 1, 0)), materialFloor));
		
		for (int i = 0; i < n; i++) {
			double phi = 1.0 * i / n;
			Vector pxz = Vector.polar(6, phi);
			Vec3 c = Vec3.xyz(pxz.x(), 0, pxz.y());

			bodies.add(
					Body.uniform(
							Ball.cr(c, 1.0),
							Material.glass(refractiveIndex, 0.06)
					)
			);
		}
		
		for (int j = 0; j < 6; j++) {
			lights.add(Light.pc(Vec3.xyz(0, 2*j, 0), Color.gray(1.0)));
		}
		
		
		cameraTransform = Transform.IDENTITY
				.andThen(Transform.translation(Vec3.xyz(0, 0, -20)))
				.andThen(Transform.rotationAboutX(cameraAngle))
				;
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
