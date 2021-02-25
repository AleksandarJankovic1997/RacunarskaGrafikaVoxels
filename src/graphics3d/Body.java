package graphics3d;

/**
 * A combination of a Solid and function that returns the material at each point on the boundary of that solid.
 */
public interface Body {
	Solid solid();
	
	Material materialAt(Vec3 p);
	
	
	/** Returns a body with the given material at every point of the solid. */
	static Body uniform(Solid solid, Material material) {
		return new Body() {
			@Override
			public Solid solid() {
				return solid;
			}
			@Override
			public Material materialAt(Vec3 p) {
				return material;
			}
		};
	}
	
	
	static Body textured(Solid solid, Texture texture) {
		return new Body() {
			@Override
			public Solid solid() {
				return solid;
			}
			
			@Override
			public Material materialAt(Vec3 p) {
				return texture.materialAt(solid.uv(p));
			}
		};
	}
	
}
