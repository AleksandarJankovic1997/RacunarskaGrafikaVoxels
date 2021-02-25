package graphics3d.solids;

import graphics3d.Hit;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;
import mars.geometry.Vector;


public class HalfSpace implements Solid {
	
	private final Vec3 p; // A point on the boundary plane
	private final Vec3 e; // A vector parallel to the boundary plane.
	private final Vec3 f; // A vector parallel to the boundary plane, not parallel to e.
	private final Vec3 n_; // A normalized normal vector to the boundary plane
	
	private final double e_f, f_e, eLSqr, fLSqr, sinSqr;
	
	
	private HalfSpace(Vec3 p, Vec3 e, Vec3 f) {
		this.p = p;
		this.e = e;
		this.f = f;
		this.n_ = e.cross(f).normalized_();
		
		eLSqr = e.lengthSquared();
		fLSqr = f.lengthSquared();
		double ef = e.dot(f);
		e_f = ef / fLSqr;
		f_e = ef / eLSqr;
		sinSqr = 1 - e_f * f_e;
	}
	
	
	public static HalfSpace pef(Vec3 p, Vec3 e, Vec3 f) {
		return new HalfSpace(p, e, f);
	}
	
	
	public static HalfSpace pqr(Vec3 p, Vec3 q, Vec3 r) {
		return pef(p, q.sub(p), r.sub(p));
	}
	
	
	public static HalfSpace pn(Vec3 p, Vec3 n) {
		double nl = Math.sqrt(n.length());
		Vec3 e = Vec3.EY.cross(n);
		if (e.lengthSquared() == 0) {
			e = Vec3.EZ.cross(n);
		}
		e = e.normalizedTo(nl);
		Vec3 f = n.cross(e).normalizedTo(nl);
		return new HalfSpace(p, e, f);
	}
	
	
	public Vec3 p() {
		return p;
	}
	
	
	public Vec3 e() {
		return e;
	}
	
	
	public Vec3 f() {
		return f;
	}
	
	
	public Vec3 n_() {
		return n_;
	}
	
	
	@Override
	public Hit[] hits(Ray ray) {
		double o = n_().dot(ray.d());
		double t = n_().dot(p().sub(ray.p())) / o;
		
		if (o < 0) {
			return new Hit[]{Hit.tn(t, n_), Hit.tn(Double.POSITIVE_INFINITY, n_)}; // The ray enters the half-space
		}
		if (o > 0) {
			return new Hit[]{Hit.tn(Double.NEGATIVE_INFINITY, n_), Hit.tn(t, n_)}; // The ray exits the half-space
		}
		return Solid.NO_HITS;                        // the ray is parallel to the half-space;
	}
	
	
	public Vec3 normalAtPoint_(Vec3 p) {
		return n_;
	}
	
	
	@Override
	public Vector uv(Vec3 q) {
		
		/*
	
		// In case e and f are orthogonal.
	
		Vec3 a = q.sub(p);
		double u = a.dot(e) / e.lengthSquared();
		double v = a.dot(f) / f.lengthSquared();
		 */
		
		
		// In an arbitrary case (e and f not necessarily orthogonal).
	
		Vec3 a = q.sub(p);
		
		double b_e = a.dot(e) / eLSqr;
		double b_f = a.dot(f) / fLSqr;
		
		double u = (b_e - b_f * f_e) / sinSqr;
		double v = (b_f - b_e * e_f) / sinSqr;
		
		
		return Vector.xy(u, v);
	}
	
	
	@Override
	public String toString() {
		return "HalfSpace{" +
				"p=" + p +
				", e=" + e +
				", f=" + f +
				", n=" + n_ +
				'}';
	}
	
}
