package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Ball;
import graphics3d.solids.HalfSpace;
import graphics3d.textures.Checkerboard;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.drawingx.gadgets.annotations.GadgetInteger;
import mars.functions.interfaces.Function0;
import mars.geometry.Vector;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class Campfire implements Scene {
	
	public static class Factory implements Function0<Scene> {
		@GadgetInteger(min = 0)
		int n = 12;
		
		@GadgetDouble(p = 0, q = 256)
		double shininess = 32;
		
		@GadgetDouble(p = 0, q = 1)
		double cameraAngle = 0.125;

		@Override
		public Scene at() {
			return new Campfire(n, shininess, cameraAngle);
		}
	}

	
	private final List<Body> bodies;
	private final List<Light> lights;
	private final Transform cameraTransform;

	
	
	public Campfire(int n, double shininess, double cameraAngle) {
		bodies = new ArrayList<>();
		lights = new ArrayList<>();
		
		Material materialFloor0 = new Material(Color.gray(0.9));
		Material materialFloor1 = new Material(Color.gray(0.7));
		Texture texture = new Checkerboard(Vector.xy(1), materialFloor0, materialFloor1);
		
//		bodies.add(Body.uniform(HalfSpace.pn(Vec3.xyz( 0, -1, 0), Vec3.xyz( 0, 1, 0)), materialFloor));
		bodies.add(Body.textured(HalfSpace.pn(Vec3.xyz( 0, -1, 0), Vec3.xyz( 0, 1, 0)), texture));
		
		for (int i = 0; i < n; i++) {
			double phi = 1.0 * i / n;
			Vector pxz = Vector.polar(6, phi);
			Vec3 c = Vec3.xyz(pxz.x(), 0, pxz.y());

			bodies.add(
					Body.uniform(
							Ball.cr(c, 1.0),
							new Material(
									Color.hsb(360 * phi, 0.8, 0.8),
									Color.WHITE,
									shininess
									)
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
	
	@Override
	public List<Light> lights() {
		return lights;
	}
}
