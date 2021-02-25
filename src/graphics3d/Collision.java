package graphics3d;


/**
 * A record containing data about ray-body collision.
 */
public class Collision {
	
	private final Hit hit;
	private final Body body;
	
	
	public Collision(Hit hit, Body body) {
		this.hit = hit;
		this.body = body;
	}
	
	
	/** The time of the collision. */
	public Hit hit() {
		return hit;
	}
	
	
	/** The body involved in the collision. */
	public Body body() {
		return body;
	}
	
}
