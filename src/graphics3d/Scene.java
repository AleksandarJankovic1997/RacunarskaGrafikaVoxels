package graphics3d;

import java.util.List;

public interface Scene {
	List<Body> bodies();
	List<Light> lights();
	
	default Color colorBackground() {
		return Color.BLACK;
	}
	
	
	default Transform cameraTransform() {
		return Transform.IDENTITY;
	}
	
}
