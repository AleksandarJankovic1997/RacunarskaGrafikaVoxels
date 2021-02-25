package graphics3d.renderers;

import graphics3d.*;
import graphics3d.cameras.ThinLens;
import graphics3d.colliders.BruteForce;
import mars.geometry.Vector;
import mars.random.sampling.Sampler;

import java.util.stream.IntStream;

public abstract class RendererBase implements Renderer {

	protected final Scene scene;
	protected final Collider collider;
	protected final Camera camera;
	
	private final Vector imageSize;
	private final Color[][] pixelColorsTotal;
	private final Sampler[] samplers;
	private int renderIterations = 0;
	
	
	
	public RendererBase(Scene scene, Vector imageSize, double rLens, double focalLength) {
		this.scene = scene;
		this.imageSize = imageSize;
		
		samplers = new Sampler[imageSize().yInt()];
		pixelColorsTotal = new Color[imageSize.yInt()][imageSize().xInt()];
		
		collider = new BruteForce(scene.bodies());
	    camera = new ThinLens(rLens, focalLength);
		
		for (int y = 0; y < imageSize().yInt(); y++) {
			samplers[y] = new Sampler();
		}
		
		IntStream.range(0, imageSize().yInt()).parallel().forEach(y -> {
			for (int x = 0; x < imageSize().xInt(); x++) {
				pixelColorsTotal[y][x] = Color.BLACK;
			}
		});
	}
	
	
	public RendererBase(Scene scene, Vector imageSize) {
		this(scene, imageSize, 0.0, 1.0);
	}
	
	
	@Override
	public Vector imageSize() {
		return imageSize;
	}
	
	
	abstract Color sampleRay(Ray ray);
	
	
	@Override
	public void renderIteration(Color[][] pixelColors) {
		double m = imageSize().min();
		renderIterations++;
		
		IntStream.range(0, imageSize().yInt()).parallel().forEach(y -> {
			Sampler sampler = samplers[y];
			
			for (int x = 0; x < imageSize().xInt(); x++) {
				Vector pixel = Vector.xy(x, y).add(sampler.randomInBox());
				Vector pixelS = pixel.sub(imageSize().div(2)).div(m);
				
				Ray cameraRay = camera.sampleExitingRay(pixelS);
				Ray cameraRayTransformed = scene.cameraTransform().applyTo(cameraRay);
				Color sample = sampleRay(cameraRayTransformed);
				pixelColorsTotal[y][x] = pixelColorsTotal[y][x].add(sample);
				pixelColors[y][x] = pixelColorsTotal[y][x].div(renderIterations);
			}
		});
	}
}
