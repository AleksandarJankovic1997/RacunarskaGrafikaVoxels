package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Ball;
import graphics3d.solids.HalfSpace;
import mars.functions.interfaces.Function0;

import java.util.List;

public class TestCSG extends SceneBase {
	
	public static class Factory implements Function0<Scene> {
		
		@Override
		public Scene at() {
			return new TestCSG();
		}
	}
	
	
	public TestCSG() {
		
		// Bodies
		
		Solid solidA = Solid.cube(0.5).transformed(Transform.rotationAboutX(0.1).andThen(Transform.rotationAboutY(0.1)));
		Solid solidB = Ball.cr(Vec3.xyz(0, 0, 0), 0.62);
		Solid solidC = Ball.cr(Vec3.xyz(0, 0, 0), 0.68);
		Solid solid = Solid.intersection(Solid.difference(solidA, solidB), solidC);

//		Material material = new Material(Color.hsb(60, 0.7, 1), Color.WHITE, 32, Color.BLACK, Color.BLACK, 1);
		Material material = Material.GLASS;
		bodies.add(Body.uniform(solid, material));



		
		bodies.addAll(List.of(
				Body.uniform(HalfSpace.pn(Vec3.xyz(-1, 0, 0), Vec3.xyz( 1, 0, 0)), new Material(Color.hsb(  0, 0.7, 0.8))),
				Body.uniform(HalfSpace.pn(Vec3.xyz( 1, 0, 0), Vec3.xyz(-1, 0, 0)), new Material(Color.hsb(240, 0.7, 0.8))),
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0,-1, 0), Vec3.xyz( 0, 1, 0)), new Material(Color.hsb(  0, 0  , 0.8))),
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0, 1, 0), Vec3.xyz( 0,-1, 0)), new Material(Color.hsb(  0, 0  , 0.8))),
//		        Body.uniform(HalfSpace.pn(Vec3.xyz( 0, 0,-1), Vec3.xyz( 0, 0, 1)), new Material(Color.hsb(  0, 0  , 0.8))),
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0, 0, 1), Vec3.xyz( 0, 0,-1)), new Material(Color.hsb(120, 0.7, 0.8)))
		));
		
		
		// Lights
		
		double s = 0.6;
		double h = 0.6;
		
		lights.addAll(List.of(
				Light.pc(Vec3.xyz(-s, h, -s), Color.hsb(0, 0, 1)),
				Light.pc(Vec3.xyz(-s, h,  s), Color.hsb(0, 0, 1)),
				Light.pc(Vec3.xyz( s, h, -s), Color.hsb(0, 0, 1)),
				Light.pc(Vec3.xyz( s, h,  s), Color.hsb(0, 0, 1))
		));
		
		
		// Camera
		
		cameraTransform = Transform.IDENTITY
				.andThen(Transform.translation(Vec3.xyz(0, 0, -3)))
		;
	}
	
}
