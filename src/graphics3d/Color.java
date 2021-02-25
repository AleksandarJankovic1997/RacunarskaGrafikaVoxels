package graphics3d;

import mars.utils.Numeric;

import java.util.function.DoubleUnaryOperator;

/**
 * Colors in linear sRGB color space.
 */
public class Color {
	public static final Color BLACK = rgb(0, 0, 0);
	public static final Color WHITE = rgb(1, 1, 1);
	
	
	final double r, g, b;
 
 
 
	private Color(double r, double g, double b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	
	public static Color rgb(double r, double g, double b) {
		return new Color(r, g, b);
	}
	
	
	public static Color vec(Vec3 v) {
		return rgb(v.x(), v.y(), v.z());
	}
	
	
	public static Color gray(double k) {
		return rgb(k, k, k);
	}
	
	
	public static Color hsb(double h, double s, double b) {
		h = Numeric.mod1(h / 360.0);
		int base = (int) (h * 6.0);
		double f = h * 6.0 - base;
		double p = b * (1.0 - s);
		double q = b * (1.0 - s * f);
		double t = b * (1.0 - s * (1.0 - f));
		return switch (base) {
			case 0 -> Color.rgb(b, t, p);
			case 1 -> Color.rgb(q, b, p);
			case 2 -> Color.rgb(p, b, t);
			case 3 -> Color.rgb(p, q, b);
			case 4 -> Color.rgb(t, p, b);
			case 5 -> Color.rgb(b, p, q);
			default -> null;
		};
	}
	
 
	public Color add(Color o) {
		return rgb(r + o.r, g + o.g, b + o.b);
	}
 
	
	public Color mul(double c) {
		return rgb(r * c, g * c, b * c);
	}
	
	
	public Color mul(Color o) {
		return rgb(r * o.r, g * o.g, b * o.b);
	}
	
	
	public Color div(double c) {
		return rgb(r / c, g / c, b / c);
	}
	
	
	public Color pow(double c) {
		return rgb(
				Math.pow(r, c),
				Math.pow(g, c),
				Math.pow(b, c)
		);
	}
	
	
	public Color f(DoubleUnaryOperator f) {
		return rgb(
				f.applyAsDouble(r),
				f.applyAsDouble(g),
				f.applyAsDouble(b)
		);
	}
	
	
	public double luminance() {
		return
				0.212655 * r +
				0.715158 * g +
				0.072187 * b;
	}
	
	
	/**
	 * For colors with components in [0, 1].
	 */
	public double perceivedLightness() {
		double y = luminance(); // y should be between 0.0 and 1.0
		
		if (y <= 216.0 / 24389.0 ) {      // The CIE standard states 0.008856 but 216/24389 is the intent for 0.008856451679036
			return y * (24389.0 / 27.0);  // The CIE standard states 903.3, but 24389/27 is the intent, making 903.296296296296296
		} else {
			return Math.pow(y, 1.0 / 3.0) * 116.0 - 16.0;
		}
	}
	
	
	/**
	 * sRGB gamma function. Approx. pow(v, 2.2).
	 */
	private double inverseGamma(double v) {
		if (v <= 0.04045) {
			return v / 12.92;
		} else {
			return Math.pow((v + 0.055) / 1.055, 2.4);
		}
	}
	
	/**
	 * Inverse of sRGB gamma function. Approx. pow(v, 1 / 2.2).
	 */
	private double gamma(double v) {
		if (v <= 0.0031308) {
			return v * 12.92;
		} else {
			return 1.055 * Math.pow(v, 1.0 / 2.4) - 0.055;
		}
	}

	
	private int toByte(double x) {
		return (int) (gamma(x) * 255 + 0.5);
	}

	
	private int toByteClamp(double x) {
		return Math.min(toByte(x), 255);
	}
	
	
	public int code() {
		return
				(0xFF000000)    |
				(toByte(r) << 16) |
				(toByte(g) <<  8) |
				(toByte(b)      );
	}
	
	
	public int codeClamp() {
		return
				(0xFF000000)    |
				(toByteClamp(r) << 16) |
				(toByteClamp(g) <<  8) |
				(toByteClamp(b)      );
	}
	
	
	public boolean notZero() {
		return (r != 0) || (g != 0) || (b != 0);
	}
	
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		Color color = (Color) o;
		
		if (Double.compare(color.r, r) != 0) return false;
		if (Double.compare(color.g, g) != 0) return false;
		return Double.compare(color.b, b) == 0;
	}
	
	
	@Override
	public int hashCode() {
		int result;
		long temp;
		temp = Double.doubleToLongBits(r);
		result = (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(g);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(b);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
}
