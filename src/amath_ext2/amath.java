// amath.java - astro math class
//
// This class contains static constants, static functions, as well as data terms
// used for calculations.  Some of the terms are constant, others are calculated
// but are persistent.

package amath_ext2;

//import APoint;
import amath_base.*;
import amath_ext1.julian;

// Swiss ephemeris
import swisseph.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

// Astro math class
public class amath {

	public amath_const m_c;
	public SwissEph m_sw;

	public amath() {
		m_c = new amath_const();
		m_rotReduced = false;
		m_sw = new SwissEph();
	}

	// Static functions
	static public int atoi( String s ) {
		Integer iRes = new Integer( s );
		return iRes.intValue();
	} // atoi()

	// _Int() and Frac() here are deprecated - use amath_rot._Int() and amath_rot.Frac() instead

	// Replaces Trunc()
	static public int _Int( double d ) {
		Double d2 = new Double( d );
		return d2.intValue();
	} // _Int()

	// Math.round(f) replaces Round(d)
	static public double Frac( double d ) {
		return d - _Int(d);
	} // Frac()

	// Reduce to modulo 360
	static public double ModReal( double modin ) {
	  double a;
	  a= modin - amath_const.Modulus360 * _Int( modin/amath_const.Modulus360 );
	  if (modin<0.0)
		return (a+amath_const.Modulus360);
	  else
		return a;
	} // { function ModReal(modin : extended)  }

	// Reduce to modulo 12
	static public int Mod12( int n ) {
		return (n + 12) % 12;
	} // Mod12

	// Degrees to radans
	static public double Rad( double d ) {
	  double Temp;

	  Temp = d * (Math.PI / amath_const.Semi);
	  return Temp;
	} // { function Rad(d : extended) : extended; }

	// Radans to degrees
	static public double Deg( double r ) {
	  return (r * amath_const.Semi / Math.PI);
	} // { function Deg(r : extended) }

	// Reduce modulo (2 pi)
	static public double PieMod( double x ) {
	  return (x - 2.0 * Math.PI * _Int( x / (2.0 * Math.PI) ));
	}

	// Return sine of value in decimal degrees
	static public double DegSin( double d ) {
		return Math.sin( Rad( d ) );
	}

	// Return cosine of value in decimal degrees
	static public double DegCos( double d ) {
		return Math.sin( Rad( d ) );
	}

	// Return tangent of value in decimal degrees
	static public double DegTan( double d ) {
		return Math.tan( Rad( d ) );
	}

	// Instance data
	public double m_dJD = 0.0; // Julian date
	public double m_dCTime; // Centuries since 1/1/1900
	public double m_dCTime2; // m_CTime * m_CTime
	public double m_dAyanamsa; // Ayanamsa as signed (negative) offset
	public double m_dOblique; // Obliquity of the ecliptic in radans
	public double m_dLunarNode; // Mean lunar node
	public double m_dMnLunar; // Mean lunar longitude
	public double m_dLatitude; // Signed latitude (N = positive) in radans
	public double m_dLongitude; // Signed longitude (W = positive) in degrees
	public double m_dRAMC; // RAMC
	public double m_dHTime; // Time in hours GMT
	public double m_dLagna; // Ascendant
	public double m_dRahu; // Ascending node
	public double m_dKetu; // Descending node
	public double m_dMnSun; // Mean sun
	public double m_dDe; // Moon - sun elongation
	public double m_dLp; // Lunar perigee
	public double m_dMa; // Mean sun anomaly
	public double m_dMl; // Mean lunar longitude
	public double m_dNutation;
	public double m_dEl; // Elongation

	// New instance data values for use with Swiss Ephemeris
	public double m_dse_JD = 0.0;
	public double m_dse_LatDegrees;
	public double m_dse_LonDegrees; // E = positive in degrees
	public String m_se_Status;

	private static final int iflag_SID =
		SweConst.SEFLG_SIDEREAL + SweConst.SEFLG_NONUT + SweConst.SEFLG_SPEED;
	private static final int seAyanamsa = 1; // 1 == Lahiri; see http://www.astro.com/swisseph/swephprg.htm#_Hlk477842044 for SE_SIDM_LAHIRI
	private String m_seEphPath; // Additions to ephemeris path

	// Values calculated by Swiss Ephemeris
	public double m_dseLagna;
	public double m_dseRAMC;
	public double m_dseMoon;
	public double m_dseMoonRA;
	public double m_dseMoonMotion;
	public double [][] m_dsePoints; // Longitude, latitude (right ascension), velocity within longitude

	// Values set on a per-planet basis
	public double m_dAberration;
	public amath_rot m_rot;
	public boolean m_rotReduced; // Rotation reduced before yielding m_dRA (for debug only)

	// These are temporary values calculated for each planet.
	// Only the longitude is returned.  They should be gotten
	// immediately after calling Sun(), Moon(), or Planet()
	public double m_dHLong; // Heliocentric longitude
	public double m_dHRA; // Heliocenric right ascension
	public double m_dRA; // Right ascension or latitude
	public double m_dMotion; // Motion (negative for retrograde)

	// Tithi and paksa calculated on call to CalcTithi()
	private int m_nTithi; // 1 - 30 for Gaura Pratipat to Amavasya
	private int m_nPaksa; // 1 for Gaura, 2 for Krishna

	private String m_Tithi[] = {
		"Gaura pratipat",	"Gaura dvitiya",	"Gaura tritiya",	"Gaura caturthi",
		"Gaura pancami",	"Gaura sasthi",		"Gaura saptami",	"Gaura astami",
		"Gaura navami",		"Gaura dasami",		"Gaura ekadasi",	"Gaura dvadasi",
		"Gaura trayodasi",	"Gaura caturdasi",	"Gaura purnima",
		"Krishna pratipat",	"Krishna dvitiya",	"Krishna tritiya",	"Krishna caturthi",
		"Krishna pancami",	"Krishna sasthi",	"Krishna saptami",	"Krishna astami",
		"Krishna navami",	"Krishna dasami",	"Krishna ekadasi",	"Krishna dvadasi",
		"Krishna trayodasi","Krishna caturdasi","Amavasya",
	};

	// Values calculated for sun and used for other planets
	private double m_dSunX, m_dSunY, m_dSunZ;
	private double m_dSunAb;
	private double m_dSunX1, m_dSunY1, m_dSunZ1;

	// Instance-specific functions

    public void AddToEphPath( String s, int prepend )
    {
		// Escape any colons
		String sEscaped;
		sEscaped = s.replace(":", "\\:");
		if (m_seEphPath == "" || m_seEphPath == null)
		{
			m_seEphPath = sEscaped;
		}
		else if (prepend != 0)
		{
			m_seEphPath = sEscaped + ":" + m_seEphPath;
		}
		else
		{
			m_seEphPath = m_seEphPath + ":" + sEscaped;
		}
		System.out.println( "AddToEphPath(" + s + "," + prepend + "): new path = " + m_seEphPath);
    }

	public double DataInput()
	{
		double dTmp;
		dTmp = m_c.NextTerm();
		dTmp += m_c.NextTerm() * m_dCTime;
		dTmp += m_c.NextTerm() * m_dCTime2;
		return	Rad( dTmp );
	}

	// Input: m_rot.m_c, m_rot.m_d, m_rot.m_ee plus
	// parameters
	// Output: m_dRA, returns longitude
	public double RecToSpherical( double x,
									double y,
									int i )
	{
	  double sa,sr;
	  double dRet/*, dTemp1*/;

		m_rot.Coords( x, y );
		//dTemp1 = m_rot.m_dsa;
		dRet = Deg( m_rot.m_dsa ) + m_dNutation + m_dAberration;
		if (i==m_c.PT_SUN && m_dSunAb==1.0) {
		  dRet = ModReal( dRet + 180 );
		}
		dRet = ModReal( dRet + m_dAyanamsa );

		m_rot.Coords( m_rot.m_dsr, m_rot.m_drz );
		// Why are we comparing m_dsa, the intermediate result of rotation, against the
		// magic number 0.35 radans, then reducing by 2*pi radans (360 degrees) ???
		m_rotReduced = false;
		if (m_rot.m_dsa>0.35)
		{
			m_rotReduced = true;
			m_rot.m_dsa -= 2.0*Math.PI;
		}
		m_dRA = Deg( m_rot.m_dsa );

		return dRet;

	} // RecToSpherical()

	// Calculate essential auxiliary values based only on time
	private void SetAuxVals() {
     /* { Obliquity of the ecliptic and mean lunar node } */
     m_dOblique = Rad(23.452294- m_dCTime*0.0130125);
     m_dLunarNode = ModReal((933060.0 - 6962910.0 * m_dCTime +
                                7.5 * m_dCTime2)/3600.0);
     m_dRAMC = Rad (ModReal (15.0 *
                          (6.6460656 +
                           2400.0513 * m_dCTime +
                           0.0000258 * m_dCTime2 +
                           m_dHTime) - m_dLongitude));
     m_dMnLunar = ModReal (259.1833 - 0.05295392 * m_dCTime * 36525.0 +
                                  (0.000002 * m_dCTime + 0.002078) * m_dCTime2);
     m_dMnSun   = ModReal (279.69668 + 36000.7689 * m_dCTime +
                          0.0003025 * m_dCTime2);

     // { Moon - Sun Elongation }
     m_dDe      = ModReal (350.737486 + 445267.114 * m_dCTime - 0.001436 * m_dCTime2);

     // {Mean Lunar Perigee }
     m_dLp      = ModReal (334.32956 + 0.11140408 * m_dCTime * 36525.0 +
                        (-0.000012 * m_dCTime + 0.010325) * m_dCTime2);

     // { Mean Sun Anomaly }
     m_dMa      = ModReal (358.47584 + 35999.04975 * m_dCTime - 0.00015 * m_dCTime2);

     // { Mean lunar longitude }
     m_dMl      = ModReal (270.434164 + 13.1763965 * m_dCTime * 36525.0 +
                         (0.0000019 * m_dCTime - 0.001133) * m_dCTime2      );

     // { Nutation }
     m_dNutation= ( - (17.2327 + 0.01737 * m_dCTime) * DegSin (m_dMnLunar)
                 - 1.273 * DegSin (2 * m_dMnSun)) / 3600.0;
     // { Mean anomaly of moon } ???
     m_dEl = m_dMa - 6.40 * DegSin (51.2 + 20.2 * m_dCTime) / 3600.0;

	} // SetAuxVals()

	// Set rotation values for current planet
	private void MakeRotVals() {
		double c, d, ee;
	    c = DataInput();                                           /* {AP} */
		d = DataInput();                                           /* {AN} */

	    /* { Inclination } */
	    ee = DataInput();                                          /* {IN} */

		m_rot = new amath_rot( c, d, ee );
	} // MakeRotVals()

	// Set lat and long
	public void SetLocation( String szLat, String szLong ) {

		// Convert DD[NS]MM to signed decimal radans
		String s;
		s = szLat.toLowerCase();
		m_dLatitude = 0.0;
		m_dLongitude = 0.0;

		int n = s.indexOf( 'n' );
		int n2 = n;
		if (n >= 0) n = 1; // N = 1, S = -1
		else n2 = s.indexOf( 's' );
		if (n2 < 0) return;
		m_dLatitude = n * Double.valueOf( s.substring( 0, n2 ) ).doubleValue();
		m_dLatitude += n * Double.valueOf( s.substring( n2 + 1 ) ).doubleValue() / 60.0;
		m_dse_LatDegrees = m_dLatitude;
		m_dLatitude = Rad( m_dLatitude );

		// Convert DDD[EW]MM to signed decimal degrees
		s = szLong.toLowerCase();
		n = s.indexOf( 'w' );
		n2 = n;
		if (n >= 0) n = 1; // W = 1, E = -1
		else n2 = s.indexOf( 'e' );

		if (n2 < 0)
		{
			System.err.println( "Fatal error: could not parse E/W from longitude " + szLong );
		}

		m_dLongitude = n * Double.valueOf( s.substring( 0, n2 ) ).doubleValue();
		m_dLongitude += n * Double.valueOf( s.substring( n2 + 1 ) ).doubleValue() / 60.0;

		m_dse_LonDegrees = -m_dLongitude;

		System.out.println( "Coords: " + szLat + " " + szLong + " Latitude: " + m_dse_LatDegrees + " Long: " + m_dse_LonDegrees );
	} // SetLocation()

	// Set time.  Year is YYYY (e.g. 1960), nDST is daylight savings time in hours (e.g. 1),
	// and nTZ is signed timezone offset E of Greenwich in minutes (e.g. -300 for Washington
	// DC, 330 for India)
	public void SetTime( int nYear, int nMonth, int nDay, int nHour, int nMinute, int nSecond, int nDST, int nTZ ) {

		// Subtract daylight savings and get time in minutes
		// If DST=1 and nHour<1, we'll have a negative time value
		// but we'll adjust below after getting the Julian value
		// so we don't have to mess with month-day-year arithmetic.

		m_dse_JD = getTJD_UT( nYear, nMonth, nDay,
							  nHour, nMinute, nSecond,
							  nDST, nTZ );

		// Adjust GMT.  Example:
		// 0:40:21 DST:1 = -60 + 40 + 21/60 = -19.64 minutes
		m_dHTime = (nHour - nDST) * 60 + nMinute + nSecond / 60.0;
		// Get GMT in minutes
		m_dHTime -= nTZ;
		// Convert back to hours
		m_dHTime /= 60.0;
		// Calculate JD
		julian j = new julian();
		m_dJD = j.JulianDay( nYear, nMonth, nDay );
		// Adjust if necessary
		if (m_dHTime < 0.0) {
			m_dHTime += 24.0;
			m_dJD -= 1.0;
		}
		if (m_dHTime >= 24.0) {
			m_dHTime -= 24.0;
			m_dJD += 1.0;
		}
		// Total days within the century beginning 1/1/1900 is 36524
		// Note that julian day epoch begins at midnight.  Our century
		// figure starts from 12 noon.
		m_dJD += (m_dHTime / 24.0); // Save hours in JD
		m_dCTime = ((m_dJD - 2415020.0) - 0.5);
		// The old time calc method worked only for 20th century birth times
		int nNewTimeCalc = 1;
		if (nNewTimeCalc == 0) {
			m_dCTime /= 36525.0;
		} // Old calculation
		else {
			if (m_dCTime <= 36524.0) m_dCTime /= 36524.0;
			else {
				m_dCTime -= 36524.0; // Days in 20th century
				m_dCTime = 1.0 + m_dCTime / 36525.0; // 21st century has one more day
			} // post-2000
		} // New style
		// FIXME - This doesn't work for pre-1900 dates...
		m_dCTime2 = m_dCTime * m_dCTime; // Square - used frequently for polynomial expansion
		// This calculates the ascendant
		// FIXME Since we're assuming location has been set, check for it!
		SetAuxVals();
		// These differ by exactly 0.5 day; m_dse_JD = m_dJD - 0.5
		System.err.println( "old m_dJD = " + m_dJD );
		System.err.println( "m_dse_JD  = " + m_dse_JD );
		// Set path for ephemeris data files - apparently http: as well as directory paths are supported
		// Escape is required for colon in URL protocol prefix
		//m_sw.swe_set_ephe_path("/var/www/astro/ephe:http\\://gothmog/astro/ephe:/home/hgroover/ephe");
		if (m_seEphPath != "") m_sw.swe_set_ephe_path(m_seEphPath);
		// Loosely similar to AllPlanetsCalculator.calculateAllPlanets
		calculateAllPlanets( "sidereal", seAyanamsa, iflag_SID );
		// Loosely similar to HouseDataCalculator.calculateHouseData
		// Newer versions suggest 'W' for whole house, freejyotish used 'O' for Shripati
		calculateHouseData( 'O', iflag_SID ); // Shripati ?
	} // SetTime()

	// Adapted from freejyotish/main/calc/ChartBuilder.java
	public double getTJD_UT(int year, int mon, int day, int hour, int min, int sec, int dst, int tzOffsetMinutes) //converts local time to the Julian Date of GMT
	{
		Calendar birth =
			new GregorianCalendar(year, mon - 1, day, hour, min, sec);
		birth.add(Calendar.SECOND, -60 * (tzOffsetMinutes + dst * 60));
		//Changes time to UTC
		double UTHour = (double) birth.get(Calendar.HOUR_OF_DAY);
		double UTMin = (double) birth.get(Calendar.MINUTE);
		double UTSec = (double) birth.get(Calendar.SECOND);
		double ut = UTHour + UTMin / 60 + UTSec / 3600;
		SweDate sd =
			new SweDate(
				birth.get(Calendar.YEAR),
				birth.get(Calendar.MONTH) + 1,
				birth.get(Calendar.DATE),
				ut);
		return sd.getJulDay();
	}

    // Loosely based on freejyotish/main/calc/AllPlanetsCalculator.java:calculateAllPlanets
    public void calculateAllPlanets( String zodiac, int ayanamsa, int iflag )
    {
		int p;
		int nd;
		double[] x2 = new double[6];
		long iflgret;
		StringBuffer serr = new StringBuffer();

		// Co-opt SE_MEAN_APOG for Ketu
		// SE_NPLANETS is private but should be 23
		m_dsePoints = new double[24 /*SweConst.SE_NPLANETS*/][3];

		if (zodiac.equals("sidereal"))
		{
			m_sw.swe_set_sid_mode(ayanamsa, 0, 0);
		}
		// Calculate true node (i.e. Rahu and Ketu)
		nd = 11;
		m_se_Status = "OK";

		for (p = SweConst.SE_SUN; p <= nd; p++)
		{
			// Skip mean node (10)
			if (nd == 11 && p == SweConst.SE_MEAN_NODE)
			{
				continue;
			}

			System.out.println("Calculating p#: " + p);
			iflgret = m_sw.swe_calc_ut(m_dse_JD, p, (int) iflag, x2, serr);

			if (iflgret < 0)
			{
				System.out.print("error: " + serr.toString() + "\n");
				m_se_Status = "Error";
				continue;
			}
			else if (iflgret != iflag)
			{
				System.out.print("warning: iflgret != iflag. " + serr.toString() + "\n");
				m_se_Status = "Failed to find data";
			}

			if (p == SweConst.SE_MOON)
			{
				m_dseMoon = x2[0];
				m_dseMoonRA = x2[1];
				m_dseMoonMotion = x2[3];
			}

			//if (p <= SweConst.SE_MEAN_NODE)
			//{
				System.out.println( "Long=" + x2[0] + "; lat=" + x2[1] + "; vel=" + x2[3] );
			//snam = FJConstants.planetLongNames[p];
			//planet = new Planet(snam, p);
			//planet.setLongitude(x2[0]);//longitude
			//planet.setLatitude(x2[1]);//latitude
			//planet.setVelocity(x2[3]);//velocity in longitude
			m_dsePoints[p][0] = x2[0]; // Longitude
			m_dsePoints[p][1] = x2[1]; // Latitude (right ascension)
			m_dsePoints[p][2] = x2[3]; // Apparent velocity within longitude
			//}
			//else
			//{
			//snam = "Rahu";
			//planet = new Planet(snam, p);
			//planet.setLongitude(x2[0]);//longitude
			//planet.setLatitude(x2[1]);//latitude
			//planet.setVelocity(x2[3]);//velocity in longitude
			//}
			//planets.setPlanet(planet);
			if (p == SweConst.SE_TRUE_NODE)
			{
				m_dRahu = x2[0];
				System.out.println( "Rahu=" + m_dRahu );
			}

		} // for all planets / nodes
		m_dKetu = ModReal( m_dRahu - 180 );
		System.out.println( "Ketu=" + m_dKetu );
		m_dsePoints[SweConst.SE_MEAN_APOG][0] = m_dKetu;
		m_dsePoints[SweConst.SE_MEAN_APOG][1] = m_dsePoints[SweConst.SE_TRUE_NODE][1];
		m_dsePoints[SweConst.SE_MEAN_APOG][2] = m_dsePoints[SweConst.SE_TRUE_NODE][2];
    }

	// Loosely similar to HouseDataCalculator.calculateHouseData
	public void calculateHouseData( char houseSystem, int iflag )
	{
		double[] cusp = new double[13];
		double[] ascmc = new double[10];

		System.out.println("House System = " + houseSystem);//////////TESTING ONLY

//		the actual calculation
		m_sw.swe_houses(m_dse_JD, iflag, m_dse_LatDegrees, m_dse_LonDegrees, houseSystem, cusp, ascmc);

		m_dseLagna = ascmc[0];
		m_dseRAMC = ascmc[1]; // 2 is ARMC, 3 is Vertex
		System.out.println("Ascendant = " + ascmc[0]);
		//System.out.println("Old lagna = " + (OldLagna() - m_sw.swe_get_ayanamsa_ut(m_dse_JD)) + "; ayanamsa = " + m_dAyanamsa);
		System.out.println("Lat:  " + m_dse_LatDegrees );
		System.out.println("Long: " + m_dLongitude );
		//m_sw.swe_houses(m_dse_JD + 0.5, iflag, m_dse_LatDegrees, m_dLongitude, houseSystem, cusp, ascmc);
		//System.out.println("Asc + 12h = " + ascmc[0]);
	}

	// Set ayanamsa.  Currently only Lahiri is supported
	public void SetAyanamsa( String szAyanamsa ) {
		if (szAyanamsa.equalsIgnoreCase( "Lahiri" )) {
			/* {Lahiri's ayanamsa - beginning with 23d 9' in
	  						1950 + 50.25"*yr} */
            m_dAyanamsa = -23.15 - 50.25 * (m_dCTime - 0.50) / 36.0;
            System.out.println("Old ayana = " + m_dAyanamsa + "; sw = " + m_sw.swe_get_ayanamsa_ut(m_dse_JD));
            m_dAyanamsa = -m_sw.swe_get_ayanamsa_ut(m_dse_JD);
		} // Calculate
		else if (szAyanamsa.equalsIgnoreCase( "Simple" )) {
			// 23d + 54" per year since 1940
			m_dAyanamsa = -23.0 - 54.0 * (m_dCTime - 0.40) / 36.0;
		} // Simple ayanamsa Sri Rama used to use
		else if (szAyanamsa.equalsIgnoreCase( "Krishnamurti" )) {
			/* {Krsnamurti's ayanamsa - beginning with 0 in
	  						291AD + 50.2388475*yr} */
			double dTemp = (1900.0 - 291.0) * 50.23884750;
            dTemp /= 3600.0;  // { decimal degrees since 1900AD}
            dTemp += (50.23884750 * m_dCTime / 36.0);
            m_dAyanamsa = -dTemp;
		} // Krishnamurti
		else {
			m_dAyanamsa = 0.0;
		} // Default to tropical
	} // SetAyanamsa()


	// Member functions that should be called only after setting
	// (in order) place, time, and ayanamsa type

	// Ascendant or lagna
	public double Lagna()
	{
		return m_dseLagna;
	}

	public double OldLagna()
	{
	  double L;

		L = Math.atan(Math.cos(m_dRAMC) / (-Math.sin(m_dRAMC) * Math.cos(m_dOblique) - Math.tan(m_dLatitude) * Math.sin(m_dOblique)));
		if (L < 0.0) {
		  L = L + Math.PI;
		}
		if (Math.cos(m_dRAMC) < 0.0) {
		  L = L + Math.PI;
		}
		L = ModReal( Deg( L ) + m_dAyanamsa );

		return L;

	} // Lagna()

	// Sun longitude
	public double Sun()
	{
		m_dSunX = 0.0;
		m_dSunY = 0.0;
		m_dSunZ = 0.0;
		m_dSunAb= 0.0;
		m_dSunX1 = 0.0;
		m_dSunY1 = 0.0;
		m_dSunZ1 = 0.0;
		return Planet( m_c.PT_SUN );
	} // Sun()

	// Moon longitude - also calculates m_dRahu and m_dKetu
	public double Moon()
	{
		// Previous name:	New name:
		// Moon				dRet
		// EphemLatitude	m_dRA
		// TrueNode			m_dRahu
		m_dRA = m_dseMoonRA;;
		m_dMotion = m_dseMoonMotion;

		System.out.println( "index " + amath_const.PT_MOON + " sw " + SweConst.SE_MOON + " long " + m_dseMoon + " ra " + m_dRA + " motion " + m_dMotion );

		return m_dseMoon;
    }

    private double OldMoon()
    {
		double dRet;
		double TrueNode;

		double
		  Llng, SMn, MnNode, MnPer, MnEl, Aux1, Aux2, Aux3, Aux4, Pert,
	   //{LL    G    N       G1     D     L     L1    F     Y     ML}
		  De, Ma, Ml, El, FF, W, X, Z;

      /* { Auxiliary Angles } */
      El = m_dMa;
      Llng = m_dMl  - m_dLp;
      FF = m_dMl - m_dMnLunar;

      /* { Coefficients for long-term perturbations of Lunar elements } */
      W = DegSin (51.2 + 20.2 * m_dCTime);
      X = DegSin (193.4404 - 132.87 * m_dCTime - 0.0091731 * m_dCTime2) * 14.27;
      Aux4 = DegSin (m_dMnLunar);
      Z = -15.58 * DegSin (m_dMnLunar + 275.05 - 2.3 * m_dCTime);

      /* {Corrections to elements } */
      Ml   = (0.84 * W + X + 7.261 * Aux4) / 3600.0 + m_dMl;
      Llng = (2.94 * W + X + 9.337 * Aux4) / 3600.0 + Llng;
      De   = (7.24 * W + X + 7.261 * Aux4) / 3600.0 + m_dDe;
      El   = -6.40 * W / 3600 + El;
      FF   = (0.21 * W + X - 88.699 * Aux4 - 15.58 * Z) / 3600.0 + FF;

      /* { Aux1 contains short term lunar perturbations } */
      Aux1 = 22639.55 * DegSin (Llng) - 4586.47 * DegSin (Llng - 2.0 * De) +
               2369.912 * DegSin (2.0 * De);
      Aux1 +=		 769.02 * DegSin (2.0 * Llng) - 668.15 * DegSin (El) -
                     411.61 * DegSin (2.0 * FF) -
                     211.66 * DegSin (2.0 * Llng - 2.0 * De) -
                     205.96 * DegSin (Llng + El - 2.0 * De);
      Aux1 +=		 191.95 * DegSin (Llng + 2.0 * De) -
                     165.15 * DegSin (El - 2.0 * De) +
                     147.69 * DegSin (Llng - El) -
                     125.15 * DegSin (De) - 109.67 * DegSin (Llng + El);
      Aux1 -=		 55.17 * DegSin (2.0 * FF - 2.0 * De) -
                     45.099 * DegSin (Llng + 2.0 * FF) +
                     39.53 * DegSin (Llng - 2.0 * FF) -
                     38.43 * DegSin (Llng - 4.0 * De) +
                     36.12 * DegSin (3.0 * Llng);
      Aux1 -=		 30.77 * DegSin (2.0 * Llng - 4.0 * De) +
                     28.48 * DegSin (Llng - El - 2.0 * De) -
                     24.42 * DegSin (El + 2.0 * De) +
                     18.61 * DegSin (Llng - De) +
                     18.02 * DegSin (El + De) +
                     14.58 * DegSin (Llng - El + 2.0 * De);
      Aux1 +=		 14.39 * DegSin (2.0 * Llng + 2.0 * De) +
                     13.90 * DegSin (4.0 * De) -
                     13.19 * DegSin (3.0 * Llng - 2.0 * De) +
                      9.70 * DegSin (2.0 * Llng - El) +
                      9.37 * DegSin (Llng - 2.0 * FF - 2.0 * De) -
                      8.63 * DegSin (2.0 * Llng + El - 2.0 * De);
      Aux1 -=		  8.47 * DegSin (Llng + De) -
                      8.096 * DegSin (2.0 * El - 2.0 * De) -
                      7.65 * DegSin (2.0 * Llng + El) -
                      7.49 * DegSin (2.0 * El) -
                      7.41 * DegSin (Llng + 2.0 * El - 2.0 * De) -
                      6.38 * DegSin (Llng - 2.0 * FF + 2.0 * De);
      Aux1 -=		  5.74 * DegSin (2.0 * FF + 2.0 * De) -
                      4.39 * DegSin (Llng + El - 4.0 * De) -
                      3.99 * DegSin (2.0 * Llng + 2.0 * FF) +
                      3.22 * DegSin (Llng - 3.0 * De) -
                      2.92 * DegSin (Llng + El + 2.0 * De) -
                      2.74 * DegSin (2.0 * Llng + El - 4.0 * De);
      Aux1 -=		  2.49 * DegSin (2.0 * Llng - El - 2.0 * De) +
                      2.58 * DegSin (Llng - 2.0 * El) +
                      2.53 * DegSin (Llng - 2.0 * El - 2.0 * De) -
                      2.15 * DegSin (El + 2.0 * FF - 2.0 * De) +
                      1.98 * DegSin (Llng + 4.0 * De) +
                      1.94 * DegSin (4.0 * Llng);
      Aux1 -=		  1.88 * DegSin (El - 4.0 * De) +
                      1.75 * DegSin (2.0 * Llng - De) -
                      1.44 * DegSin (El - 2.0 * FF + 2.0 * De) -
                      1.298 * DegSin (2.0 * Llng - 2.0 * FF) +
                      1.27  * DegSin (Llng + El + De) +
                      1.23  * DegSin (2.0 * Llng - 3.0 * De);
      Aux1 -=		  1.19  * DegSin (3.0 * Llng - 4.0 * De) +
                      1.18  * DegSin (2.0 * Llng  - El + 2.0 * De) -
                      1.17  * DegSin (Llng + 2.0 * El) -
                      1.09  * DegSin (Llng - El - De) +
                      1.06  * DegSin (3.0 * Llng + 2.0 * De) -
                      0.59  * DegSin (2.0 * Llng + De);
      Aux1 -=		  0.99  * DegSin (Llng + 2.0 * FF + 2.0 * De) -
                      0.95  * DegSin (4.0 * Llng - 2.0  * De) -
                      0.57  * DegSin (2.0 * Llng - 6.0 * De) +
                      0.64  * DegSin (Llng - 4.0 * De) +
                      0.56  * DegSin (El - De) +
                      0.76  * DegSin (Llng - 2.0 * El + 2.0 * De);
      Aux1 +=		  0.58  * DegSin (2.0 * FF - De) -
                      0.55  * DegSin (3.0 * Llng + El) +
                      0.68  * DegSin (3.0 * Llng - El);

      Aux1 = (Aux1 + 0.557 * DegSin (2.0 * Llng + 2.0 * FF - 2.0 * De) +
                      0.538 * DegSin (2.0 * Llng - 2.0 * FF - 2.0 * De)   ) / 3600.0;

      /* { Sun perturbations, Mean moon, Nutation and Ayanamsa } */
      dRet = ModReal (m_dAyanamsa + Ml + Aux1 + m_dNutation);

      /* { You thought it was over, huh?  Now the latitude..... } */
      /* { Perturbations leading to lunar latitude ;  Aux1 for PL, Aux2 for FP,
                               Aux3 for SC, Aux4 for P2} */
      Aux1 = 0;
      Aux1 = 22609 * DegSin (Llng) -  4578.1 * DegSin (Llng - 2.0 * De) +
              2373.4 * DegSin (2.0 * De) + 768.0 * DegSin (2.0 * Llng) +
              192.7  * DegSin (Llng + 2.0 * De) - 182.4 * DegSin (Llng + El - 2.0 * De) -
              165.1  * DegSin (El - 2.0 * De);
      Aux1 -=
              152.5  * DegSin (2.0 * Llng - 2.0 * De) - 138.8 * DegSin (El - Llng) -
              127.0  * DegSin (El) - 115.2 * DegSin (Llng + El) -
              112.8  * DegSin (De) - 85.1 * DegSin (2.0 * FF - Llng) -
              52.1   * DegSin (2.0 * FF - 2.0 * De) + 50.64 * DegSin (3.0 * Llng);
      Aux2 = (Aux1 - 38.6 * DegSin (Llng - 4.0 * De) -
               34.1 * DegSin (2.0 * Llng - 4.0 * De)      ) / 3600.0 + FF;

      /* { Intermediate Perturbations } */
      Aux3 = (1.0 - 0.00004664 * DegCos (m_dMnLunar) -
               0.0000754 * DegCos (m_dMnLunar + 275.05 - 2.3 * m_dCTime)) *
                                                             DegSin (Aux2);
      Aux4 = 44.3 * DegSin (Llng + FF - 2.0 * De) -
              30.6 * DegSin (-Llng + FF + 2.0 * De) -
              24.6 * DegSin (-Llng + FF) -
              22.6 * DegSin (El + FF - 2.0 * De) +
              20.6 * DegSin (-Llng + FF) +
              11.0 * DegSin (-El - 2.0 * De);
      Aux4 = (Aux4 - 6.0 * DegSin (Llng + FF - 4.0 * De)) / 3600.0;
      Pert = (18519.7 * Aux3 - 6.2 * DegSin (3.0 * Aux2)) / 3600.0;
      Aux4 = -526.1 * DegSin (FF - 2.0 * De);

      m_dRA = Pert + (Aux4 / 3600);

      /* { Calculate True node } */
      Llng  = 973563.0 + 1732564379.0 * m_dCTime - 4.0 * m_dCTime2;
      SMn   = 1012395.0 + 6189.0 * m_dCTime;
      MnNode= 933060.0 - 6962911.0 * m_dCTime + 7.5 * m_dCTime2;
      MnPer = 1203586.0 + 14648523.0 * m_dCTime - 37.0 * m_dCTime2;
      MnEl  = 1262655.0 + 1602961611.0 * m_dCTime - 5.0 * m_dCTime2;
      Aux1  = (Llng - MnPer) / 3600.0;               /* {L#} */
      Aux2  = ((Llng - MnEl) - SMn) / 3600.0;      /* {L1#} */
      Aux3  = (Llng - MnNode) / 3600.0;                /* {F#} */
      MnEl  = MnEl / 3600.0;                          /* {D#} */
      Aux4  = 2.0*MnEl;                               /* {Y#} */
      TrueNode= MnNode   + 5392.0 * DegSin(2.0*Aux3-Aux4) -
                             541.0 * DegSin(Aux2);
      TrueNode= TrueNode -  442.0 * DegSin(Aux4) +
                             423.0 * DegSin(2.0*Aux3);
      TrueNode= TrueNode -  291.0 * DegSin(2.0*Aux1 - 2.0*Aux3);
      TrueNode= ModReal (TrueNode / 3600.0);

		m_dRahu = TrueNode + m_dAyanamsa;
		m_dKetu = ModReal( m_dRahu - 180 );

		return dRet;

	} // Moon()

	// Harmonic adjustment on m_rot.m_drx, etc.
	private void AdjustHarmonics( int nIndex )
	{
		int nHarm, j, k;
		double
			Harm1, Harm2, Harm3, Harm4, Harm5, Harm6, Harm7, Harm8,
			dRet;
	  Harm1 = 0.0;
	  Harm2 = 0.0;
	  Harm3 = 0.0;

        switch (nIndex) {

          case amath_const.PT_BRHASPATI:
          	 nHarm = 11;
			 break;

          case amath_const.PT_SANI:
          	 nHarm = 5;
			 break;

          //case PLURANUS :
          //case PLNEPTUNE:
          //case PLPLUTO:
          //   nHarm = 4;
		  // break;

		  default:
			  return;

		} // switch ()

        for (j = 1; j <= 3; j++) {

          if (nIndex == amath_const.PT_BRHASPATI && j == 3) {
          	Harm3 = 0;
          } else {

            if (j == 3) {
            	nHarm = nHarm - 1;
            }

            Harm4 = DataInput();
            Harm5 = 0;
            for (k=1; k <= nHarm; k++) {
			  Harm6 = m_c.NextTerm();
			  Harm7 = m_c.NextTerm();
			  Harm8 = m_c.NextTerm();
              Harm5 = Harm5 + Rad( Harm6 ) * Math.cos( (Harm6*m_dCTime + Harm8) * Math.PI/180.0 );
            } // for (k=1; ...)

            switch (j) {
              case 1:
				  Harm1 = Deg( Harm4 + Harm5 );
				  break;

              case 2:
				  Harm2 = Deg( Harm4 + Harm5 );
				  break;

              case 3:
				  Harm3 = Deg( Harm4 + Harm5 );
				  break;

            } // switch (j)

          } // if/else

        } // for (j=1; ...)

        m_rot.m_drx += Harm2;
		m_rot.m_dry += Harm1;
		m_rot.m_drz += Harm3;

	} // AdjustHarmonics()

	// Translate our planet index to Swiss Ephemeris index
	public int SwissIndex( int nPlanetIndex )
	{
		switch (nPlanetIndex)
		{
			case amath_const.PT_SUN:
				return SweConst.SE_SUN;
			case amath_const.PT_MOON:
				return SweConst.SE_MOON;
			// Lagna is handled separately
			case amath_const.PT_LAGNA:
				return SweConst.SE_EARTH;
			case amath_const.PT_BUDHA:
				return SweConst.SE_MERCURY;
			case amath_const.PT_SUKRA:
				return SweConst.SE_VENUS;
			case amath_const.PT_KUJA:
				return SweConst.SE_MARS;
			case amath_const.PT_BRHASPATI:
				return SweConst.SE_JUPITER;
			case amath_const.PT_SANI:
				return SweConst.SE_SATURN;
			case amath_const.PT_RAHU:
				return SweConst.SE_TRUE_NODE;
			// We've co-opted SE_MEAN_APOG for Ketu
			case amath_const.PT_KETU:
				return SweConst.SE_MEAN_APOG;
		}
		System.err.println( "Unhandled planet index " +  nPlanetIndex );
		return -1;
	}

	// Longitude for specified planet calculated by Swiss Ephemeris
	public double Planet( int nIndex )
	{
		// nIndex is from amath_const
		// m_dsePoints is indexed by SweConst.SE_* values
		m_dMotion = 0.0;
		m_dRA = 0.0;
		if (nIndex == m_c.PT_LAGNA)
		{
			return Lagna();
		}
		int nSwiss = SwissIndex( nIndex );
		if (nSwiss < 0)
		{
			System.err.println( "Cannot get swiss ephemeris index for " + nIndex );
			return 0.0;
		}
		// Ugly - we return latitude and velocity in public members on each call
		m_dRA = m_dsePoints[nSwiss][1];
		m_dMotion = m_dsePoints[nSwiss][2];
		double d = m_dsePoints[nSwiss][0];
		// Make longitude negative if retrograde
		if (m_dMotion < 0.0)
		{
			d = -d;
		}
		System.out.println( "index " + nIndex + " sw " + nSwiss + " long " + d + " ra " + m_dRA + " motion " + m_dMotion );
		return d;
	}

	// Longitude for specified planet
	public double OldPlanet( int nIndex )
	{

		if (nIndex == m_c.PT_MOON) {
			return Moon();
		} // Handle separately
		if (nIndex == m_c.PT_RAHU) {
			return m_dRahu;
		} // Assume Moon() has already been called
		if (nIndex == m_c.PT_KETU) {
			return m_dKetu;
		} // Assume Moon() already called

	    int j;
		double Temp, Temp1, Temp2,
			Axis, a,b,f, xh, yh, dRet;

      Temp=0;
	  Temp1=0;
	  //CPHelio=0;

      /* { Mean anomaly } */
      Temp = PieMod( DataInput() );

      /* { Eccentricity } */
      Temp1 = Deg( DataInput() );                                  /* {E} */
      Temp2 = Temp;

      /* { Solve Kepler's equation } */
      for (j=0; j<5; j++) {
        Temp2 = Temp + Temp1 * Math.sin( Temp2 );  /* {Temp2 is EA#, Axis is AU#} */
      } // for()

      /* { Semi-major axis } */
      Axis = m_c.NextTerm();

      /* { Begin velocity coordinates } */
      Temp = 0.0172021 / (Math.pow( Axis, 1.5 ) * (1 - Temp1*Math.cos( Temp2 )));     /* {E1} */

      /* { Perifocal coordinates } */
      a = -(Axis*Temp) * Math.sin( Temp2 );                             /* {XW} */

      /* { Calculate argument of perihelion and ascending node } */
      b = (Axis*Temp) * Math.sqrt( (1-Temp1*Temp1) ) * Math.cos( Temp2 );       /* {YW} */

	  // Set up m_rot with rotation values
	  MakeRotVals();

      /* { Rotate velocity coordinates } */
	  m_rot.m_drx = a;
	  m_rot.m_dry = b;
	  m_rot.m_drz = 0.0;
      m_rot.Rotate();

	  // Save these for later
	  xh = m_rot.m_drx;
	  yh = m_rot.m_dry;

      if (nIndex==m_c.PT_SUN) {  /* {store sun velocity figures} */
        m_dSunX = -m_rot.m_drx;
        m_dSunY = -m_rot.m_dry;
        m_dSunZ = -m_rot.m_drz;
        m_dSunAb = 0;
      } else {     /* { Geo components of solar velocity } */
        a = m_rot.m_drx + m_dSunX;
        b = m_rot.m_dry + m_dSunY;
        f = m_rot.m_drz + m_dSunZ;            /* {f is ZW} */
      }

      /* { Perifocal coordinates for rectangular position coordinates } */
      m_rot.m_drx = Axis * (Math.cos( Temp2 ) - Temp1);
      m_rot.m_dry = Axis * Math.sin( Temp2 ) * Math.sqrt( 1-Temp1*Temp1 );
      m_rot.m_drz = 0.0;

      /* { Rotate for rectangular position coordinates } */
      m_rot.Rotate();

      /* { Harmonic terms for outer planets } */
      if (nIndex>m_c.PT_KUJA) {

		AdjustHarmonics( nIndex );

      } // if (i > PT_KUJA)

      /* { Helio daily motion } */
	  m_rot.Square(); // Calculate m_dx2, m_dy2, m_dz2
      m_dMotion = (m_rot.m_drx * yh - m_rot.m_dry * xh) /
			(m_rot.m_dx2 + m_rot.m_dy2);   /* {XK} */
      m_dAberration = 0;

      /* { Helio rectangular to spherical coordinates } */
      m_dHLong = RecToSpherical( m_rot.m_drx, m_rot.m_dry, nIndex ); /* {Now Temp is SS# and Helio is P$} */
      m_dSunAb = 1;

      /* { store helio longitude and latitude } */
      m_dHRA = m_dRA;


      if (nIndex != amath_const.PT_SUN) {     /* { Helio to Georectangular } */
        m_rot.m_drx -= m_dSunX1;
		m_rot.m_dry -= m_dSunY1;
		m_rot.m_drz -= m_dSunZ1;
        /* {Geo daily motion } */
		m_rot.Square();
        m_dMotion = (m_rot.m_drx * b - m_rot.m_dry * a) /
			(m_rot.m_dx2 + m_rot.m_dy2);
      } else {         /* { store Sun coordinates } */
        m_dSunX1 = m_rot.m_drx;
		m_dSunY1 = m_rot.m_dry;
		m_dSunZ1 = m_rot.m_drz;
		m_rot.Square();
      } // if / else

      m_dAberration = 0.0057683 * Math.sqrt( m_rot.m_dx2 + m_rot.m_dy2 + m_rot.m_dz2 ) * Deg( m_dMotion );

      dRet = RecToSpherical( m_rot.m_drx, m_rot.m_dry, nIndex );

      if (nIndex != amath_const.PT_SUN) {
        if (m_dMotion < 0.0) {
          dRet = -dRet;
        } // Retrograde
      } else {
          dRet = Sun_private();
      } // if / else

      //Latitude = m_dRA;
	  //Gmotion = m_dMotion;

   	  return dRet;

	} // Planet()

	private double Sun_private()
	{
	double Ec, Ee, Ta, Le, Dl, Qu, Sa, Ve, Ju, Vp, Mr, Jp, Sp, Mp;
	double Ma;
    int k;

	// SetAuxVals() does set m_dEl
	//// { The following line was copied from the moon procedure and }
    //El = Ma - 6.40 * DegSin (51.2 + 20.2 * Time) / 3600.0;
    //// { I am not sure that it yields the right value. AuxVals should handle
    ////   the assignment of El }
    Ec = 0.01675104 - 0.0000418 * m_dCTime - 0.000000126 * m_dCTime2;
    Ee  = Rad (m_dMa);

    for (k = 0; k < 5; k++)
      Ee  = Rad (m_dMa) + Ec * Math.sin (Ee);

    Ta = Deg (2 * Math.atan(Math.sqrt((1 + Ec) /
                              (1 - Ec)) *
                           Math.tan(Ee / 2)          ));
    Le = ModReal (Ta - m_dMa + m_dMnSun);

    // { Long-term perturbations to the mean anomaly }
    Dl = 6.4 * DegSin (231.19 + 20.2 * m_dCTime) +
          (1.882 - 0.016 * m_dCTime) * DegSin (57.24 + 150.27 * m_dCTime);
    Dl = (Dl + 0.266 * DegSin (31.8 + 119.0 * m_dCTime) +
           0.202 * DegSin (315.6 + 893.3 * m_dCTime)       )  / 3600.0;
    Ma = m_dMa + Dl;
    // { Mars argument }
    Qu = ModReal (165.94905 + 16859.0697 * m_dCTime);
    // { Saturn argument }
    Sa = ModReal (193.1323 + 34777.259 * m_dCTime);
    // { Venus argument }
    Ve = ModReal (63.07037 + 22518.44298 * m_dCTime);
    // { Jupiter argument }
    Ju = ModReal (221.64742 + 32964.4669 * m_dCTime);

    // {Perturbations of Venus}
    Vp = 4.838 * DegCos (Ve + 90) +
          5.526 * DegCos (2.0 * Ve + 90.12) +
          2.497 * DegCos (2.0 * Ve - Ma + 257.75) +
          1.559 * DegCos (3.0 * Ve - Ma + 77.96) +
          1.024 * DegCos (3.0 * Ve - 2.0 * Ma + 50.85);
    Vp = Vp +
          0.666 * DegCos (3.0 * Ve + 270.41) +
          0.210 * DegCos (4.0 * Ve + 89.80) +
          0.154 * DegCos (5.0 * Ve - 3.0 * Ma + 34.1);
    Vp = (Vp +
           0.152 * DegCos (4.0 * Ve - 2.0 * Ma + 227.4) +
           0.144 * DegCos (4.0 * Ve - Ma + 79.0)        ) / 3600.0;

    // { Perturbations of Mars }
    Mr = 2.043 * DegCos (2.0 * Qu + 89.76) +
          1.770 * DegCos (2.0 * Qu - Ma + 306.27) +
          0.585 * DegCos (4.0 * Qu - 2.0 * Ma + 185.82) +
          0.500 * DegCos (4.0 * Qu - Ma + 317.7) +
          0.425 * DegCos (3.0 * Qu - Ma + 317.7);
    Mr = (Mr +
           0.204 * DegCos (5.0 * Qu - 2.0 * Ma + 185.5)  ) / 3600.0;

    // { Jupiter perturbation }
    Jp = 7.208 * DegCos (Ju + 91.09) +
          2.731 * DegCos (2.0 * Ju + 270.25) +
          2.600 * DegCos (Ju - Ma + 174.77) +
          1.610 * DegCos (2.0 * Ju - Ma + 292.6);
    Jp = (Jp +
           0.556 * DegCos (3 * Ju - Ma + 177.31) ) / 3600.0;

    // { Saturn perturbations }
    Sp = (0.419 * DegCos (Sa + 90.34) +
           0.320 * DegCos (Sa - Ma + 259.22)  ) / 3600.0;

    // { Moon perturbations }
    Mp = 6.454 * DegSin (m_dDe);
    Mp = Mp + 0.424 * DegSin (m_dDe - m_dEl);
    Mp = Mp / 3600.0;

   // { Sum of sun terms }
	double dRet = ModReal (Le + Dl + Vp + Mr + Jp + Sp + Mp + m_dNutation + m_dAyanamsa);

   // { Correct for aberration }
    dRet += (-(20.47 + 0.342 * DegCos (Ma))) / 3600.0;

	return dRet;

	} // Sun_private()

	// Calculate tithi and paksa from sun and moon
	public void CalcTithi( double dSun, double dMoon ) {
                 Double dMoonOrg = new Double(dMoon);
		 dMoon -= dSun;
		 if (dMoon < 0.0) dMoon += 360.0;
                 // Get tithi from 0 to 29 (truncated from 29.999...)
                 m_nTithi = 1 + _Int( dMoon / 12.0 );
		 m_nPaksa = 1 + (m_nTithi - 1) / 15;
                 System.err.println( "CalcTithi(" + (new Double(dSun)).toString() + "," + dMoonOrg.toString() + ") diff " + (new Double(dMoon)).toString() + " tithi/paksa " + m_nTithi + "/" + m_nPaksa);
	} // CalcTithi()

	// Access tithi, paksa
	public int GetTithi() {
		return 1 + (m_nTithi - 1) % 15;
	} // GetTithi()

	public int GetPaksa() {
		return m_nPaksa;
	} // GetPaksa

	public String TithiName() {
		return m_Tithi[ m_nTithi - 1 ];
	} // TithiName()

}

