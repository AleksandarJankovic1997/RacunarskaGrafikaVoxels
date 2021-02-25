package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Ball;
import graphics3d.solids.HalfSpace;
import graphics3d.textures.Checkerboard;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.drawingx.gadgets.annotations.GadgetInteger;
import mars.drawingx.gadgets.annotations.GadgetLongRandomStream;
import mars.functions.interfaces.Function0;
import mars.geometry.Vector;
import mars.random.sampling.Sampler;

import java.util.ArrayList;
import java.util.List;

public class Room implements Scene {
	
	public static class Factory implements Function0<Scene> {
		@GadgetLongRandomStream
		long seed = 0;
		
		@GadgetInteger(min = 0)
		int nBalls  = 8;
		
		@GadgetInteger(min = 0)
		int nLights  = 8;
		
		@GadgetDouble(p = -0.1, q = 0.1)
		double phi = 0;
		
		@GadgetDouble(p = -10, q = 10)
		double z = -3;
		
		@Override
		public Scene at() {
			return new Room(seed, nBalls, nLights, phi, z);
		}
	}
	
	
	private final List<Body> bodies;
	private final List<Light> lights;
	private final double phi;
	private final double z;
	
	
	public Room(long seed, int nBalls, int nLights, double phi, double z) {
		this.phi = phi;
		this.z = z;
		
		Sampler sampler = new Sampler(seed);
		Sampler samplerB = new Sampler(sampler.rng().nextLong());
		Sampler samplerL = new Sampler(sampler.rng().nextLong());
		
		Material materialWall0 = new Material(Color.gray(0.9));
		Material materialWall1 = new Material(Color.gray(0.7));
		
		Texture textureWall = new Checkerboard(Vector.xy(0.25), materialWall0, materialWall1);
		
		bodies = new ArrayList<>();
		
		// Walls
		
		bodies.addAll(List.of(
				Body.textured(HalfSpace.pn(Vec3.xyz(-1, 0, 0), Vec3.xyz( 1, 0, 0)), textureWall),
				Body.textured(HalfSpace.pn(Vec3.xyz( 1, 0, 0), Vec3.xyz(-1, 0, 0)), textureWall),
				Body.textured(HalfSpace.pn(Vec3.xyz( 0,-1, 0), Vec3.xyz( 0, 1, 0)), textureWall),
				Body.textured(HalfSpace.pn(Vec3.xyz( 0, 1, 0), Vec3.xyz( 0,-1, 0)), textureWall),
//		        Body.textured(HalfSpace.pn(Vec3.xyz( 0, 0,-1), Vec3.xyz( 0, 0, 1)), textureWall),
				Body.textured(HalfSpace.pn(Vec3.xyz( 0, 0, 1), Vec3.xyz( 0, 0,-1)), textureWall)
		));
		
		// Balls;
		
		for (int i = 0; i < nBalls; i++) {
			Vec3 c = Vec3.xyz(
					samplerB.uniform(-1, 1),
					samplerB.uniform(-1, 1),
					samplerB.uniform(-1, 1)
			);
			double r = samplerB.uniform(0.1, 0.2);
			Ball     ball     = Ball.cr(c, r);
			Color    color    = Color.hsb(samplerB.uniform(0, 360), 0.75, 1);
			Material material = new Material(color);
			Body     body     = Body.uniform(ball, material);
			bodies.add(body);
		}
		
		// Lights
		
		lights = new ArrayList<>();
		for (int i = 0; i < nLights; i++) {
			Vec3 p = Vec3.xyz(
					samplerL.uniform(-1, 1),
					samplerL.uniform(-1, 1),
					samplerL.uniform(-1, 1)
			);
			Color color = Color.hsb(samplerL.uniform(0, 360), 0.75, 1);
			Light light = Light.pc(p, color);
			lights.add(light);
		}
	}
	
	
	@Override
	public List<Body> bodies() {
		return bodies;
	}
	
	@Override
	public List<Light> lights() {
		return lights;
	}
	
	@Override
	public Transform cameraTransform() {
		return Transform.translation(Vec3.xyz(0, 0, z)).
				andThen(Transform.rotationAboutY(phi));
	}
}
