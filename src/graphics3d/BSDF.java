package graphics3d;

import mars.random.sampling.Sampler;


public interface BSDF {
	BSDFResult sample(Sampler sampler, Vec3 n, Vec3 i);
	
	
	// Utility instances and factories.
	
	
	/** Interpolates between two bsdfs */
	static BSDF mix(BSDF bsdf0, BSDF bsdf1, double k) {
		if (k == 0) return bsdf0;
		if (k == 1) return bsdf1;
		return (sampler, n, i) ->
				sampler.uniform() < k ?
						bsdf0.sample(sampler, n, i) :
						bsdf1.sample(sampler, n, i);
	}
	
	
	/** Returns the sum of the specified bsdfs. The sampling is weighted according to the given weights (but the
	 * analytical bsdfs is not dependent on the weights) */
	static BSDF add(BSDF[] bsdfs, double[] weights) {
		double sum = 0;
		int onlyNonZero = -1;
		for (int j = 0; j < weights.length; j++) {
			double w = weights[j];
			sum += w;
			if (w > 0) {
				onlyNonZero = onlyNonZero == -1 ? j : -2;
			}
		}
		if (sum == 0) return BSDF.ABSORPTIVE;
		if (onlyNonZero >= 0) return bsdfs[onlyNonZero];
		
		double[] ks = new double[weights.length];
		for (int j = 0; j < weights.length; j++) {
			ks[j] = weights[j] / sum;
		}
		
		return (sampler, n, i) -> {
			double u = sampler.uniform();
			double s = 0;
			int j = 0;
			while (s <= u) {
				s += ks[j++];
			}
			j--;
			
			BSDFResult bsdfResult = bsdfs[j].sample(sampler, n, i);
			return new BSDFResult(
				bsdfResult.out(),
				bsdfResult.color().div(ks[j])
			);
		};
	}
	
	
	
	
	BSDF ABSORPTIVE = (sampler, n, i) -> BSDFResult.ABSORBED;
	
	BSDF REFLECTIVE = (sampler, n, i) -> new BSDFResult(Utils.reflect(i, n), Color.WHITE);
	
	
	static BSDF diffuse(Color c) {
		return (sampler, n, i) -> new BSDFResult(Utils.sampleHemisphereCosineDistributed(sampler, n), c);
	}
	
	
	static BSDF reflective(double k) {
		return reflective(Color.gray(k));
	}
	

	static BSDF reflective(Color c) {
		return (sampler, n, i) -> new BSDFResult(Utils.reflect(i, n), c);
	}
	
	
	static BSDF refractive(double refractiveIndex) {
		return refractive(1.0, refractiveIndex);
	}
	
	
	static BSDF refractive(double k, double refractiveIndex) {
		return refractive(Color.gray(k), refractiveIndex);
	}
	
	
	static BSDF refractive(Color c, double refractiveIndex) {
		return (sampler, n, i) -> {
			double ri = refractiveIndex;
			double k = 1;
			Vec3 n_ = n.normalized_();
			double lI = i.length();
			
			double c1 = i.dot(n_) / lI;
			if (c1 < 0) { 		                              // We are exiting the object
				ri = 1.0 / ri;
				k = -1;
			}
			double c2Sqr = 1 - (1 - c1 * c1) / (ri * ri);
			
			Vec3 f;
			if (c2Sqr > 0) {
				double c2 = Math.sqrt(c2Sqr);
				f = n_.mul(c1/ri - k * c2).sub(i.div(ri*lI)); // refraction
			} else {
				f = Utils.reflectN(i, n_);                    // total reflection
			}
			
			return new BSDFResult(f, c);
		};
	}
}
