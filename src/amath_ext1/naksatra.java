// naksatra.java
// naksatra manipulation class

package amath_ext1;

import amath_base.amath_rot;

public class naksatra {

	// Protected member variables
	protected double m_dLong; // Lunar longitude in degrees
	protected int m_nNaksatra; // Naksatra (origin:1)
	protected int m_nPadam; // Padam (origin:1)
	protected int m_nAbhijit; // True if Abhijit

	static protected final String m_Names[] = {
		"Asvini",		"Bharani",		"Krittika",	"Rohini",	"Mrigasira",
		"Ardra",		"Punarvasu",	"Pushyami",	"Aslesha",	"Magha",
		"Purva Phalguni","Uttara Phalguni","Hasta",	"Citra",	"Svati",
		"Visakha",		"Anuradha",		"Jyestha",	"Mula",		"Purvashadha",
		"Uttarashadha", "Sravana",		"Dhanistha","Satabhisha", "Purvabhadra",
		"Uttarabhadra", "Revati"
	};

	static protected final String m_Rulers[] = {
		"Ketu",		"Venus",	"Sun",
		"Moon",		"Mars",		"Rahu",
		"Jupiter",	"Saturn",	"Mercury"
	};

	// Number of days per year
	static protected final double m_dYearLength = 365.25;

	// Ruler periods in years relative to 120 (vimsottari)
	static protected final int m_nRulerPeriods120[] = {
		7,			20,			6,
		10,			7,			18,
		16,			19,			17
	};

	static protected final String m_Resembles[] = {
		"Horse's face",
			"Female organ",
			"Razor",
			"Cart",
			"Head",
		"Coral bead",
			"Potter's wheel",
			"Leech",
			"Serpent",
			"Palanquin",
		"Eyes",
			"Eyes",
			"Fingers",
			"Pearl",
			"Sapphire",
		"Potter's wheel",
			"Umbrella",
			"Umbrella",
			"Crouching lion",
			"Half of square",
		"Half of square",
			"Arrow",
			"Man's head",
			"Flower",
			"Legs of cot",
		"Legs of cot",
			"Fish"
	};

	// Constructor
	public naksatra( double dLong ) {
		SetLong( dLong );
	} // constructor

	// Access
	public void SetLong( double dLong ) {
		m_dLong = Math.abs( dLong );
		// Get origin:0 padam
		m_nPadam = amath_rot._Int( 108.0 * m_dLong / 360.0 );
		m_nNaksatra = 1 + m_nPadam / 4;
		m_nPadam = 1 + (m_nPadam % 4);
		m_nAbhijit = 0;
		// Abhijit is from last quarter of Uttarashadha (21)
		// through first 15th of Sravana (22).  The range is
		// thus 360 * 83/108 to 360 * 316 / 405, or 276.6666
		// through 280.8888
		if (m_dLong >= 276.6666666 && m_dLong <= 280.8888888) {
			m_nAbhijit = 1;
			System.out.println( "Got abhijit for " + dLong );
		} // Abhijit
	} // SetLong()

	public int GetNum() {
		return m_nNaksatra;
	}

	public String GetName() {
		if (m_nAbhijit != 0) {
			return m_Names[ m_nNaksatra - 1 ] + " Abhijit";
		} // Abhijit
		else {
			return m_Names[ m_nNaksatra - 1 ];
		}
	}

	public int GetPadam() {
		return m_nPadam;
	}

	public String GetRuler() {
		return m_Rulers[ (m_nNaksatra - 1) % 9 ];
	}

	static public String GetRuler( int nNum ) {
		return m_Rulers[ (nNum - 1) % 9 ];
	}

	public int GetRulerNum() {
		return 1 + ((m_nNaksatra - 1) % 9);
	}

	public int GetRulerPeriod( int nSystem ) {
		// We only recognize vimsottari (nSystem = 120)
		if (nSystem != 120) return 0;
		return Get120Period( m_nNaksatra );
	}

	static public int Get120Period( int nValue ) {
		// Use GetRulerPeriod() to get vimsottari period for naksatra
		// if nValue == 0, get vimsottari period for naksatra
		//if (nValue == 0) nValue = m_nNaksatra;
		return m_nRulerPeriods120[ (nValue - 1) % 9 ];
	}

	// Return days remaining in period
	public double GetDaysRemaining() {
		return Get120Period( m_nNaksatra ) * m_dYearLength * (m_nNaksatra * (40.0/3.0) - m_dLong) / (40.0/3.0);
	}

	public String Fmt() {
		String s;
		s = String.valueOf( m_nNaksatra );
		s += ": ";
		s += GetName();
		s += ", P";
		s += GetPadam();
		return s;
	} // Fmt()

} // class naksatra
