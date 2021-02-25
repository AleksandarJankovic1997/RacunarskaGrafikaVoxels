package graphics3d.renderers;

import graphics3d.Color;
import graphics3d.Renderer;
import graphics3d.Scene;
import mars.functions.interfaces.Function2;
import mars.geometry.Vector;

import java.util.stream.IntStream;

public class TestBuffers implements Renderer {
	
	public static class Factory implements Function2<Renderer, Scene, Vector> {
		@Override
		public Renderer at(Scene scene, Vector imageSize) {
			return new TestBuffers(scene, imageSize);
		}
	}
	
	
	private final Scene scene;
	private final Vector imageSize;
	
	
	
	public TestBuffers(Scene scene, Vector imageSize) {
		this.scene = scene;
		this.imageSize = imageSize;
	}
	
	
	@Override
	public Vector imageSize() {
		return imageSize;
	}


	int iteration = 0;
	
	@Override
	public void renderIteration(Color[][] pixelColors) {
		Color c = Color.gray(iteration % 2);
		
		IntStream.range(0, imageSize().yInt()).parallel().forEach(y -> {
			for (int x = 0; x < imageSize().xInt(); x++) {
				pixelColors[y][x] = c;
			}
		});
		
		iteration++;
	}
	
}
