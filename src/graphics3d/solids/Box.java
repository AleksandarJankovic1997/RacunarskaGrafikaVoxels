package graphics3d.solids;

import graphics3d.Hit;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;
import mars.utils.Numeric;


public class Box implements Solid {

	private final Vec3 p, q;
	
	
	private Box(Vec3 p, Vec3 q) {
		this.p = p;
		this.q = q;
	}
	
	public static Box pq(Vec3 p, Vec3 q) {
		return new Box(p, q);
	}
	
	public static Box pd(Vec3 p, Vec3 d) {
		return pq(p, p.add(d));
	}
	
	public static Box cr(Vec3 c, Vec3 r) {
		return pd(c.sub(r), r.mul(2));
	}
	
	public static Box cr(Vec3 c, double r) {
		return cr(c, Vec3.EXYZ.mul(r));
	}
	
	public static Box r(Vec3 r) {
		return cr(Vec3.ZERO, r);
	}
	
	public static Box r(double r) {
		return cr(Vec3.ZERO, r);
	}
	
	
	public Vec3 p() {
		return p;
	}
	
	public Vec3 q() {
		return q;
	}
	
	
	@Override
	public Hit[] hits(Ray ray) {
		Vec3 tP = p().sub(ray.p()).div(ray.d());
		Vec3 tQ = q().sub(ray.p()).div(ray.d());
		
		Vec3 t0 = Vec3.min(tP, tQ);
		Vec3 t1 = Vec3.max(tP, tQ);
		
		int iMax0 = t0.maxIndex();
		int iMin1 = t1.minIndex();
		
		double max0 = t0.get(iMax0);
		double min1 = t1.get(iMin1);
		
		if (max0 < min1) {
			return new Hit[] {
					Hit.tn(max0, Vec3.E[iMax0].mul(-Numeric.sign(ray.d().get(iMax0)))),
					Hit.tn(min1, Vec3.E[iMin1].mul( Numeric.sign(ray.d().get(iMin1))))
			};
		} else {
			return Solid.NO_HITS;
		}
	}
}
