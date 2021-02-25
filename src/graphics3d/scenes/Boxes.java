package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Box;
import graphics3d.solids.HalfSpace;
import mars.drawingx.gadgets.annotations.GadgetInteger;
import mars.drawingx.gadgets.annotations.GadgetLongRandomStream;
import mars.functions.interfaces.Function0;
import mars.geometry.Vector;
import mars.random.sampling.Sampler;

import java.util.ArrayList;
import java.util.List;


public class Boxes extends SceneBase {
	
	public static class Factory implements Function0<Scene> {
		@GadgetInteger
		int n = 60;
		
		@GadgetLongRandomStream
		long seed = 0;
		
		
		@Override
		public Scene at() {
			return new Boxes(n, seed);
		}
	}
	
	
	public Boxes(int n, long seed) {
		Sampler sampler = new Sampler(seed);
		
		List<mars.geometry.Box> boxes = new ArrayList<>();
		
		int nTrials = 0;
		
		while (boxes.size() < n && nTrials < 10000) {
			nTrials++;
			Vector c = sampler.randomInBox(mars.geometry.Box.cr(4.5));
			Vector r = Vector.xy(sampler.uniform(0.05, 0.5), sampler.uniform(0.05, 0.5));
			mars.geometry.Box bCandidate = mars.geometry.Box.cr(c, r);
			
			if (boxes.stream().noneMatch(b -> b.intersects(bCandidate))) {
				boxes.add(bCandidate.extend(0.05));
			
				double h = sampler.exponential(1);
				Solid solid = Box.pq(Vec3.py(bCandidate.p(), 0), Vec3.py(bCandidate.q(), h));
				
				Material material = Material.diffuse(Color.hsb(sampler.uniform(360), 0.6, 0.8));
				Body body = Body.uniform(solid, material);
				bodies.add(body);
				nTrials = 0;
			}
		}
		System.out.println("Number of boxes: " + boxes.size());
		
		{
			Solid solid = HalfSpace.pn(Vec3.ZERO, Vec3.EY);
			Body body = Body.uniform(solid, Material.diffuse(Color.gray(0.8)));
			bodies.add(body);
		}
		
		{
			double d = 20;
			lights.add(Light.pc(Vec3.xyz(-1, 2, -2).mul(d), Color.gray(1.0)));
			lights.add(Light.pc(Vec3.xyz( 2, 2,  1).mul(d), Color.gray(0.5)));
		}
		
		colorBackground = Color.WHITE;
		
		cameraTransform = Transform.IDENTITY
				.andThen(Transform.translation(Vec3.xyz(0, 0, -6)))
				.andThen(Transform.rotationAboutX(0.2))
				.andThen(Transform.rotationAboutY(0.1))
		;
	}
}
