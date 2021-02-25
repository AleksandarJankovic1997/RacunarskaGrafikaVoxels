package graphics3d.renderers;

import graphics3d.Color;
import graphics3d.Renderer;
import graphics3d.Scene;
import mars.functions.interfaces.Function2;
import mars.geometry.Vector;
import mars.utils.Numeric;

import java.util.stream.IntStream;

public class TestGammaGradient implements Renderer {
	
	public static class Factory implements Function2<Renderer, Scene, Vector> {
		@Override
		public Renderer at(Scene scene, Vector imageSize) {
			return new TestGammaGradient(scene, imageSize);
		}
	}
	
	
	private final Scene scene;
	private final Vector imageSize;
	
	
	
	public TestGammaGradient(Scene scene, Vector imageSize) {
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
				k = Numeric.clamp((k - 0.5) * 2 + 0.5);
				pixelColors[y][x] = Color.rgb(k, 1-k, 0);
			}
		});
	}
	
}
