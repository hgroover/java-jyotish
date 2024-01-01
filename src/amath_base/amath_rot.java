package amath_base;

// Simple class to do coordinate assembly and transformation
public class amath_rot {

	// Constructors
	public amath_rot( double c, double d, double ee ) {
		m_c = c;
		m_d = d;
		m_ee = ee;
	}
	/***
	amath_rot( double rx, double ry )
	{
		m_drx = rx;
		m_dry = ry;
		m_drz = 0.0;
	}
	amath_rot( double rx, double ry, double rz )
	{
		m_drx = rx;
		m_dry = ry;
		m_drz = rz;
	}
	***/

	// Required input values
	private double m_c, m_d, m_ee;

	// Return coordinate values
	public double m_drx, m_dry, m_drz;
	public double m_dr, m_dra;
	public double m_dsa, m_dsr;
	public double m_dx2, m_dy2, m_dz2;
	public double m_dHelio;

	// Functions moved from amath
	// Replaces Trunc()
	static public int _Int( double d ) {
		Double d2 = new Double( d );
		return d2.intValue();
	} // _Int()

	// Math.round(f) replaces Round(d)
	static public double Frac( double d ) {
		return d - _Int(d);
	} // Frac()


	// Calculate square of m_drx, m_dry and m_drz
	public void Square()
	{
		m_dx2 = m_drx * m_drx;
		m_dy2 = m_dry * m_dry;
		m_dz2 = m_drz * m_drz;
	} // Square()

	// Convert X,Y to polar coordinates m_dr, m_dra
	public void Polar( double rx, double ry )
	{
		if (ry==0.0) ry=Double.MIN_VALUE;
		//m_dr = Math.pow( rx*rx + ry*ry, 0.5 );
		m_dr = Math.sqrt( rx*rx + ry*ry );
		m_dra = Math.atan( ry / rx );
		if (m_dra<0.0) m_dra += Math.PI;
		if (ry<0.0) m_dra += Math.PI;
	} /* {Procedure polar} */

	// Convert specified polar coordinates to
	// rectangular coordinates m_drx, m_dry
	public void Rectangular( double r, double ra )
	{
        if (ra==0.0) ra = Double.MIN_VALUE;
        m_drx = r * Math.cos( ra );
        m_dry = r * Math.sin( ra );
	}

	// Input: m_drx,m_dry,m_drz
	// Output: m_drx, m_dry, m_drz
	public void Rotate()
	{
		double rd;

      Polar( m_drx, m_dry );
      m_dra += m_c;
      Rectangular( m_dr, m_dra );
      rd = m_drx;
      m_drx = m_dry;
      m_dry = 0.0;
      Polar( m_drx, m_dry );
      m_dra += m_ee;
      Rectangular( m_dr, m_dra );
      m_drz = m_dry;
      m_dry = m_drx;
      m_drx = rd;
      Polar( m_drx, m_dry );
      m_dra += m_d;
      if (m_dra<0.0) {
      	 m_dra += 2*Math.PI;
      }
      Rectangular( m_dr, m_dra );

	} // Rotate

	// Output: m_dsr, m_dsa
	public void Coords( double x, double y )
	{
      if (y==0.0) y = Double.MIN_VALUE;
      m_dsr = Math.sqrt( x*x + y*y );
      m_dsa = Math.atan( y/x );
      if ( m_dsa < 0.0) m_dsa += Math.PI;
      if ( y < 0.0 ) m_dsa += Math.PI;
	} // Coords()

} // class amath_rot

