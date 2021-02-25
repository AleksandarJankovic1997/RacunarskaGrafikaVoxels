package graphics3d.textures;

import graphics3d.Material;
import graphics3d.Texture;
import mars.geometry.Vector;

public class Checkerboard implements Texture {
	
	private final Vector size;
	private final Material material0, material1;
	
	
	public Checkerboard(Vector size, Material material0, Material material1) {
		this.size = size;
		this.material0 = material0;
		this.material1 = material1;
	}


	//
	@Override
	public Material materialAt(Vector uv) {
		Vector i = uv.div(size).floor();
		if ((i.xInt() & 1) ==(i.yInt() & 1) ) {
			return material0;
		} else {
			return material1;
		}
	}
}
