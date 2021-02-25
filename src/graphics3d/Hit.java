package graphics3d;


public class Hit {
	public static final Hit NEGATIVE_INFINITY = Hit.tn(Double.NEGATIVE_INFINITY, Vec3.ZERO);
	public static final Hit POSITIVE_INFINITY = Hit.tn(Double.POSITIVE_INFINITY, Vec3.ZERO);
	
	
	private final double t;
	private final Vec3 n_;
	
	
	
	private Hit(double t, Vec3 n_) {
		this.t = t;
		this.n_ = n_;
	}
	
	
	public static Hit tn(double t, Vec3 n_) {
		return new Hit(t, n_);
	}
	
	
	/** The time of the hit */
	public double t() {
		return t;
	}
	
	
	/** The normalized normal at the point of the hit */
	public Vec3 n_() {
		return n_;
	}
	
	
	/** The Hit with the same time but reversed normal. */
	public Hit inverse() {
		return tn(t(), n_().inverse());
	}
}
