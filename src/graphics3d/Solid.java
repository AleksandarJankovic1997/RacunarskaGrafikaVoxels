package graphics3d;

import graphics3d.solids.HalfSpace;
import graphics3d.solids.Nothing;
import graphics3d.solids.Space;
import mars.geometry.Vector;

import java.util.Arrays;

/**
 * A Solid is a 3D object (a set of points in 3D space). It is solely defined by a function returning the intersection
 * between its boundary and a given ray.
 */
public interface Solid {
	
	Hit[] NO_HITS = new Hit[0];
	Hit[] FULL_RANGE = new Hit[] {
			Hit.tn(Double.NEGATIVE_INFINITY, Vec3.ZERO),
			Hit.tn(Double.POSITIVE_INFINITY, Vec3.ZERO)
	};
	
	/**
	 * Times of the intersections between the ray and the surface of the solid, in strictly increasing order,
	 * including negative-time intersections. Even and odd indices correspond to the moments the ray is entering
	 * and exiting the solid, respectively.
	 */
	Hit[] hits(Ray ray);
	
	
	default Vector uv(Vec3 p) {
		throw new UnsupportedOperationException();
	}
	
	
	// ------------------------
	
	
	default Solid transformed(Transform t) {
		return new Solid() {
			private final Transform tInv = t.inverse();
			private final Transform tInvT = tInv.transposeWithoutTranslation();
			
			@Override
			public Hit[] hits(Ray ray) {
				Hit[] hitsO = Solid.this.hits(tInv.applyTo(ray)); // Testing the original object for the intersections with the inversely transformed ray.
				if (hitsO.length == 0) {
					return NO_HITS;
				}
				Hit[] hitsT = new Hit[hitsO.length];
				for (int i = 0; i < hitsO.length; i++) {
					Hit h = hitsO[i];
					hitsT[i] = Hit.tn(h.t(), tInvT.applyTo(h.n_()).normalized_()); // Transforming normals using transposed inverted transform.
				}
				
				return hitsT;
			}
		};
	}
	
	
	default Solid complement() {
		return new Solid() {
			@Override
			public Hit[] hits(Ray ray) {
				// We just add -inf at the beginning and +inf at the end.
				// Repetitions might be introduced this way, so we check and remove them.
				
				Hit[] hitsS = Solid.this.hits(ray);
				int nS = hitsS.length;
				if (nS == 0) {
					return FULL_RANGE;
				}
				
				int sS = hitsS[0     ].t() == Double.NEGATIVE_INFINITY ? 1 : 0; // How many to skip from the start.
				int eS = hitsS[nS - 1].t() == Double.POSITIVE_INFINITY ? 1 : 0; // How many to skip from the end.
				
				int nT = nS + 2 - 2 * (sS + eS);
				if (nT == 0) {
					return NO_HITS;
				}
				
				Hit[] hitsT = new Hit[nT];
				hitsT[0               ] = Hit.NEGATIVE_INFINITY;                // Avoiding ifs. These elements might be overwritten in the for loop.
				hitsT[hitsT.length - 1] = Hit.POSITIVE_INFINITY;
				int n = nS - sS - eS;
				for (int i = 0; i < n; i++) {
					Hit h = hitsS[sS + i];
					hitsT[1 - sS + i] = h.inverse();                            // We need to invert the normal, because the normal should point towards exterior, and in the complement interior and exterior switch sides.
				}
				
				return hitsT;
			}
		};
	}
	
	static Solid union(Solid sA, Solid sB) {
		return new Solid() {
			@Override
			public Hit[] hits(Ray ray) {
				// Ve are computing the union of the intervals defined by the hits with the solids.
				
				Hit[] hitsA = sA.hits(ray);
				int nA = hitsA.length;
				
				Hit[] hitsB = sB.hits(ray);
				int nB = hitsB.length;
				
				if (nA == 0) return hitsB;
				if (nB == 0) return hitsA;
				
				Hit[] hits = new Hit[nA + nB];
				
				int i = 0;
				int iA = 0;
				int iB = 0;
				
				while (iA < nA || iB < nB) {
					double tA = iA < nA ? hitsA[iA].t() : Double.POSITIVE_INFINITY;
					double tB = iB < nB ? hitsB[iB].t() : Double.POSITIVE_INFINITY;
					if (tA <= tB && iA < nA) {
						if ((iB & 1) == 0) {             // if we are outside B then this hit is relevant.
							hits[i++] = hitsA[iA];
						}
						iA++;
					} else {
						if ((iA & 1) == 0) {             // if we are outside A then this hit is relevant.
							hits[i++] = hitsB[iB];
						}
						iB++;
					}
				}
				
				return Arrays.copyOf(hits, i);
			}
		};
	}
	
	
//	static Solid intersection(Solid sA, Solid sB) {
//		return Solid.union(sA.complement(), sB.complement()).complement();
//	}
	
	static Solid intersection(Solid sA, Solid sB) {
		return new Solid() {
			@Override
			public Hit[] hits(Ray ray) {
				Hit[] hitsA = sA.hits(ray);
				int nA = hitsA.length;
				if (nA == 0) return Solid.NO_HITS;
				
				Hit[] hitsB = sB.hits(ray);
				int nB = hitsB.length;
				if (nB == 0) return Solid.NO_HITS;
				
				Hit[] hits = new Hit[nA + nB - 1];
				
				int i = 0;
				int iA = 0;
				int iB = 0;
				
				while (iA < nA || iB < nB) {
					double tA = iA < nA ? hitsA[iA].t() : Double.POSITIVE_INFINITY;
					double tB = iB < nB ? hitsB[iB].t() : Double.POSITIVE_INFINITY;
					if (tA <= tB && iA < nA) {
						if ((iB & 1) == 1) {             // if we are inside B then this hit is relevant.
							hits[i++] = hitsA[iA];
						}
						iA++;
					} else {
						if ((iA & 1) == 1) {             // if we are inside A then this hit is relevant.
							hits[i++] = hitsB[iB];
						}
						iB++;
					}
				}
				
				return Arrays.copyOf(hits, i);
			}
		};
	}
	
	
//	static Solid difference(Solid sA, Solid sB) {
//		return Solid.union(sA.complement(), sB).complement();
//	}
	
	
	static Solid difference(Solid sA, Solid sB) {
		return new Solid() {
			@Override
			public Hit[] hits(Ray ray) {
				Hit[] hitsA = sA.hits(ray);
				int nA = hitsA.length;
				if (nA == 0) return Solid.NO_HITS;
				
				Hit[] hitsB = sB.hits(ray);
				int nB = hitsB.length;
				if (nB == 0) return hitsA;
				
				int n = nA + nB;
				Hit[] hits = new Hit[n];
				
				int i = 0;
				int iA = 0;
				int iB = 0;
				
				while (iA < nA || iB < nB) {
					double tA = iA < nA ? hitsA[iA].t() : Double.POSITIVE_INFINITY;
					double tB = iB < nB ? hitsB[iB].t() : Double.POSITIVE_INFINITY;
					if (tA <= tB && iA < nA) {
						if ((iB & 1) == 0) {             // if we are outside B then this hit is relevant.
							hits[i++] = hitsA[iA];
						}
						iA++;
					} else {
						if ((iA & 1) == 1) {             // if we are inside A then this hit is relevant.
							hits[i++] = hitsB[iB].inverse();
						}
						iB++;
					}
				}
				
				return Arrays.copyOf(hits, i);
			}
		};
	}
	
	
	private static Solid union(Solid[] solids, int i, int n) {
		if (n == 1) return solids[i];
		if (n == 2) return Solid.union(solids[i], solids[i+1]);
		int m = n/2;
		return union(union(solids, i, m), union(solids, i+m, n-m));
	}
	
	static Solid union(Solid... solids) {
		if (solids.length == 0) return Nothing.INSTANCE;
		return union(solids, 0, solids.length);
	}
	
	
	private static Solid intersection(Solid[] solids, int i, int n) {
		if (n == 1) return solids[i];
		if (n == 2) return Solid.intersection(solids[i], solids[i+1]);
		int m = n/2;
		return intersection(intersection(solids, i, m), intersection(solids, i+m, n-m));
	}
	
	static Solid intersection(Solid... solids) {
		if (solids.length == 0) return Space.INSTANCE;
		return intersection(solids, 0, solids.length);
	}
	
	
	// Factory methods for some specific solids.
	
	//** The cube with the sides parallel to coordinate planes and distance s from origin to the sides. */
	static Solid cube(double s) {
		// A cube is just an intersection of 6 half-spaces.
		return
				Solid.intersection(
						Solid.intersection(
								Solid.intersection(
										HalfSpace.pn(Vec3.xyz(-s, 0, 0), Vec3.xyz(-s, 0, 0)),
										HalfSpace.pn(Vec3.xyz( s, 0, 0), Vec3.xyz( s, 0, 0))
								),
								Solid.intersection(
										HalfSpace.pn(Vec3.xyz( 0,-s, 0), Vec3.xyz( 0,-s, 0)),
										HalfSpace.pn(Vec3.xyz( 0, s, 0), Vec3.xyz( 0, s, 0))
								)
						),
						Solid.intersection(
								HalfSpace.pn(Vec3.xyz( 0, 0,-s), Vec3.xyz( 0, 0,-s)),
								HalfSpace.pn(Vec3.xyz( 0, 0, s), Vec3.xyz( 0, 0, s))
						)
				);
	}
	
	
}
