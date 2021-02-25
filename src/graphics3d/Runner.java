package graphics3d;

import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import mars.drawingx.application.DrawingApplication;
import mars.drawingx.application.Options;
import mars.drawingx.drawing.Drawing;
import mars.drawingx.drawing.DrawingUtils;
import mars.drawingx.drawing.View;
import mars.drawingx.gadgets.annotations.DoNotDetectChanges;
import mars.drawingx.gadgets.annotations.GadgetBoolean;
import mars.drawingx.gadgets.annotations.RecurseGadgets;
import mars.functions.interfaces.Function0;
import mars.functions.interfaces.Function2;
import mars.geometry.Vector;
import mars.input.InputEvent;
import mars.input.InputState;
import mars.time.Timer;

import java.nio.IntBuffer;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;



public class Runner implements Drawing {

	static final Vector imageSize = Vector.xy(640, 640);
	static final PixelFormat<IntBuffer> pixelFormat = PixelFormat.getIntArgbPreInstance();
	static final int PARALLELISM = Utils.streaming() ? 2 : ForkJoinPool.getCommonPoolParallelism();  // Reduce parallelism for streaming.
	
	
	@GadgetBoolean
	@DoNotDetectChanges
	boolean showStats = false;
	
	
	@RecurseGadgets
	Function0<ToneMapper> fToneMapper = new graphics3d.tonemappers.Clamp.Factory();
	
	@RecurseGadgets
//	Function2<Renderer, Scene, Vector> fRenderer = new graphics3d.renderers.TestGammaPattern.Factory();
//	Function2<Renderer, Scene, Vector> fRenderer = new graphics3d.renderers.TestGammaGradient.Factory();
//	Function2<Renderer, Scene, Vector> fRenderer = new graphics3d.renderers.TestBuffers.Factory();
//	Function2<Renderer, Scene, Vector> fRenderer = new graphics3d.renderers.Sonar.Factory();
//	Function2<Renderer, Scene, Vector> fRenderer = new graphics3d.renderers.IAmLight.Factory();
//	Function2<Renderer, Scene, Vector> fRenderer = new graphics3d.renderers.RayCaster.Factory();
//	Function2<Renderer, Scene, Vector> fRenderer = new graphics3d.renderers.RayTracer.Factory();
	Function2<Renderer, Scene, Vector> fRenderer = new graphics3d.renderers.PathTracer.Factory();
	
	@RecurseGadgets
//	Function0<Scene> fScene = new graphics3d.scenes.Test1.Factory();
//	Function0<Scene> fScene = new graphics3d.scenes.Room.Factory();
//	Function0<Scene> fScene = new graphics3d.scenes.Campfire.Factory();
//	Function0<Scene> fScene = new graphics3d.scenes.CampfireGlass.Factory();
//	Function0<Scene> fScene = new graphics3d.scenes.Mirrors.Factory();
//	Function0<Scene> fScene = new graphics3d.scenes.OrangesGlass.Factory();
//	Function0<Scene> fScene = new graphics3d.scenes.OrangesBowling.Factory();
//	Function0<Scene> fScene = new graphics3d.scenes.TestTransform.Factory();
//	Function0<Scene> fScene = new graphics3d.scenes.TestCSG.Factory();
	Function0<Scene> fScene = new graphics3d.scenes.TestGI.Factory();
//	Function0<Scene> fScene = new graphics3d.scenes.Boxes.Factory();

	
	
	Renderer renderer;
	int iterationsRendering;
	int iterationsToneMapping;
	
	Timer timer;
	double timeLastRender;
	boolean shouldResetRenderer = false;
	

	// TripleBuffering

	TripleBuffering<BufferRendering  > buffersRendering   = new TripleBuffering<>(() -> new BufferRendering  (imageSize));
	TripleBuffering<BufferToneMapping> buffersToneMapping = new TripleBuffering<>(() -> new BufferToneMapping(imageSize));
	
	
	
	
	void resetRenderer() {
		renderer = fRenderer.at(fScene.at(), imageSize);
		iterationsRendering = 0;
		iterationsToneMapping = 0;
		timer = new Timer();
		shouldResetRenderer = false;
	}
	
	
	private final Object toneMappingMonitor = new Object();
	
	@Override
	public void init() {
		resetRenderer();
		
		Runnable rendering = () -> {
			try {
				//noinspection InfiniteLoopStatement
				while (true) {
					if (shouldResetRenderer) {
						resetRenderer();
					}
					
					buffersRendering.write(bufferRendering -> {
						renderer.renderIteration(bufferRendering.pixelColors());
					});
					
					timeLastRender = timer.getTime();
					iterationsRendering++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		
		Runnable toneMapping = () -> {
			try {
				//noinspection InfiniteLoopStatement
				while (true) {
					try {
						synchronized (toneMappingMonitor) {
							toneMappingMonitor.wait();
						}
						
						buffersRendering.read(bufferRendering -> {
							buffersToneMapping.write(bufferToneMapping -> {
								fToneMapper.at().toneMap(bufferRendering.pixelColors(), bufferToneMapping.imageData());
							});
						});
						
						iterationsToneMapping++;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		
		ForkJoinPool pool = new ForkJoinPool(PARALLELISM);
		pool.submit(rendering);
		pool.submit(toneMapping);
	}
	
	
	@Override
	public void draw(View view) {
		double time = timer.getTime();
		
		DrawingUtils.clear(view, javafx.scene.paint.Color.BLACK);
		
		buffersToneMapping.read(bufferToneMapping -> {
			WritableImage img = new WritableImage(imageSize.xInt(), imageSize.yInt());
			img.getPixelWriter().setPixels(0, 0, imageSize.xInt(), imageSize.yInt(), pixelFormat, bufferToneMapping.imageData(), 0, imageSize.xInt());
			view.drawImageCentered(Vector.ZERO, img);
		});

		synchronized (toneMappingMonitor) {
			toneMappingMonitor.notifyAll();
		}
		
		if (showStats) {
			DrawingUtils.drawInfoText(view,
					String.format("Iterations rendering:     %5d", iterationsRendering),
					String.format("Iterations tone mapping : %5d", iterationsToneMapping),
					String.format("Time elapsed: %.3f", time),
					String.format("Time per iteration rendering: %.3f", timeLastRender / iterationsRendering)
			);
		}
	}
	
	
	@Override
	public void receiveEvent(View view, InputEvent event, InputState state, Vector pointerWorld, Vector pointerViewBase) {
		if (event.isKeyPress(KeyCode.ENTER)) {
			shouldResetRenderer = true;
		}
	}
	
	
	@Override
	public void valuesChanged() {
		shouldResetRenderer = true;
	}
	
	
	public static void main(String[] args) {
		Options options = new Options();
		options.drawingSize = Runner.imageSize;
		DrawingApplication.launch(options);
	}
	
}


class BufferRendering {
	private final Color[][] pixelColors;
	
	
	public BufferRendering(Vector size) {
		pixelColors = new Color[size.yInt()][size.xInt()];
		
		IntStream.range(0, size.yInt()).parallel().forEach(y -> {
			for (int x = 0; x < size.xInt(); x++) {
				pixelColors[y][x] = Color.BLACK;
			}
		});
	}
	
	public Color[][] pixelColors() {
		return pixelColors;
	}
}


class BufferToneMapping {
	private final int[] imageData;
	
	public BufferToneMapping(Vector size) {
		imageData = new int[(int) size.area()];
	}
	
	public int[] imageData() {
		return imageData;
	}
}
