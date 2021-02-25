package graphics3d;

/**
 * A collider is an algorithm and data structure used to (efficiently) compute the intersection between any given ray
 * and a set of bodies (given through the constructor parameters).
 */
public interface Collider {

	Collision collide(Ray r);
	
	/** Is there an intersection with the given ray for which the ray-time is inside the (0,1) interval.
	 * Collider implementations might override this method for efficiency purposes. */
	default boolean collidesIn01(Ray r) {
		return collide(r).hit().t() < 1;
	}
}
