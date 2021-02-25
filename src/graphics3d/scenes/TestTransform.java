package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Ball;
import graphics3d.solids.HalfSpace;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.functions.interfaces.Function0;

import java.util.List;

public class TestTransform extends SceneBase {
	
	public static class Factory implements Function0<Scene> {
		@GadgetDouble
		double phiZ = 0;

		@GadgetDouble
		double phiY = 0;
		
		@Override
		public Scene at() {
			return new TestTransform(phiZ, phiY);
		}
	}
	
	
	public TestTransform(double phiZ, double phiY) {
		
		// Bodies
		
		Solid solid = Ball.cr(Vec3.ZERO, 1).transformed(
				Transform.scaling(Vec3.xyz(0.2, 0.6, 0.6))
						.andThen(Transform.rotationAboutZ(phiZ))
						.andThen(Transform.rotationAboutY(phiY))
		);

		Material material = new Material(Color.hsb(60, 0.7, 1), Color.WHITE, 32, Color.BLACK, Color.BLACK, 1);
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
