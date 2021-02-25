package graphics3d.renderers;

import graphics3d.Color;
import graphics3d.Renderer;
import graphics3d.Scene;
import mars.functions.interfaces.Function2;
import mars.geometry.Vector;

import java.util.stream.IntStream;

public class TestGammaPattern implements Renderer {
	
	public static class Factory implements Function2<Renderer, Scene, Vector> {
		@Override
		public Renderer at(Scene scene, Vector imageSize) {
			return new TestGammaPattern(scene, imageSize);
		}
	}
	
	
	private final Scene scene;
	private final Vector imageSize;
	
	
	
	public TestGammaPattern(Scene scene, Vector imageSize) {
		this.scene = scene;
		this.imageSize = imageSize;
	}
	
	
	@Override
	public Vector imageSize() {
		return imageSize;
	}


	@Override
	public void renderIteration(Color[][] pixelColors) {
		IntStream.range(0, imageSize().yInt()).parallel().forEach(y -> {
			for (int x = 0; x < imageSize().xInt(); x++) {
				double k = x / imageSize.x();
				if (k < 0.5) {
					pixelColors[y][x] = Color.gray(0.5);
				} else {
					if ((x + y) % 2 == 0) {
						pixelColors[y][x] = Color.WHITE;
					} else {
						pixelColors[y][x] = Color.BLACK;
					}
				}
			}
		});
	}
	
}
