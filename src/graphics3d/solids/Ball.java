package graphics3d.solids;

import graphics3d.Hit;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;

public class Ball implements Solid {
	
	private final Vec3 c;
	private final double r;
	
	private final double rSqr;

	
	
	private Ball(Vec3 c, double r) {
		this.c = c;
		this.r = r;
		rSqr = r * r;
	}
	
	
	public static Ball cr(Vec3 c, double r) {
		return new Ball(c, r);
	}
	
	
	public Vec3 c() {
		return c;
	}
	
	
	public double r() {
		return r;
	}
	
	
	
	@Override
	public Hit[] hits(Ray ray) {
		double dSqr = ray.d().lengthSquared();
		Vec3 e = c().sub(ray.p());                                // Vector from the ray origin to the ball center
		double l = e.dot(ray.d()) / dSqr;
		double mSqr = l * l - (e.lengthSquared() - rSqr) / dSqr;
		
		if (mSqr <= 0) {
			return Solid.NO_HITS;
		} else {
			double m = Math.sqrt(mSqr);

//			normalAtPoint_(ray.at(l-m))                     // p -> ray.at(l-m)
//			ray.at(l-m).sub(c()).div(r)                     // ray.at(t) = ray.p().add(ray.d().mul(t))    t -> l - m
//			ray.p().add(ray.d().mul(l-m)).sub(c()).div(r)
//		    ray.d().mul(l-m).sub(c()).add(ray.p()).div(r)   // .sub(c()).add(ray.p())  -> .sub(e)
//			ray.d().mul(l-m).sub(e).div(r)
			
			return new Hit[]{
					Hit.tn(l - m, ray.d().mul(l - m).sub(e).div(r)),
					Hit.tn(l + m, ray.d().mul(l + m).sub(e).div(r))
			};
		}
	}
	
	
	public Vec3 normalAtPoint_(Vec3 p) {
		return p.sub(c()).div(r);
	}
}
