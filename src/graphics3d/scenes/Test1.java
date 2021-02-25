package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Ball;
import graphics3d.solids.HalfSpace;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.functions.interfaces.Function0;

import java.util.List;

public class Test1 implements Scene {
	
	public static class Factory implements Function0<Scene> {

		@GadgetDouble(p = -6, q = 6)
		double y = -0.5;
		
		@GadgetDouble(p = -10, q = 10)
		double z = -2;
		
		@Override
		public Scene at() {
			return new Test1(y, z);
		}
	}
	
	
	private final List<Body> bodies;
	private final List<Light> lights;
	
	
	public Test1(double y, double z) {
		bodies = List.of(
			Body.uniform(Ball.cr(Vec3.xyz( 0  ,  0,  4.0),  1)   , new Material(Color.hsb(  0, 0.8, 1.0))),
			Body.uniform(Ball.cr(Vec3.xyz( 1.5,  1,  4.5),  2)   , new Material(Color.hsb(120, 0.7, 1.0))),
			
			Body.uniform(HalfSpace.pn(Vec3.xyz(0, y, 0), Vec3.EY), new Material(Color.hsb(240, 0.6, 1.0)))
		);
		
		lights = List.of(
			Light.pc(Vec3.xyz(-1, 1, z), Color.gray(1.0))
		);

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
