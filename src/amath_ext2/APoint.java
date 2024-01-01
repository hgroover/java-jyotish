// APoint.java
// Point class
//package astro;

package amath_ext2;

import java.util.*;
import amath_ext1.naksatra;
import amath_base.amath_const;

// This class describes a single point (Ascendant, Sun, Moon, Mars, Mercury, etc)
// along with all its attributes

public class APoint {

	//------------------
	// Constructor
	public APoint() {
		m_dLong = 0.0;
		m_dRas = 0.0;
		m_dHouse = 0.0;
		m_dMHouse = 0.0; // House offset from moon
		m_bRetro = false;
		m_dMotion = 0.0;
		m_nak = new naksatra( 0.0 );
		m_nIndex = 0; // amath_const.PT_LAGNA, .PT_SUN, .PT_MOON, etc.
		m_Aspects = new int[amath_const.MAX_POINTS];
		m_AspectSigns = new int[amath_const.MAX_POINTS];
		if (m_Others == null)
		{
			m_Others = new APoint[amath_const.MAX_POINTS];
		}
	}

	//------------------
	// Public members
	public naksatra m_nak;
	public int m_nIndex;

	public static int MOON_VARGA = 5; // Pseudo-varga used for moon

	public final String[] m_Signs = {
		"Ar", "Ta", "Ge", "Ca", "Le", "Vi", "Li", "Sc", "Sa", "Cp", "Aq", "Pi"
	};

	// Also present in AChart as m_aPointAbbr
	public final String[] m_Points = {
		"su", "mo", "as", "me", "ve", "ma", "ju", "sa", "ra", "ke"
	};

	// Absolute value range of sun combust,
	// i.e. combust if abs(long-sun_long)<value
	protected final double[] m_CombustRange = {
		-1, 6.0, -1, 7.0, 5.0, 8.5, 5.5, 7.5, -1, -1
	};

	// Note that MTK does not get more points than
	// exaltation.
	// Points run for auspicious effect:
	// Ex	MTK		Svarasi	Friendly Neutral	Enemy
	// 1	0.75	0.5		0.25	  0.125		0
	// For inauspicious effect:
	// 0	.125	0.25	0.5		  0.75		1
	//	-2		-1	0	  1		2		3
	protected final String[] m_Positions = {
		"Db-", "Db", "", "Ex", "Ex+", "MTK"
	};

	//	-2		-1		0		1		2		3
	protected final String[] m_Occupational = {
		"BE",	"En",	"Neu",	"Fr",	"GF",	"Own"
	};

	// Map signs to owners
	protected final int[] m_Owners = {
		amath_const.PT_KUJA,	amath_const.PT_SUKRA,	amath_const.PT_BUDHA,
		amath_const.PT_MOON,	amath_const.PT_SUN,		amath_const.PT_BUDHA,
		amath_const.PT_SUKRA,	amath_const.PT_KUJA,	amath_const.PT_BRHASPATI,
		amath_const.PT_SANI,	amath_const.PT_SANI,	amath_const.PT_BRHASPATI
	};

	// Access other points - set in calc
	protected static APoint[] m_Others = null;

	// Sign offsets
	//	Ar	Ta	Ge	Ca	Le	Vi	Li	Sc	Sa	Cp	Aq	Pi
	//	0	30	60	90	120	150	180	210	240	270	300	330

	// Points of exaltation
	// Exalted if in same sign, deep if within ? degrees
	// (use 1 for ?)
	//
	protected final double[] m_Exalted = {
		0.0,	// Lagna
		10.0,	// Su 10Ar
		33.0,	// Mo 03Ta
		165.0,	// Me 15Vi
		357.0,	// Ve 27Pi
		298.0,	// Ma 28Cp
		95.0,	// Ju 05Ca
		200.0,	// Sa 20Li
		0.0,	// None
		0.0		// None
	};

	// MTK ranges - corrected 1d for mo me
	protected final double[] m_MTK = {
		0.0,	0.0,	// Lagna
		120.0,	140.0,	// Su 00-20Le
		33.0,	60.0,	// Mo 03-29Ta
		165.0,	170.0,	// Me 15-20Vi
		180.0,	195.0,	// Ve 00-15Li
		0.0,	12.0,	// Ma 00-12Ar
		240.0,	250.0,	// Ju 00-10Sa
		300.0,	320.0,	// Sa 00-20Aq
		0.0,	0.0,	// None
		0.0,	0.0		// None
	};

	// Range of motion or 0, 0 if not applicable
        // How are these values derived? ./motion-extractor.sh motion-data-output 1895-01-01 2030-12-31 15
        // (calculate motion and other values every 15 minutes at Washington DC starting at 4am on
        // 01 Jan 1895 until midnight 31 Dec 2030 and save in motion-data-output.csv, then look at min and
        // max for each relevant planet below)
	protected final double[] m_MotionRange = {
		0.0,	0.0,	// Asc
		0.0,	0.0,	// Su
		0.0,	0.0,	// Mo
		-1.3862969023030307,	2.202358591604505,	// Me
		-0.6325783219284082,	1.2586279537655187,	// Ve
		-0.4006995235976607,	0.7913238369908185,	// Ma
		-0.1366014968691612,	0.2424140704874081,	// Ju
		-0.082835051297035,		0.130888887240048,	// Sa
		0.0,	0.0,	// Ra
		0.0,	0.0		// Ke
	};

	// Matrix of natural relationships
	// These are not symmetric.  Rows represent the friends and enemies of
	// a planet.  Lagna is irrelevant.  For example, Rahu and Ketu both have
	// Sun as an enemy but Sun doesn't have them as enemies.
	protected final int[] m_Relations = {
		// Sun	Moon	Lagna	Mercury	Venus	Mars	Jupiter	Saturn	Rahu	Ketu
/*Su*/	0,		1,		0,		0,		-1,		1,		1,		-1,		0,		0,
/*Mo*/	1,		0,		0,		1,		0,		0,		0,		0,		0,		0,
/*la*/	0,		0,		0,		0,		0,		0,		0,		0,		0,		0,
/*Me*/	1,		-1,		0,		0,		1,		0,		0,		0,		0,		0,
/*Ve*/	-1,		-1,		0,		1,		0,		0,		0,		1,		0,		0,
/*Ma*/	1,		1,		0,		-1,		0,		0,		1,		0,		0,		0,
/*Ju*/	1,		1,		0,		-1,		-1,		1,		0,		0,		0,		0,
/*Sa*/	-1,		-1,		0,		1,		1,		-1,		0,		0,		0,		0,
/*Ra*/	-1,		-1,		0,		0,		1,		-1,		1,		1,		0,		0,
/*Ke*/	-1,		-1,		0,		0,		1,		1,		0,		1,		0,		0,
	};

	// Starting points for navamsa (origin:0)
	public final int[] m_NStart = { 0, 9, 6, 3 };

	//------------------
	// Access

	// Get longitude relative to origin (i.e. lagna or candra)
	public double RelativeLong( double dOrigin ) {
		return amath.ModReal( m_dLong - dOrigin + 360.0 );
	} // RelativeLong()

	// Get longitudinal separation from a point
	public double GetSeparation( double d )
	{
		// Handle various cases:
		// long=359.9, d=0.1 (0.2)
		// long=0.1, d=359.9 (-0.2)
		// long=0.1, d=0.5 (0.4)
		// long=0.5, d=0.1 (-0.4)
		// long=0.1, d=179.9 (179.8)
		// long=0.1, d=180.1 (179.8)
		double dDiff = Math.abs( m_dLong - d );
		if (dDiff > 180.0) dDiff = 360.0 - dDiff;
		return dDiff;
	} // GetSeparation()

	public double GetLong() {
		return m_dLong;
	} // GetLong()

	// Get index (amath_const.PT_*)
	public int GetIndex() {
		return m_nIndex;
	} // GetIndex()

	// Get abbreviation
	public String GetName() {
		return m_Points[m_nIndex];
	} // GetName()

	// Get origin:0 house number
	public long GetHouse() {
		return Math.round( amath._Int( m_dHouse / 30.0 ) );
	} // GetHouse()

	// Get/set daily motion value
	public void SetMotion( double d )
	{
		m_dMotion = d;
	}

	public double GetMotion()
	{
		return m_dMotion;
	}

	// Get motion within range of expected, where -100 is maximum retrograde
	// and 100 is maximum forward. If outside -100 .. 100 range, not applicable
	public int GetMotionPct()
	{
		if (m_nIndex >= amath_const.MAX_PLANETS)
		{
			return 1000;
		}
		if (m_MotionRange[m_nIndex * 2] == 0.0)
		{
			return 1000;
		}
		double dMin = m_MotionRange[m_nIndex * 2];
		double dMax = m_MotionRange[m_nIndex * 2 + 1];
                //double dSpan = dMax - dMin;
		if (m_dMotion < dMin)
		{
			return -100;
		}
		if (m_dMotion > dMax)
		{
			return 100;
		}
                // Retrograde motion is typically slower than forward motion,
                // so we need to scale them separately. When evaluating
                // motion, we may want to know if it is stopped, which is
                // more powerful than retrograde
                if (m_dMotion <= 0)
                {
                    return amath._Int( -100 * m_dMotion / dMin );
                }
                else
                {
                    return amath._Int( 100 * m_dMotion / dMax );
                }
                //return amath._Int( -100 + 200 * (m_dMotion - dMin) / dSpan );
	}

	// Given another point, return compound
	// relationship.  Note that relationships
	// are not reflexive nor are they symmetric.
	// Object "this" is reporting on whether object
	// "p" is its friend, enemy, bitter enemy, etc.
	// Return 3 if svarasi
	public int GetCompoundRel( APoint p, int nDiv ) {
		if (p.GetIndex() == GetIndex()) {
			if (m_Owners[ GetSign( nDiv ) ] == GetIndex()) {
				return 3; // Own sign
			}
			else {
				return 0; // always neutral to ourselves
			}
		} // Comparing with ourself
		int nSign2 = p.GetSign( nDiv );
		int nSign1 = GetSign( nDiv );
		// Always calculate in origin:1
		int nDistance = amath.Mod12( nSign2 - nSign1 ) + 1;
		int nRet;
		if (nDistance == 1 || (nDistance >= 5 && nDistance <= 9)) {
			nRet = -1;	// Enemy
		}
		else {
			nRet = 1;	// Friend
		}
		nRet += m_Relations[ GetIndex() * 10 + p.GetIndex() ];
		return nRet;
	} // GetCompoundRel()

	// Return exaltation (-2 for deep debilitation, -1 for debilitated,
	// 0 for nothing, 1 for exalted, 2 for highly exalted, 3 for MTK).
	// Note that MTK is more than exalted.
	public int Exaltation( int nDiv ) {
		double d = GetDivLong( nDiv );
		double dDiff = Math.abs( m_Exalted[ m_nIndex ] - d );
		if (dDiff > 180.0) dDiff = 360.0 - dDiff;
		if (dDiff < 1.0) {
			return 2; // Deep exaltation
		}
		// Check for MTK
		if (d >= m_MTK[ m_nIndex * 2 ] && d < m_MTK[ m_nIndex * 2 + 1 ]) {
			return 3; // MTK
		}
		// Check for debilitation
		if (dDiff >= 179.0) {
			return -2; // Deep debilitation
		}
		// Get origin:0 exaltation sign
		int nExSign = amath._Int( m_Exalted[ m_nIndex ] / 30.0 );
		if (amath.Mod12( nExSign - 6 ) == GetSign( nDiv )) {
			return -1; // Debilitated
		}
		if (nExSign == GetSign( nDiv )) {
			return 1; // Exalted
		}
		return 0;
	} // Exaltation()

	// Return origin:0 (amath_const.PT_*) index of owner
	public int GetOwner( int nDiv ) {
		return m_Owners[ GetSign( nDiv ) ];
	} // GetOwner()

	// Return 2 if malefic or 0 if benefic, 1 if mixed/depends on conjunction, -1 if not applicable
	public int IsMalefic( int tithi, int paksa )
	{
		switch (m_nIndex)
		{
			case amath_const.PT_SUN:
			case amath_const.PT_SANI:
			case amath_const.PT_KUJA:
			case amath_const.PT_RAHU:
			case amath_const.PT_KETU:
				return 2;
			case amath_const.PT_BUDHA:
				// In conjunction with malefic becomes malefic
				// If also with benefic, mixed
				return 1;
			case amath_const.PT_BRHASPATI:
			case amath_const.PT_SUKRA:
				return 0;
			case amath_const.PT_MOON:
				// Paksas are gaura or sukla (1) and krishna (2)
				if ((paksa == 2 && tithi >= 8) || (paksa == 1 && tithi < 8))
				{
					return 2;
				}
				return 0;
		}
		// Default - not applicable
		return -1;
	}

	// Return string describing position (Db-, Db, Ex, Ex+, MTK)
	public String GetPositionString( int nDiv ) {
		int n = Exaltation( nDiv );
		if (n == 0) return "";
		return m_Positions[ n + 2 ] + " ";
	} // GetPositionString()

	// Return string describing relation with house owner (BE, En, Neu, Fr, GF, Own)
	// Call using GetOwner: GetOwnerRelString( m_APoints[ m_APoints[n].GetOwner( nDiv ) ], nDiv )
	public String GetOwnerRelString( APoint p, int nDiv ) {
		int nRel = GetCompoundRel( p, nDiv );
		return m_Occupational[ nRel + 2 ];
	} // GetOwnerRelString()

	// Trimsamsa range and sign data for even and odd
	private int m_TRange[][] = {
		{	5, 5, 8, 7, 5 },	// Even
		{	5, 7, 8, 5, 5 }		// Odd
	};
	private int m_TSign[][] = {
		{	0, 10, 8, 2, 6 },	// Even
		{	1, 5, 11, 9, 7 }	// Odd
	};

	// Get origin:0 sign for specified division
	// Also set degrees within pseudo-house in m_DivLong;
	public int GetSign( int nDiv ) {
		// Calculate degrees within house
		double d = m_dHouse - 30.0 * amath._Int( m_dHouse / 30.0 );
		if (nDiv == MOON_VARGA) {
			return (int)Math.round( amath._Int( m_dLong / 30.0 ) );
		} // constant required for switch()
		if (nDiv != 1) {
			// Division longitude is simple except for trimsamsa
			m_dDivLong = nDiv * (d - (30.0 / nDiv) * amath._Int(d * nDiv / 30.0) );
		} // Not Rasi
		switch (nDiv) {
			case 1: // Rasi
				return (int)Math.round( amath._Int( m_dLong / 30.0 ) );
			case 2: // Hora
				return 0;
			case 3: // Drekana
				return amath.Mod12( GetSign(1) + 4 * amath._Int(d * 3.0 / 30.0) );
			case 4: // Caturthamsa
				// Sign + _Int(d/7.5) * 3;
				return amath.Mod12( GetSign(1) + 3 * amath._Int(d * 4.0 / 30.0) );
			case 7: // Saptamsa
				// If origin:0 sign is odd add 6, then d * 7 / 30
				return amath.Mod12( GetSign(1) + 6 * (GetSign(1) % 2) + amath._Int(d * 7.0 / 30.0) );
			case 9: // Navamsa
				return amath.Mod12( m_NStart[ (int)(GetSign(1) % 4) ] + amath._Int(d * 9.0 / 30.0) );
			case 10: // Dasamsa
				// Add 9 for odd sign
				return amath.Mod12( GetSign(1) + 9 * (GetSign(1) % 2) + amath._Int(d * 10.0 / 30.0) );
			case 12: // Dvadasamsa
				return amath.Mod12( GetSign(1) + amath._Int(d * 12.0 / 30.0) );
			case 16: // Sodasamsa
				return amath.Mod12( GetSign(1) + 4 * (GetSign(1) % 3) + amath._Int(d * 16.0 / 30.0) );
			case 20: // Vimsamsa
				return amath.Mod12( GetSign(1) + 8 * (GetSign(1) % 3) + amath._Int(d * 20.0 / 30.0) );
			case 24: // Caturvimsamsa
				return amath.Mod12( GetSign(1) + 4 - (GetSign(1) % 2) + amath._Int(d * 24.0 / 30.0) );
			case 27: // Bhamsa
				return amath.Mod12( GetSign(1) + 3 * (GetSign(1) % 4) + amath._Int(d * 27.0 / 30.0) );
			case 30: // Trimsamsa
				// This is interesting but I'm suspicious as there's no Cancer or Leo
				// If org:0 sign is even:
				//   sections	0-4.99	5-9.99	10-17.99	18-24.99	25-29.99
				//	 signs		0		10		8			2			6
				// If org:0 sign is odd:
				//	 sections	0-4.99	5-11.99	12-19.99	20-24.99	25-29.99
				//	 signs		1		5		11			9			7
				int n = (GetSign(1) % 2);
				int i;
				double dBase = 0.0;
				for (i = 0; i < 5; i++) {
					if (dBase + m_TRange[n][ i ] > d) {
						// The range is either 5 or 7.
						// Get degrees from start of range and divide by range length
						m_dDivLong = 30.0 * amath.Frac( (d - dBase) / m_TRange[n][i] );
						return m_TSign[n][ i ];
					} // found it
					dBase += m_TRange[n][i];
				} // for all
				// We should never get here
				return 0;
			case 40: // Khavedamsa
				return amath.Mod12( GetSign(1) + 6 * (GetSign(1) % 2) + amath._Int(d * 40.0 / 30.0) );
			case 45: // Aksavedamsa
				return amath.Mod12( GetSign(1) + 4 * (GetSign(1) % 3) + amath._Int(d * 45.0 / 30.0) );
			case 60: // Sasthyamsa
				return amath.Mod12( GetSign(1) + amath._Int(d * 60.0 / 30.0) );
		} // switch ()
		return 0;
	}

	// Get pseudo-longitude for a particular division
	public double GetDivLong( int nDiv ) {

		if (nDiv == 1)
		{
			return m_dLong;
		} // Rasi

		int nSign = GetSign( nDiv );
		return m_dDivLong + nSign * 30.0;

	} // GetDivLong()

	// Get degrees within house
	public long GetHLong() {
		// IEEEremainder operates on round() not floor()
		return Math.round( amath._Int( m_dHouse - 30.0 * amath._Int( m_dHouse / 30.0 ) ) );
	}

	// Change longitude
	public void SetLong( double d, int n ) {
		m_dLong = d;
		m_dRas = 0.0;
		m_dHouse = 0.0;
		if (d < 0.0) {
			m_dLong *= -1.0;
			m_bRetro = true;
		}
		else m_bRetro = false;
		m_nak.SetLong( d );
		m_nIndex = n;
		m_Others[n] = this;
	} // SetLong()

	// Set naksatra to specified division
	public void SetNakDiv( int nDiv ) {
		m_nak.SetLong( GetDivLong( nDiv ) );
	} // SetNakDiv()

	public void Parse( String sz, int n ) {

		// Parse input like 23Ar34.52R
		double d = 0.0;

		int i, iSign;
		String s1, s2;
		s1 = sz.toLowerCase();
		m_bRetro = false;
		if (s1.endsWith( "r" )) {
			m_bRetro = true;
			s1 = s1.substring( 0, s1.length() - 1 ); // Trim it off
		} // Retro
		for (i = 0; i < 12; i++) {
			s2 = m_Signs[i].toLowerCase();
			iSign = s1.indexOf( s2 );
			if (iSign >= 0) {
				d = Double.valueOf( s1.substring( 0, iSign ) ).doubleValue();
				double d2;
				d2 = Double.valueOf( s1.substring( iSign + 2 ) ).doubleValue();
				d += (d2 / 60.0); // Convert minutes to decimal degrees
				d += (30.0 * i); // Add base of sign
				break;
			} // Got a match
		} // for all signs
		// If all numeric, try getting valueOf()
		if (d == 0.0) {
			d = Double.valueOf( s1 ).doubleValue();
		}
		if (m_bRetro) {
			d = -d;
		} // Use negative for retrograde

		SetLong( d, n );

	} // Parse()

	public void SetAsc( double dAsc ) {
		m_dHouse = m_dLong - 30.0 * amath._Int( Math.abs( dAsc ) / 30.0 );
		if (m_dHouse < 0.0) m_dHouse += 360.0;
	} // SetAsc()

	public void SetAsc( APoint ptAsc ) {
		SetAsc( ptAsc.GetLong() );
	} // SetAsc()

	public void SetMoon( double dAsc ) {
		m_dMHouse = m_dLong - 30.0 * amath._Int( Math.abs( dAsc ) / 30.0 );
		if (m_dMHouse < 0.0) m_dMHouse += 360.0;
	} // SetMoon()

	public void SetMoon( APoint ptMoon ) {
		SetMoon( ptMoon.GetLong() );
	} // SetMoon()

	public String Fmt( int nPrecMult, int nDiv ) {
		String s = "";
		if (nDiv != 1 && nDiv != 5) {
			int n = GetSign( nDiv );
			s += amath._Int( m_dDivLong );
			s += m_Signs[ n ];
			s += Math.round( (m_dDivLong - amath._Int( m_dDivLong )) * 60.0 * nPrecMult ) / nPrecMult;
		} // Sign only
		else {
			s += GetHLong();
			s += m_Signs[ GetSign( nDiv ) ];
			// Show nPrecMult digits of minutes
			s += Math.round( (m_dLong - amath._Int( m_dLong )) * 60.0 * nPrecMult ) / nPrecMult;
			if (m_bRetro) {
				s += "R";
			}
		} // Rasi or Candra
		return s;
	} // Fmt()

	// Aspects for this point against others (who this point "sees", i.e. drishti). 0 if no aspect, 1 for 25%,
	// 2 for 50%, 3 for 75%, 4 for 100%. Does not consider graha-yuddha or combustion
	public int[] m_Aspects;
	// Signs of separation used for values in m_Aspects
	public int[] m_AspectSigns;

	// Calculate aspects for this point against all others
	public void RecalcAspects( int nDiv, APoint a[] )
	{
		ResetAspects();
		// Calculate aspects only for sun, moon, mars, mercury, venus, saturn, jupiter
		if (m_nIndex == amath_const.PT_RAHU || m_nIndex == amath_const.PT_KETU || m_nIndex == amath_const.PT_LAGNA) return;
		int n;
		for (n = 0; n <= amath_const.MAX_PLANETS; n++)
		{
			if (n==m_nIndex) continue;
			int distance1 = GetForwardDistance( GetSign(nDiv), a[n].GetSign(nDiv) );
			// Rules (per Brihat-Parasara-Hora-Sastra Ch. 28, pg 359 in Pt. Girish Chandra Sharma's edition):
			// All give full aspect to 7
			// Mars also gives full to 4 and 8
			// Saturn also gives full to 3 and 10
			// Jupiter also gives full to 5 and 9
			// All others give 1/2 to 5 and 9, and give 1/4 to 3 and 10
			if (distance1 == 7) m_Aspects[n] = 4;
			else if (distance1 == 3 || distance1 == 10)
			{
				if (m_nIndex == amath_const.PT_SANI) m_Aspects[n] = 4;
				else m_Aspects[n] = 1;
			}
			else if (distance1 == 4 || distance1 == 8)
			{
				if (m_nIndex == amath_const.PT_KUJA) m_Aspects[n] = 4;
			}
			else if (distance1 == 5 || distance1 == 9)
			{
				if (m_nIndex == amath_const.PT_BRHASPATI) m_Aspects[n] = 4;
				else m_Aspects[n] = 2;
			}
			// else conjunct or no aspect
			else continue;
			m_AspectSigns[n] = distance1;
		}
	}

	// Zero-pad number to specified number of decimal places
	public String ZeroPad( int n, int places )
	{
		String s = "00000" + n;
		return s.substring(s.length() - places);
	}

	// Get symbolic format described in data_format.txt, optionally with identifying prefix
	public String GetSymbolic( int nDiv, int withPrefix, int tithi, int paksa )
	{
		String s = "";
		if (withPrefix > 0)
		{
			s += m_Points[m_nIndex];
			s += "-";
			s += ZeroPad(nDiv,2);
			s += ":";
		}
		s += "P";	s += ZeroPad(m_nIndex + 1,2);
		s += "S";	s += ZeroPad(GetSign(nDiv) + 1,2);
		s += "N";	s += ZeroPad(m_nak.GetNum(),2);
		s += "Q";	s += ZeroPad(m_nak.GetPadam(),1);
		s += "H";	s += ZeroPad((int)GetHouse()+1,2);

		// Longitude
		s += "Z";	s += Math.abs(GetLong());

		// Retrograde or stopped
		int motionPct = GetMotionPct();
		if (Math.abs(motionPct) <= 100)
		{
			s += "B";	s += motionPct;
		}
                else System.err.println("Skipping motion for " + m_nIndex + " in div " + nDiv + " pct=" + motionPct );

		// Determine combustion
		if (m_nIndex < amath_const.MAX_PLANETS && m_CombustRange[m_nIndex] > 0.0)
		{
			double sunDistance = Math.abs( GetSeparation( m_Others[amath_const.PT_SUN].GetLong() ) );
                        System.err.println( "Distance from sun = " + sunDistance );
			if (sunDistance < 1.0)
			{
				// Deep combust
				s += "U2";
			}
			else if (sunDistance < m_CombustRange[m_nIndex])
			{
				// Normal combust
				s += "U1";
			}
                        else System.err.println( "Not combust, not in range " + m_CombustRange[m_nIndex] );
		}
                else if (m_nIndex >= amath_const.MAX_PLANETS) System.err.println( "Combust not applicable, index " + m_nIndex );
                else System.err.println( "Combust n/a, range = "+m_CombustRange[m_nIndex] );

		// Add tithi and paksa only for lagna
		if (m_nIndex == amath_const.PT_LAGNA)
		{
			s += "T";	s += ZeroPad(tithi,2);
			s += "K";	s += ZeroPad(paksa,1);
		}

		int m = IsMalefic( tithi, paksa );
		if (m >= 0)
		{
			// Mercury is mixed if with both benefic and malefic
			// If with no malefic (rare as always close to Sun), benefic
			// Otherwise malefic
			if (m == 1)
			{
				int withMalefics = 0;
				int withBenefics = 0;
				int mySign = GetSign(nDiv);
				for (int n = 0; n < amath_const.MAX_PLANETS; n++)
				{
					if (n == m_nIndex) continue;
					if (m_Others[n].GetSign(nDiv) != mySign) continue;
					int m2 = m_Others[n].IsMalefic( tithi, paksa );
					if (m2 == 2)
					{
						withMalefics++;
						//System.out.println( "Me[" + m_nIndex + "] with malefic " + n + ", m2=2" );
					}
					else if (m2 == 0)
					{
						withBenefics++;
						//System.out.println( "Me[" + m_nIndex + "] with benefic " + n + ", m2=0" );
					}
				}
				if (withMalefics == 0) m = 0;
				else if (withBenefics == 0) m = 2;
			}
			s += "X";	s += ZeroPad(m,1);
		}

		// Do all aspects in no particular order
		// Ama[4]07 for example, represents giving a full aspect to Mars by dint of 7 sign separation
		if (m_nIndex < amath_const.MAX_PLANETS)
		{
			for (int k = 0; k < amath_const.MAX_PLANETS; k++)
			{
				if (k==m_nIndex) continue;
				if (m_Aspects[k] == 0) continue;
				s += "A";
				s += m_Points[k];
				s += "[";
				s += ZeroPad(m_Aspects[k],1);
				s += "]";
				s += ZeroPad(m_AspectSigns[k],2);
			}
		}

		// Add 0, 1 or 2 lordship values
		int lagna = (GetSign(nDiv) + 12 - (int)GetHouse()) % 12;
		int n;
		for (n = 0; n < 12; n++)
		{
			if (m_Owners[(lagna + n) % 12] != m_nIndex) continue;
			s += "L";	s += ZeroPad(n+1,2); // Lord of house 01-12
		}

		// Add record of planets with (includes Rahu and Ketu)
		// Add record of planets aspecting
		if (m_nIndex < amath_const.MAX_PLANETS && m_nIndex != amath_const.PT_LAGNA)
		{
			int mySign = GetSign(nDiv);
			for (int k = 0; k < amath_const.MAX_PLANETS; k++)
			{
				if (k == m_nIndex) continue;
				if (k != amath_const.PT_LAGNA && m_Others[k].GetSign(nDiv)==mySign)
				{
					s += "C";
					s += m_Points[k];
				}
				// Find planets aspecting this
				// We've already weeded out non-planets when calculating aspects
				if (m_Others[k].m_Aspects[m_nIndex] > 0)
				{
					s += "R";
					s += m_Points[k];
					s += "[";
					s += ZeroPad(m_Others[k].m_Aspects[m_nIndex],1);
					s += "]";
				}
			} // for all others

			// Determine ownership value
			int lordIndex = m_Owners[GetSign(nDiv)];
			s += "O";
			s += GetCompoundRel(m_Others[lordIndex], nDiv);

			// Determine exaltation / debilitation
			int exalted = Exaltation(nDiv);
			if (exalted!=0)
			{
				s += "E";
				s += Exaltation( nDiv );
			} // MTK, Exalted or debilitated

		}

		return s;
	}

	// Get origin:1 count from one origin:0 sign to another origin:0 sign
	public int GetForwardDistance( int nSign1, int nSign2 )
	{
		int dist0 = (nSign2 + 12 - nSign1) % 12;
		return dist0 + 1;
	}

	//------------------
	// Private members

	private double m_dLong;	// Zodiacal longitude
	private double m_dRas;	// Right ascension (latitude)
	private double m_dHouse; // Offset from start of house 1
	private double m_dMHouse; // Offset from moon
	private boolean m_bRetro;	// Retrograde
	protected double m_dDivLong; // Divisional longitude (within sign)
	private double m_dMotion; // Daily motion

	// Reset aspects
	private void ResetAspects()
	{
		for (int n = 0; n < amath_const.MAX_POINTS; n++)
		{
			m_Aspects[n] = 0;
			m_AspectSigns[n] = 0;
		}
	}

} // class APoint
