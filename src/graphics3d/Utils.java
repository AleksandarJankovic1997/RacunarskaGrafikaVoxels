package graphics3d;

import mars.random.sampling.Sampler;
import mars.utils.Numeric;

public class Utils {
	
	/** Is the environment variable "ID" set to "mars". */
	public static boolean idMars() {
		return "mars".equals(System.getenv("ID"));
	}
	
	
	/** Is OBS running. */
	public static boolean streaming() {
		return ProcessHandle.allProcesses().anyMatch(ph -> ph.info().command().orElse("").contains("obs64"));
	}
	
	
	public static Vec3 reflectN(Vec3 a, Vec3 n_) {
		return n_.mul(2 * a.dot(n_)).sub(a);
	}

	public static Vec3 reflect(Vec3 a, Vec3 n) {
		return n.mul(2 * a.dot(n) / n.lengthSquared()).sub(a);
	}
	
	
	/** Some normal to v */
	public static Vec3 normal(Vec3 v) {
		Vec3 p = v.cross(Vec3.EX);
		if (p.isZero()) {
			p = v.cross(Vec3.EY);
		}
		return p;
	}
	
	
	/** The length of the result equals the length of n. */
	public static Vec3 sampleHemisphereUniform(Sampler sampler, Vec3 n) {
		double nL = n.length();
		
		Vec3 p = normal(n) .normalizedTo(nL);
		Vec3 q = n.cross(p).normalizedTo(nL);
		
		double phi = sampler.uniform();
		double z   = sampler.uniform();
		
		double r = Math.sqrt(1 - z*z);
		double x = Numeric.cosT(phi) * r;
		double y = Numeric.sinT(phi) * r;
		
		return      (n.mul(z))
				.add(p.mul(x))
				.add(q.mul(y));
	}
	
	
	/** The length of the result equals the length of n. */
	public static Vec3 sampleHemisphereCosineDistributed(Sampler sampler, Vec3 n) {
		double nL = n.length();
		
		Vec3 p = normal(n) .normalizedTo(nL);
		Vec3 q = n.cross(p).normalizedTo(nL);
		
		double phi  = sampler.uniform();
		double zSqr = sampler.uniform();
		double z = Math.sqrt(zSqr);
		
		double r = Math.sqrt(1 - zSqr);
		double x = Numeric.cosT(phi) * r;
		double y = Numeric.sinT(phi) * r;
		
		return      (n.mul(z))
				.add(p.mul(x))
				.add(q.mul(y));
	}
	
}
