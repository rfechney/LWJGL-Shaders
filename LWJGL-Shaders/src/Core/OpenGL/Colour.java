package Core.OpenGL;

/**
 * This class is just a simple container for an ARGB colour.
 * @author Ryan Fechney
 */
public class Colour
{
	public double a=0;
	public double r=0;
	public double b=0;
	public double g=0;

	public Colour()
	{
	}	
	
	public Colour(double newR, double newG, double newB, double newA)
	{
		a = newA;
		r = newR;
		g = newG;
		b = newB;
	}
	
	public void gradient(Colour left, Colour right)
	{
		a = (right.a - left.a)/2;
		r = (right.r - left.r)/2;
		g = (right.g - left.g)/2;
		b = (right.b - left.b)/2;
	}
}
