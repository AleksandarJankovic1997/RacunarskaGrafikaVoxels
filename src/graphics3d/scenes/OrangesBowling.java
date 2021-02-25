package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Ball;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.drawingx.gadgets.annotations.GadgetInteger;
import mars.functions.interfaces.Function0;
import mars.geometry.Vector;

public class OrangesBowling extends SceneBase {
	
	public static class Factory implements Function0<Scene> {
		@GadgetInteger(min = 0)
		int n = 5;
		
		@GadgetDouble
		double phiCameraX = 0.07;
		
		@GadgetDouble
		double phiCameraY = 0.54;
		
		@Override
		public Scene at() {
			return new OrangesBowling(n, phiCameraX, phiCameraY);
		}
	}
	
	
	public OrangesBowling(int n, double phiCameraX, double phiCameraY) {
		
		// Bodies
		
		double sqrt3 = Math.sqrt(3);
		
		Vec3 dI = Vec3.xyz(1.0, 0.0, 0.0);
		Vec3 dJ = Vec3.xyz(0.5, 0.0, sqrt3/2);
		Vec3 dK = Vec3.xyz(0.5, Math.sqrt(2.0/3.0), 1.0/(2*sqrt3));
		
		Vec3 o = dI.add(dJ.add(dK)).mul((n-1)/4.0);
		
		double q = 0.125;
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; i+j < n; j++) {
				for (int k = 0; i+j+k < n; k++) {
					Vec3 c = dI.mul(i).add(dJ.mul(j)).add(dK.mul(k)).sub(o);
					
					Color color = Color.rgb(i, j, k).mul((1 - q) / (n - 1)).add(Color.gray(q));
					Material material = new Material(
							color,
							color.add(Color.WHITE).div(2),
							64,
							Color.gray(0.25),
							Color.BLACK,
							1
					);
					
					bodies.add(Body.uniform(Ball.cr(c, 0.5), material));
				}
			}
		}
		

		// Lights
		
		for (int a = 0; a < 3; a++) {
			Vector xz = Vector.polar(12, (a+0.25)/3.0);
			lights.add(Light.pc(Vec3.xyz(xz.x(), 10, xz.y()), Color.gray(100)));
		}
		
		
		// Camera
		
		cameraTransform = Transform.IDENTITY
				.andThen(Transform.translation(Vec3.xyz(0, 0, -8)))
				.andThen(Transform.rotationAboutX(phiCameraX))
				.andThen(Transform.rotationAboutY(phiCameraY))
		;
	}
	
}
