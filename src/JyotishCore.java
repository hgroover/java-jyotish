//******************************************************************************
// JyotishCore.java:	Core class with Jyotish calculation methods
//                      previously in AChart.java
//******************************************************************************
//package astro;  // the package name maps to a directory

import java.util.*;
import java.net.*;
import java.io.*;
//import astro.*;
import amath_ext2.*;
import amath_base.*;
import amath_ext1.*;
import generated.*;

// Debug stuff
//import com.mhuss.AstroLib.*;

// Swiss ephemeris
import swisseph.*;


// This represents a single set of periods
class DasaSet {

	// This is a compact representation of data needed to
	// display a timeline vertically:

	//      - dd/mm/yy hh:mm:ss x.x yrs
	// P1 +|
	//     |- dd/mm/etc
	// P2 -| P2 +|
	//     |     |- dd/mm/etc
	//     | P3 +|
	//     |     |- dd/mm/etc
	//     | P4 -| P4 +|
	//     |     |     |- dd/mm/etc
	//     |     | P5 +|
	//     |     |     |- dd/mm/etc
	//     |     | P6 +|
	//     |     |     |- dd/mm/etc
	//     |     | P7 +|
	//     |     |     |- dd/mm/etc
	//     |     | P8 +|
	//     |     |     |- dd/mm/etc
	//     |     | P9 +|
	//     |     |     |- dd/mm/etc
	//     |     | P1 +|
	//     |     |     |- dd/mm/etc
	//     |     | P2 +|
	//     |     |     |- dd/mm/etc
	//     |     | P3 +|
	//     |     |- dd/mm/etc
	//     | P5 +|
	//     |     |- dd/mm/etc
	//     | P6 +|
	//     |     |- dd/mm/etc
	//     | P7 +|
	//     |     |- dd/mm/etc
	//     | P8 +|
	//     |     |- dd/mm/etc
	//     | P9 +|
	//     |     |- dd/mm/etc
	//     | P1 +|
	//     |- dd/mm/etc
	// P3 +|
	//     |- dd/mm/etc
	// P4 +|
	//     |- dd/mm/etc
	// P5 +|
	//     |- dd/mm/etc
	// P6 +|
	//     |- dd/mm/etc
	// P7 +|
	//     |- dd/mm/etc
	// P8 +|
	//     |- dd/mm/etc
	// P9 +|
	//      - dd/mm/etc

	// Each set has a starting date, ending
	// date, birth date (which may or may not
	// fall between start and end) and links
	// to 9 sub-periods.  All dates are
	// stored as double (Julian).
	private DasaSet m_apChildren[];
	private Double m_Start; // Start time
	private Double m_Birth; // Birth time
	private Double m_End; // End time
	private int m_nRuler;
	private boolean m_bOpen; // Displayed open (children visible) or closed
	private int m_nLevel; // 0 for maha-dasa, 1 for bhukti, etc.
	private int m_nX1, m_nX2, m_nY1, m_nY2; // Boundaries of hot spot
	private int m_nY2MaxChild; // Maximum vertical boundary of a child's hot spot

	// Used for iterating via GetFirst() and GetNext()
	private int m_nCurrent;

	// Constructor
	// Besides specifying the level for this object we also
	// specify how many additional levels to initialize.
	public DasaSet( double dBirth, double dStart, double dEnd, int nRuler, int nLevel, int nDepth ) {

		m_Birth = new Double( dBirth );
		m_Start = new Double( dStart );
		m_End = new Double( dEnd );
		m_nRuler = nRuler;
		m_nLevel = nLevel;
		m_bOpen = (nLevel == 0);
		m_apChildren = new DasaSet[9];

		m_nCurrent = 0;
		m_nX1 = 0;
		m_nX2 = 0;
		m_nY1 = 0;
		m_nY2 = 0;
		m_nY2MaxChild = 0;

		int nChild;

		if (nDepth > 0) {

			InitChildren( nDepth );

		} // Initialize children
		else {

			for (nChild = 0; nChild < 9; nChild++) {
				m_apChildren[ nChild ] = null;
			} // for all children

		} // set children to null

	} // Constructor

	// Initialize all children
	protected void InitChildren( int nDepth ) {

		double dStart = m_Start.doubleValue();
		// Length of entire period in days
		double dPeriod = m_End.doubleValue() - dStart;
		// Length of sub-period in days
		double dSubPeriod;
		int nChild;

		for (nChild = 0; nChild < 9; nChild++) {
			dSubPeriod = dPeriod * naksatra.Get120Period( m_nRuler + nChild ) / 120.0;
			m_apChildren[ nChild ] = new DasaSet( m_Birth.doubleValue(), dStart,
					dStart + dSubPeriod, 1 + (m_nRuler + nChild - 1) % 9,
					m_nLevel + 1, nDepth - 1 );
			dStart += dSubPeriod;
		} // for all children

	} // InitChildren()

	// Begin iteration, returning first child or null
	public DasaSet GetFirst() {
		for (m_nCurrent = 0;
				m_nCurrent < 9 &&
				m_apChildren[ m_nCurrent ] != null;
				m_nCurrent++) {
					if (m_apChildren[ m_nCurrent ].GetEnd() >= m_Birth.doubleValue()) {
						return m_apChildren[ m_nCurrent ];
					} // Ends after birth
		} // while iterating through children
		return null;
	} // GetFirst()

	// Return next child or null
	public DasaSet GetNext() {
		for (m_nCurrent++;
				m_nCurrent < 9 &&
				m_apChildren[ m_nCurrent ] != null;
				m_nCurrent++) {
					if (m_apChildren[ m_nCurrent ].GetEnd() >= m_Birth.doubleValue()) {
						return m_apChildren[ m_nCurrent ];
					} // Ends after birth
		} // while iterating through children
		return null;
	} // GetNext()

	// Accessor functions

	// Get starting JD
	public double GetStart() {
		return m_Start.doubleValue();
	} // GetStart()

	// Get ending JD
	public double GetEnd() {
		return m_End.doubleValue();
	} // GetEnd()

	// Get duration in years
	public double GetYears() {
		return (m_End.doubleValue() - m_Start.doubleValue()) / 365.25;
	} // GetYears()

        // Get age at start in years
        public double GetStartAge() {
            return (m_Start.doubleValue() - m_Birth.doubleValue()) / 365.25;
        } // GetStartAge

        // Get age at end in years
        public double GetEndAge() {
            return (m_End.doubleValue() - m_Birth.doubleValue()) / 365.25;
        } // GetEndAge

        // Starts before birth?
        public boolean StartsBeforeBirth() {
            return m_Start.doubleValue() < m_Birth.doubleValue();
        } // StartsBeforeBirth

        // Ends before birth?
        public boolean EndsBeforeBirth() {
            return m_End.doubleValue() < m_Birth.doubleValue();
        }

	// Get start formatted appropriately
	public String GetFmtStart() {
		if (m_nLevel < 3) {
			// Show date and age
			return FmtDate( m_Start.doubleValue(), m_Start.doubleValue() - m_Birth.doubleValue() );
		} // Mahadasa, bhukti
		else {
			return FmtDate( m_Start.doubleValue() );
		} // Antaradasa, pratyantara, etc.
	} // GetFmtStart()

	// Get end formatted appropriately
	public String GetFmtEnd() {
		if (m_nLevel < 3) {
			return FmtDate( m_End.doubleValue(), m_End.doubleValue() - m_Birth.doubleValue() );
		} // Mahadasa, bhukti
		else {
			return FmtDate( m_End.doubleValue() );
		} // Antaradasa, pratyantara
	} // GetFmtEnd()

        // Get end as date only regardless of level
        public String GetFmtEndDate() {
            double d = m_End.doubleValue();
            julian j = new julian();
            j.Reverse( d );
            return j.ShortDate();
        } // GetFmtEndDate()

	// Get birth formatted mm/dd/yyyy hh:mm:ss
	public String GetFmtBirth() {
		return FmtDate( m_Birth.doubleValue() );
	} // GetFmtBirth()

	// Get ruler string
	public String GetRuler() {
		return naksatra.GetRuler( m_nRuler );
	} // GetRuler()

	// Get 2-letter ruler abbreviation
	public String GetRulerAbbr() {
		return GetRuler().substring( 0, 2 );
	} // GetRulerAbbr()

	public int GetLevel() {
		return m_nLevel;
	} // GetLevel()

	// Return true if open
	public boolean IsOpen() {
		return m_bOpen;
	} // IsOpen()

	// Set hot spot
	public void SetHotSpot( int X1, int X2, int Y1, int Y2, int Y2Max ) {
		m_nX1 = X1;
		m_nX2 = X2;
		m_nY1 = Y1;
		m_nY2 = Y2;
		m_nY2MaxChild = Y2Max;
	} // SetHotSpot

	// Toggle open state
	public void ToggleOpen() {
		m_bOpen = !m_bOpen;
		// If Opened we may need to initialize another level
		if (m_bOpen && m_apChildren[ 0 ] == null) {
			InitChildren( 1 );
		} // Initialize children
	} // ToggleOpen()

	// Recursively find dasa at hot spot.
	// Return null if not found
	public DasaSet FindDasa( int x, int y ) {
		// We should never have negative values
		if (m_nY1 <= 0 && m_nY2 <= 0) return null;
		// First check for a hit on us
		if (y >= m_nY1 && y < m_nY2) {
			if (x >= m_nX1 && x < m_nX2) {
				return this;
			} // got a hit
			return null;
		} // Potential hit on us
		else if (y >= m_nY1 && y < m_nY2MaxChild) {
			int nChild;
			DasaSet p;
			for (nChild = 0; nChild < 9; nChild++) {
				p = m_apChildren[ nChild ].FindDasa( x, y );
				if (p != null) return p;
			} // for all children
			return null;
		} // Potential hit on a child
		return null; // no hit
	} // FindDasa

	// Get julian date formatted mm/dd/yyyy hh:mm:ss
	protected String FmtDate( double d ) {
		julian j = new julian();
		j.Reverse( d );
		return j.FullDate();
	} // FmtDate

	// Get julian date formatted mm/dd/yyyy xx.xx yrs
	protected String FmtDate( double d, double age ) {
		julian j = new julian();
		j.Reverse( d );
		Double dd = new Double( Math.round( age / 3.6525 ) / 100.0 );
		return j.FullDate().substring( 0, 11 ) + dd.toString() + " yrs";
	} // FmtDate

        // Get end age
        public String GetFmtEndAge()
        {
            Double dd = new Double( Math.round( GetEndAge() * 100.0 ) / 100.0 );
            return dd.toString() + " yrs";
        } // GetFmtEndAge()

	// Expose for testing
    public String FmtDateTest( double dJ )
    {
		return FmtDate( dJ );
    }

	public void Recalc( int nLevel ) {


	} // Recalc()

} // class DasaSet

//==============================================================================
// Main Class for JyotishCore
//==============================================================================
public class JyotishCore
{
    // By convention, the major and minor should match what's defined in Makefile
        public final String m_CoreVersion = "v2.01.125";

	private amath	 m_am;
	private int		 m_nPtImageX; // Max width of point image
	private int		 m_nPtImageY; // Max height of point image
	private int		 m_nSgnImageX; // Max width of sign image
	private int		 m_nSgnImageY;
	private boolean  m_fAllLoaded = false;
	private boolean	 m_bResizing = false;
	private int		 m_nDragX1 = 0, m_nDragX2 = -1;
	private int		 m_nDragY1 = 0, m_nDragY2 = -1;
	private int		 m_nMaxText = 16; // Maximum text lines vertically
	private int		 m_initStage = 0; // 0 = init() not run; 1 = init2() not run; 2 = initialization completed


	public final String[] m_aPoints = {
		"Sun",	"Moon",	"Ascendant", "Mercury", "Venus", "Mars", "Jupiter", "Saturn", "Rahu", "Ketu"
	};
	// Also present all lowercase in APoint
	public final String[] m_aPointAbbr = {
		"Su", "Mo", "Asc", "Me", "Ve", "Ma", "Ju", "Sa", "Ra", "Ke"
	};
	public final String[] m_aSPoints = {
		"Ravi",	"Chandra", "Lagna", "Budha", "Sukra", "Kuja", "Brhaspati", "Sani", "Rahu", "Ketu"
	};

	// Temporary values parsed from m_aHouseAspectPaths on call to GetHouseAspectPath()
	int m_tempXOffset;
	int m_tempYOffset;
	int m_tempSourceXOffset;
	int m_tempDestXOffset;

	// SVG paths for aspects based on origin house (origin:0 range 0-11) and origin:3 aspect (for 3 through 10 with 6 unused)
	// First two values are offsets for additional sets, i.e. 3 planets in H1 giving drishti to H7 get a small successive x offset
	// Must match m_aHouseAspectPathCounts in dimension
	// Now moved to generated/LayoutData
	// Use counts of m_aHouseAspectPaths. Used to determine incremental shifting. Reset to 0 by ResetPathCounts()
	// and updated by GetHouseAspectPath()
	protected int[][] m_aHouseAspectPathCounts = {
		{ 0,0,0,	0,	0,0,0,0 },
		{ 0,0,0,	0,	0,0,0,0 },
		{ 0,0,0,	0,	0,0,0,0 },
		{ 0,0,0,	0,	0,0,0,0 },

		{ 0,0,0,	0,	0,0,0,0 },
		{ 0,0,0,	0,	0,0,0,0 },
		{ 0,0,0,	0,	0,0,0,0 },
		{ 0,0,0,	0,	0,0,0,0 },

		{ 0,0,0,	0,	0,0,0,0 },
		{ 0,0,0,	0,	0,0,0,0 },
		{ 0,0,0,	0,	0,0,0,0 },
		{ 0,0,0,	0,	0,0,0,0 },
	};

	public final int MAX_DIVISIONS = 15;
	public final int[] m_DivisionCycle = {
		1,	// Rasi
		//2,	// Hora
		3,	// Drekana
		4,	// Caturthamsa
		7,	// Saptamamsa

		9,	// Navamamsa
		10,	// Dasamamsa
		12,	// Dvadasamsa
		16,	// Sodasamsa
		20,	// Vimsamsa

		24,	// Caturvimsamsa
		27,	// Bhamsa
		30,	// Trimsamsa
		40,	// Catvarimsamsa
		45,	// Aksavedamsa

		60	// Sasthyamsa
	};

	// PARAMETER SUPPORT:
	//		Parameters allow an HTML author to pass information to the applet;
	// the HTML author specifies them using the <PARAM> tag within the <APPLET>
	// tag.  The following variables are used to store the values of the
	// parameters.
    //--------------------------------------------------------------------------

    // Members for applet parameters
    // <type>       <MemberVar>    = <Default Value>
    //--------------------------------------------------------------------------
	public String m_Style = "South";
	public int m_nStyle = 0; // 1 = north
	public volatile int m_nRecalc = -1; // Auto-Recalc interval in seconds.  Default of -1 means to recalculate once only.
	protected int m_nCalculated = 0; // TRUE when we have valid data
	public String m_Date = "";
	public String m_Time = "";
	public String m_Lat = "";
	public String m_Long = "";
	public String m_TZ = "";
	public int m_nDST = 0;
	public String m_Text = "";
	// See data_format.txt for format of coded data available to browser objects
	public String m_CodedOutput = "";
	public boolean m_bResizable = true;
	public String m_Ayanamsa = "Lahiri";
	public int m_nDivision = 1; // 1 = rasi, 2 = hora, 3 = drekana, 4 = caturthamsa, 5 = candra lagna, 7 = saptamsa, 9 = navamsa,
								// 10 = dasamsa, 12 = dvadasamsa, 16 = sodasamsa, 20 = vimsamsa, 24 = caturvimsamsa, 27 = bhamsa,
								// 30 = trimsamsa, 40 = Khavedamsa, 45 = Aksavedamsa, 60 = sasthyamsa
	public int m_nDebug = 1;
	public int m_jsDebugOutput = 0; // If nonzero, use jsj_Output() in javascript to issue debug output

	public int m_nYear; // e.g. 1960
	public int m_nMonth; // e.g. Jan=1
	public int m_nDay; // e.g. 1-31
	public int m_nHour; // 24-hour local time
	public int m_nMinute; // Minutes local time
	public int m_nSecond; // Seconds local time
	public int m_nTZ; // Timezone in minutes E of greenwich
	public int m_nPrecision = 0; // Display precision in decimal places for minutes
	protected int m_nPrecMult = 1; // Precision multiplier (10 ** m_nPrecision)
	// Removed http://hgsoft.com from lead of URL
	public String m_PostAddr = "achart-output.php";
	public String m_PostTarget = "_blank";
        public String m_htmlDivs = ",1,9,"; // Divisions to print for html5 output

	//private DlgStyle m_dlgStyle;


	// Other public members
	public APoint m_Points[];
	public int m_PGrade[];

	// Other private members
	protected boolean m_bDasaDirty = true; // Recalc dasas
	protected DasaSet m_pDasa = null;
	protected int m_nDasaSkipLines = 0; // Lines to skip
	protected int m_nDasaSkip; // Temporary counter
	protected int m_nDasaLinesRemain = 0; // Lines remaining (0 or !0) to scroll down
	protected int m_nDasaUpTop = 2; // Offset from top of panel
	protected int m_nDasaDnBot = 2; // Offset from bottom of panel
	protected int m_nDasaBtnHeight = 20; // Height of up and down buttons
	protected int m_nDasaBtnRight = 2; // Offset from right edge
	protected int m_nDasaBtnWidth = 24; // Width of up and down buttons

	// Parameters used for drawing northern style chart using HTML4 div positioning
	// and (some) for using HTML5 inline svg
	private int m_boxWidth = 80; // Width in px
	private int m_boxHeight = 20; // Height in px
	protected int m_chartWidthHtml = 800;
	protected int m_chartHeightHtml = 800;
	private final String m_northBG = "images/northern_bg.png";
	// Vector sets for 0-5 slots, 6-10 slots; format: vectorset[;vectorset[...]]
	// Vectorset format: startx,starty,incx,incy,count,align,valign
	// These are translated to actual chart dimensions when parsed - expressed below assuming
	// m_chartWidthHtml == 800 and m_chartHeightHtml == 800
	private final String NVECTORS[][] = {
			// 0-5 slots					6+ slots
		{	"400,260,0,-20,5,center,bottom","490,280,20,-20,4,right,top;570,200,-20,-20,4,right,bottom"	}, // H1
		{	"200,115,0,-20,7,center,top",	"300,15,0,20,1,center,top;200,135,0,-20,7,center,top;100,15,0,20,1,center,top" }, // H2
		{	"15,140,0,20,6,left,top",		"15,100,0,20,10,left,top" }, // H3
		{	"200,360,0,20,5,center,top",	"90,345,-20,20,4,left,bottom;30,405,20,20,4,left,top;310,345,20,20,4,right,bottom;370,405,-20,20,4,right,top" }, // H4
		{	"15,560,0,20,6,left,top",		"15,520,0,20,10,left,top" }, // H5
		{	"200,695,0,-20,6,center,bottom", "100,795,0,20,1,center,bottom;200,675,0,20,7,center,bottom;300,795,0,20,1,center,bottom" }, // H6
		{	"400,560,0,20,6,center,top",	"297,536,-20,20,4,left,bottom;222,596,20,20,4,left,top;510,536,20,20,4,right,bottom;570,596,-20,20,4,right,top" }, // H7
		{	"600,795,0,-20,6,center,bottom", "500,795,0,-20,1,center,bottom;600,795,0,-20,7,center,bottom;700,795,0,-20,1,center,bottom" }, // H8
		{	"795,640,0,-20,6,right,top",	"795,680,0,-20,10,right,top" }, // H9
		{	"600,440,0,-20,6,center,top",	"695,465,20,-20,4,right,top;755,405,-20,-20,4,right,bottom;490,465,-20,-20,4,left,top;430,405,20,-20,4,left,bottom" }, // H10
		{	"795,240,0,-20,6,right,top",	"795,280,0,-20,10,right,top" }, // H11
		{	"600,115,0,-20,6,center,top",	"700,15,0,-20,1,center,top;600,135,0,-20,7,center,top;500,15,0,-20,1,center,top" }, // H12
	};
	// CSS style preamble issued for northern chart
	private final String NORTHERN_HTML_CSS = "<style type=\"text/css\">\n" +
		"div.chart {\n" +
		//"	clear:both;\n" +
		"	position:relative;\n" +
		"	width:805px;\n" +
		"	height:805px;\n" +
		"	padding: 5px;\n" +
		"	z-index:1;\n" +
		"}\n" +
		"div.ctable {\n" +
		//"	clear:both;\n" +
		//"	position:relative;\n" +
		//"	top:805px;\n" +
		"	width:800px;\n" +
		//"	height:380px;\n" +
		//"	background:#c7f15f;\n" +
		"	margin-bottom:25px;\n" +
		"}\n" +
		"div.slot {\n" +
		"	position:absolute;\n" +
		"	font-size:10px;\n" +
		"	z-index:100;\n" +
		// Removed debug properties:
		//"	background:#e3f14f;\n" +
		//"	border:1px solid gray;\n" +
		"}\n" +
		"</style>";
	// Removed debug properties:


/*** Caller's sequence ***
jj = new JyotishCore();
jj.init();
jj.init2();
jj.Recalc();
jj.RenderHtml5();
System.out.println( jj.m_CodedOutput );
**************************/

        // JyotishCore Class Constructor
	//--------------------------------------------------------------------------
        public JyotishCore()
	{
		m_nPtImageX = 0;
		m_nPtImageY = 0;
		m_nSgnImageX = 0;
		m_nSgnImageY = 0;
		// TODO: Add constructor code here
		//debugOut( "Constructor, V1.0" ); // Can't do that
	}

	// Debug support
	public void debugOut( String szMsg )
	{
		if (m_nDebug != 0) {

			try {
				// This appears on Java console in Netscape (not!)
				System.err.println( szMsg );
			}
			catch (Exception e)
			{
				System.err.println( "Exception on showStatus(" + szMsg + "): " + e.toString() );
			}
		} // Debugging

	} // debugOut()

	// Raw output to html in browser (via exposed Javascript)
	public void browserOut( String html )
	{
                System.out.println( "--- browserOut() called standalone ---" );
                System.out.println( html );
                System.out.println( "=== end browserOut() ===" );
	} // browserOut()

	// The init() method is called by the AWT when an applet is first loaded or
	// reloaded.  Override this method to perform whatever initialization your
	// applet needs, such as initializing data structures, loading images or
	// fonts, creating frame windows, setting the layout manager, or adding UI
	// components.
    //--------------------------------------------------------------------------
	public void init()
	{
		// We should not have called start() yet!

		m_initStage = 0;

		/*******************************************/

		m_initStage = 1;

                debugOut( "Exiting JyotishCore init()" );
	}

	// Second stage of init called within Thread context
	public void init2()
	{
                debugOut( "Entering JyotishCore init2()" );

		if (m_initStage >= 2)
		{
			debugOut( "Already at stage " + m_initStage );
			return;
		}

		try {
			// Allocate point array
			m_Points = new APoint[amath_const.MAX_POINTS];
			m_PGrade = new int[amath_const.MAX_POINTS];
			for (int i = 0; i < m_Points.length; i++) {
				m_Points[ i ] = new APoint();
				// Seed grade vector with identity values
				m_PGrade[ i ] = i;
			}
		}
		catch (Exception e)
		{
			debugOut( "Exception 1: " + e.toString() );
		}

		// Allocate math object
		m_am = new amath();

                // This used to be done by argument parsing. Now it is the caller's
                // responsibility to set these values
                //		if (s1.equalsIgnoreCase( PARAM_Text )) { m_Text = s2; continue; }
                //		if (s1.equalsIgnoreCase( PARAM_Ayanamsa )) { m_Ayanamsa = s2; continue; }
                //		if (s1.equalsIgnoreCase( PARAM_Time )) { m_Time = s2; continue; }
                //		if (s1.equalsIgnoreCase( PARAM_Date )) { m_Date = s2; continue; }
                //		if (s1.equalsIgnoreCase( PARAM_Lat )) { m_Lat = s2; continue; }
                //		if (s1.equalsIgnoreCase( PARAM_Long )) { m_Long = s2; continue; }
                //		if (s1.equalsIgnoreCase( PARAM_TZ )) { m_TZ = s2; continue; }
                //		if (s1.equalsIgnoreCase( PARAM_DST )) { m_nDST = amath.atoi( s2 ); continue; }
                //		if (s1.equalsIgnoreCase( PARAM_Debug )) { m_nDebug = amath.atoi( s2 ); continue; }
                //		if (s1.equalsIgnoreCase( PARAM_Post )) { m_PostAddr = s2; continue; }
                //		if (s1.equalsIgnoreCase( PARAM_PostTarget )) { m_PostTarget = s2; continue; }


		// Set house and moon offsets
		for (int i = amath_const.PT_FIRST; i <= amath_const.PT_LAST; i++) {
			m_Points[ i ].SetAsc( m_Points[ amath_const.PT_LAGNA ] );
			m_Points[ i ].SetMoon( m_Points[ amath_const.PT_MOON ] );
		} // for all points

		debugOut( "Grading points" );

		// Grade points
		GradePoints( amath_const.PT_LAGNA );

		// Initialization completed
		m_initStage = 2;

                debugOut( "Exiting JyotishCore init2()" );
	}

	// Represent style as a string
	public String StyleName()
	{
		switch (m_nStyle) {
		case 0: return "Southern";
		case 1: return "Northern";
		case 2: return "Chakra";
		}
		return "?";
	} // StyleName()

	// Represent division as a string
	public String DivisionName()
	{
		if (m_nDivision == APoint.MOON_VARGA) {
			return "Candra lagna";
		}
		switch (m_nDivision) {
		case 1: return "Rasi";
		case 2: return "Hora";
		case 3: return "Drekana";
		case 4: return "Caturthamsa";
		case 7: return "Saptamamsa";
		case 9: return "Navamamsa";
		case 10: return "Dasamamsa";
		case 12: return "Dvadasamsa";
		case 16: return "Sodasamsa";
		case 20: return "Vimsamsa";
		case 24: return "Caturvimsamsa";
		case 27: return "Bhamsa";
		case 30: return "Trimsamsa";
		case 40: return "Khavedamsa";
		case 45: return "Aksavedamsa";
		case 60: return "Sasthyamsa";
		} // switch
		return "?";
	} // DivisionName()

	// Dump everything in a given sign
	protected void DumpInSign( PrintStream o, int nSign ) {
		int i;
		for (i = 0; i < amath_const.MAX_POINTS; i++) {
			int j = m_PGrade[ i ];
			if (m_Points[ j ].GetSign( m_nDivision ) != nSign) continue;
			if (j == amath_const.PT_LAGNA) {
				o.println( "<hr>" );
			}
			else {
				o.println( "<br>" );
			}
			o.println( m_aPointAbbr[ j ] + ": " + m_Points[ j ].Fmt( m_nPrecMult, m_nDivision ) );
		} // for all
	} // DumpInSign()

	// Dump a single division (m_nDivision) along with longitudes, aspects, etc.
	protected void DumpDivisionSouthern( PrintStream o ) {

		// Southern style chart
		o.println( "<p><table width=80% border=2><tr><td width=25%>Pi" );
		DumpInSign( o, 11 );
		o.println( "</td><td width=25%>Ar" );
		DumpInSign( o, 0 );
		o.println( "</td><td width=25%>Ta" );
		DumpInSign( o, 1 );
		o.println( "</td><td width=25%>Ge" );
		DumpInSign( o, 2 );
		o.println( "</td></tr>" );
		o.println( "<tr><td width=25%>Aq" );
		DumpInSign( o, 10 );
		o.println( "</td><td colspan=2 rowspan=2 width=50%><h3>" + DivisionName() + "</h3>" );

		// Dump everything in the center
		// List points
		o.println( "<table cellpadding=0 cellspacing=1>" );
		int i;
		for (i = 0; i < amath_const.MAX_POINTS; i++) {
			int j = m_PGrade[ i ];
			o.println( "<tr><td>" );
			o.println( m_aPointAbbr[ j ] + ": " + m_Points[ j ].Fmt( m_nPrecMult, m_nDivision ) );
			o.println( "</td><td>" );
			m_Points[ j ].SetNakDiv( m_nDivision );
			o.println( m_Points[ j ].m_nak.Fmt() );
			o.println( "</td><td>" );
			if (j == amath_const.PT_LAGNA || j >= amath_const.MAX_PLANETS) { // Skip Lagna, Rahu and Ketu
				o.println( "&nbsp;" );
			}
			else {
				o.println( m_Points[ j ].GetPositionString( m_nDivision ) + " " );
				o.println( m_Points[ j ].GetOwnerRelString( m_Points[ m_Points[ j ].GetOwner( m_nDivision ) ], m_nDivision ) );
			} // for all divisions
			o.println( "</td>" );
			o.println( "</tr>" );
		} // for all points
		o.println( "</table>" );

		o.println( "</td>" );
		o.println( "<td width=25%>Ca" );
		DumpInSign( o, 3 );
		o.println( "</td></tr>" );
		o.println( "<tr><td width=25%>Cp" );
		DumpInSign( o, 9 );
		o.println( "</td><td width=25%>Le" );
		DumpInSign( o, 4 );
		o.println( "</td></tr>" );
		o.println( "<tr><td width=25%>Sa" );
		DumpInSign( o, 8 );
		o.println( "</td><td width=25%>Sc" );
		DumpInSign( o, 7 );
		o.println( "</td><td width=25%>Li" );
		DumpInSign( o, 6 );
		o.println( "</td><td width=25%>Vi" );
		DumpInSign( o, 5 );
		o.println( "</td></tr>" );
		o.println( "</table></p>" );

	} // DumpDivisionSouthern()

	// Helper function to display a single planet
	protected void DumpSlot( PrintStream o, int x, int y, String s, String align, String valign )
	{
		// Interpret negative as offset from left/bottom edge
		// FIXME use constants for width and height
		if (x<0) x += 800;
		if (y<0) y += 800;
		// Adjust position based on selected alignment
		if (align.equalsIgnoreCase("center")) x -= (m_boxWidth/2);
		else if (align.equalsIgnoreCase("right")) x -= m_boxWidth;
		if (valign.equalsIgnoreCase("middle")) y -= (m_boxHeight/2);
		else if (valign.equalsIgnoreCase("bottom")) y -= m_boxHeight;
		o.println( "<div class=\"slot\" style=\"left:" + x + "px;top:" + y + "px;width:" + m_boxWidth + "px;height:" + m_boxHeight + "px;text-align:" + align + ";vertical-align:" + valign + ";\">" + s + "</div>" );
		//System.out.println( "Sending " + x + "," + y + " " + m_boxWidth + "x" + m_boxHeight + " a=" + align + " va=" + valign + ": " + s );
	} // DumpSlot()

	// Helper function to display a single planet using svg
	// align uses left/center/right, valign top/middle/bottom
	protected void DumpSlotSvg( PrintStream o, int x, int y, String s )
	{
		//// Interpret negative as offset from left/bottom edge
		//if (x<0) x += m_chartWidthHtml;
		//if (y<0) y += m_chartHeightHtml;
		//// Adjust position based on selected alignment
		//if (align.equalsIgnoreCase("center")) x -= (m_boxWidth/2);
		//else if (align.equalsIgnoreCase("right")) x -= m_boxWidth;
		//if (valign.equalsIgnoreCase("middle")) y -= (m_boxHeight/2);
		//else if (valign.equalsIgnoreCase("bottom")) y -= m_boxHeight;
		// dx / dy seem to be additional relative offsets - for now just use x and y
		o.println( "<text x=\"" + x + "\" y=\"" + y + "\">" + s + "</text>" );
		//System.out.println( "Sending " + x + "," + y + " " + m_boxWidth + "x" + m_boxHeight + " a=" + align + " va=" + valign + ": " + s );
	} // DumpSlotSvg()

	// Get x delta for specified x offset with alignment
	protected int GetXShift( int x, String align )
	{
		int delta = 0;
		if (x < 0) delta += m_chartWidthHtml;
		if (align.equalsIgnoreCase("center")) delta -= (m_boxWidth/2);
		else if (align.equalsIgnoreCase("right")) delta -= m_boxWidth;
		return delta;
	}

	// Get y delta for specified y offset with vertical alignment
	protected int GetYShift( int y, String valign )
	{
		int delta = 0;
		if (y < 0) delta += m_chartHeightHtml;
		if (valign.equalsIgnoreCase("middle")) delta -= (m_boxHeight/2);
		else if (valign.equalsIgnoreCase("bottom")) delta -= m_boxHeight;
		return delta;
	}

	// Dump a single division (m_nDivision) in northern style
	protected void DumpDivisionNorthern( PrintStream o ) {

		// Northern style chart
		o.println( "<h3>" + DivisionName() + "</h3>" );
		o.println( "<div id=\"dc" + m_nDivision + "\" class=\"chart\">" );
		o.println( "<img src=\"" + m_northBG + "\">" );

		int nStartHouse;
		if (m_nDivision == APoint.MOON_VARGA) {
			nStartHouse = m_Points[ amath_const.PT_MOON ].GetSign( m_nDivision );
		} // Candra
		else {
			nStartHouse = m_Points[ amath_const.PT_LAGNA ].GetSign( m_nDivision );
		} // All others

		int nHouse;
		int i;
		for (nHouse = 0; nHouse < 12; nHouse++)
		{
			// Collect indices of planets to display
			ArrayList a = new ArrayList();
			String[] aVector;
			for (i = 0; i < amath_const.MAX_POINTS; i++) {
				int j = m_PGrade[ i ];
				if (m_Points[ j ].GetSign( m_nDivision ) != ((nHouse + nStartHouse) % 12)) continue;
				a.add( j );
			}
			if (a.size() == 0) continue;
			/***** Test full house
			if (a.size() == 0)
			{
				// Test 9 points
				a.add( m_PGrade[0] );
				a.add( m_PGrade[1] );
				a.add( m_PGrade[2] );
				a.add( m_PGrade[3] );
				a.add( m_PGrade[4] );
				a.add( m_PGrade[5] );
				a.add( m_PGrade[6] );
				a.add( m_PGrade[7] );
				a.add( m_PGrade[8] );
			}
			******* end full house test ****/

			if (a.size() >= 6)
			{
				aVector = NVECTORS[nHouse][1].split(";");
			}
			else
			{
				aVector = NVECTORS[nHouse][0].split(";");
			}

			if (aVector.length == 0)
			{
				o.println( "<p>House " + nHouse + " vector empty</p>" );
				continue;
			}
			// aVector has one or more elements containing
			// x,y,xinc,yinc,count,align,valign
			// a contains indices into m_Points[]
			// Work through elements
			int curElement = 0;
			String[] aVList;
			int vIndex = -1;
			int vCount = 0;
			int vX = 0;
			int vY = 0;
			int vXInc = 0;
			int vYInc = 0;
			String align = "";
			String valign = "";
			for (i = 0; i < a.size(); i++)
			{
				if (vIndex < 0)
				{
					aVList = aVector[curElement].split(",");
					vCount = Integer.parseInt(aVList[4]);
					vIndex = 0;
					vX = Integer.parseInt(aVList[0]);
					vY = Integer.parseInt(aVList[1]);
					vXInc = Integer.parseInt(aVList[2]);
					vYInc = Integer.parseInt(aVList[3]);
					align = aVList[5];
					valign = aVList[6];
				}
				int j = Integer.parseInt( a.get(i).toString() );
				DumpSlot( o, vX, vY, m_aPointAbbr[ j ] + ": " + m_Points[ j ].Fmt( m_nPrecMult, m_nDivision ), align, valign );
				vX += vXInc;
				vY += vYInc;
				vIndex++;
				if (vIndex >= vCount)
				{
					curElement++;
					vIndex = -1;
					if (curElement >= aVector.length) break;
				}
			}
		}

		// List points
		o.println( "<div id=\"dd" + m_nDivision + "\" class=\"ctable\">" );
		o.println( "<table cellpadding=0 cellspacing=1>" );
		// Cells are:
		// 1. Name + longitude
		// 2. Naksatra + padam
		// 3. Exalt / Debil / occupation
		// 4. Aspects
		for (i = 0; i < amath_const.MAX_POINTS; i++) {
			int j = m_PGrade[ i ];
			o.println( "<tr><td>" );
			o.println( m_aPointAbbr[ j ] + ": " + m_Points[ j ].Fmt( m_nPrecMult, m_nDivision ) );
			o.println( "</td><td>" );
			m_Points[ j ].SetNakDiv( m_nDivision );
			o.println( m_Points[ j ].m_nak.Fmt() );
			o.println( "</td><td>" );
			if (j == amath_const.PT_LAGNA || j >= amath_const.MAX_PLANETS) { // Skip Lagna, Rahu and Ketu
				o.println( "&nbsp;" );
			}
			else {
				o.println( m_Points[ j ].GetPositionString( m_nDivision ) + " " );
				o.println( m_Points[ j ].GetOwnerRelString( m_Points[ m_Points[ j ].GetOwner( m_nDivision ) ], m_nDivision ) );
			} // for all divisions
			o.println( "</td>" );
			o.println( "<td>&nbsp;" );
			for (int aspectGrade = 4; aspectGrade >= 1; aspectGrade--)
			{
				int aspectsDisplayed = 0;
				String aspectGradeStr = "1/4";
				if (aspectGrade == 2) aspectGradeStr = "1/2";
				else if (aspectGrade == 3) aspectGradeStr = "3/4";
				else if (aspectGrade == 4) aspectGradeStr = "full";
				for (int k = 0; k < amath_const.MAX_PLANETS; k++)
				{
					if (m_Points[j].m_Aspects[k] == aspectGrade)
					{
						if (aspectsDisplayed == 0)
						{
							o.print( "  [" + aspectGradeStr + "]-> " + m_aPointAbbr[k] );
						}
						else
						{
							o.print( ", " + m_aPointAbbr[k] );
						}
						aspectsDisplayed++;
					}
				}
			}
			o.println( "</td>" );
			o.println( "</tr>" );
			o.println( "<tr><td colspan=\"4\" style=\"font-size:8pt;\">" + m_Points[j].GetSymbolic( m_nDivision, 1, m_am.GetTithi(), m_am.GetPaksa() )  + "</td></tr>" );
		} // for all points
		o.println( "</table>" );
		o.println( "</div>" );

	} // DumpDivisionNorthern()

	// Dump a single division (m_nDivision) in northern style using HTML 5 inline svg
	protected void DumpDivisionNorthernSvg( PrintStream o ) {

		// Northern style chart
		o.println( "<h3>" + DivisionName() + "</h3>" );
		// Drop the class
		o.println( "<div id=\"dc" + m_nDivision + "\">" );
		o.println( "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" height=\"" + m_chartHeightHtml + "\" width=\"" + m_chartWidthHtml + "\">" );
		int margin = 5;
		int yTop = margin;
		int yBottom = m_chartHeightHtml - margin;
		int yMid = yBottom / 2;
		int xLeft = margin;
		int xRight = m_chartWidthHtml - margin;
		int xMid = xRight / 2;
		o.println( "<polygon points=\"" + xLeft + "," + yTop + " " + xRight + "," + yTop + " " + xRight + "," + yBottom + " " + xLeft + "," + yBottom + "\" style=\"fill:none;stroke:blue;stroke-width:4;\"/>" );
		o.println( "<polygon points=\"" + xMid + "," + yTop + " " + xRight + "," + yMid + " " + xMid + "," + yBottom + " " + xLeft + "," + yMid + "\" style=\"fill:none;stroke:blue;stroke-width:3\"/>" );
		o.println( "<line x1=\"" + xLeft + "\" y1=\"" + yTop + "\" x2=\"" + xRight + "\" y2=\"" + yBottom + "\" style=\"stroke:rgb(20,10,245);stroke-width:2;\"/>" );
		o.println( "<line x1=\"" + xRight + "\" y1=\"" + yTop + "\" x2=\"" + xLeft + "\" y2=\"" + yBottom + "\" style=\"stroke:rgb(20,10,245);stroke-width:2;\"/>" );

		// Reset house aspect path counts used to keep track of offset translation for aspect paths
		ResetShiftCounts();

		// Keep track of html to dump after we close svg figure
		String sEnd = "";

		// Lagna sign, origin:0
		int nStartHouse;
		if (m_nDivision == APoint.MOON_VARGA) {
			nStartHouse = m_Points[ amath_const.PT_MOON ].GetSign( m_nDivision );
		} // Candra
		else {
			nStartHouse = m_Points[ amath_const.PT_LAGNA ].GetSign( m_nDivision );
		} // All others

		int nHouse;
		int i;

		// Determine offsets and alignments of every point first
		// so we can draw point-to-point aspect lines
		// Format: ptindex,x,y
		ArrayList aPtPlacement = new ArrayList();
		// Must handle all points in m_Points[] and map them to aPtPlacement values
		ArrayList aPtToPlacement = new ArrayList();
		for (i = 0; i < amath_const.MAX_POINTS; i++) aPtToPlacement.add( -1 );
		for (nHouse = 0; nHouse < 12; nHouse++)
		{
			// Collect indices of planets to display
			ArrayList a = new ArrayList();
			String[] aVector;
			for (i = 0; i < amath_const.MAX_POINTS; i++) {
				int j = m_PGrade[ i ];
				if (m_Points[ j ].GetSign( m_nDivision ) != ((nHouse + nStartHouse) % 12)) continue;
				a.add( j );
			}
			/****
			// One-time debug code: fill out remaining values
			while (a.size() < 5) a.add( 0 );
			**/
			if (a.size() == 0) continue;

			if (a.size() >= 6)
			{
				aVector = NVECTORS[nHouse][1].split(";");
			}
			else
			{
				aVector = NVECTORS[nHouse][0].split(";");
			}

			// This would be a program data error
			if (aVector.length == 0)
			{
				sEnd += ( "<p>ERROR: House " + nHouse + " vector empty</p>" );
				continue;
			}

			// aVector has one or more elements containing
			// x,y,xinc,yinc,count,align,valign
			// Translate these into current chart size
			// a contains indices into m_Points[]
			// Work through elements
			int curElement = 0;
			String[] aVList;
			int vIndex = -1;
			int vCount = 0;
			int vX = 0;
			int vY = 0;
			int vXInc = 0;
			int vYInc = 0;
			String align = "";
			String valign = "";

			for (i = 0; i < a.size(); i++)
			{
				if (vIndex < 0)
				{
					aVList = aVector[curElement].split(",");
					vCount = Integer.parseInt(aVList[4]);
					vIndex = 0;
					vX = Integer.parseInt(aVList[0]) * m_chartWidthHtml / 800;
					vY = Integer.parseInt(aVList[1]) * m_chartHeightHtml / 800;
					vXInc = Integer.parseInt(aVList[2]) * m_chartWidthHtml / 800;
					vYInc = Integer.parseInt(aVList[3]) * m_chartHeightHtml / 800;
					align = aVList[5];
					valign = aVList[6];
				}
				int j = Integer.parseInt( a.get(i).toString() );
				aPtToPlacement.set( j, aPtPlacement.size() );
				aPtPlacement.add( j + "," + (vX + GetXShift( vX,  align )) + "," + (vY + GetYShift( vY, valign ))  );
				vX += vXInc;
				vY += vYInc;
				vIndex++;
				if (vIndex >= vCount)
				{
					curElement++;
					vIndex = -1;
					if (curElement >= aVector.length) break;
				}
			} // for all points i in this house
		} // for all houses nHouse

		// Open g container for points in this house
		o.println( "<g font-size=\"12\" font=\"sans-serif\" fill=\"black\" stroke=\"none\">" );

		// Draw aspects
		// FIXME Use a matrix to avoid overlapping origins / destinations
		int xOffset = 0;
		int yOffset = 0;
		for (i = 0; i < amath_const.MAX_POINTS; i++)
		{
			int j = m_PGrade[ i ];
			if (j >= amath_const.MAX_PLANETS || j == amath_const.PT_LAGNA) continue;

			// 1/4, 2/4, 3/4, 4/4
			for (int aspectGrade = 1; aspectGrade <= 4; aspectGrade++)
			{
				int aspectsDisplayed = 0;
				// 1, 2, 3, 4
				int strokeWidth = aspectGrade;
				if (strokeWidth < 1) strokeWidth = 1;
				String strokeColor = "rgb(15,189,62)";
				if (aspectGrade == 1) strokeColor = "rgb(201,201,201)";
				else if (aspectGrade == 2) strokeColor = "rgb(0,21,71)";
				else if (aspectGrade == 3) strokeColor = "rgb(120,16,75)";
				for (int k = 0; k < amath_const.MAX_PLANETS; k++)
				{
					if (m_Points[j].m_Aspects[k] == aspectGrade)
					{
						// Get origin and destination points
						int aspectOrigin = Integer.parseInt( aPtToPlacement.get(j).toString() );
						int aspectDestination = Integer.parseInt( aPtToPlacement.get(k).toString() );
						if (aspectOrigin < 0 || aspectDestination < 0)
						{
							sEnd += ("<p>Failed to get origin and destination for " + j + "," + k + "</p>");
							continue;
						}
						String[] aVList;
						//sEnd += ("<p>Dbg: j=" + j + ",k=" + k + ", org=" + aspectOrigin + ", dest=" + aspectDestination + ", ptpl=" + aPtPlacement.get(aspectOrigin).toString() + ", ptpl-d=" + aPtPlacement.get(aspectDestination).toString() + "</p>");
						int aspectOriginX, aspectOriginY, aspectDestX, aspectDestY;
						aVList = aPtPlacement.get(aspectOrigin).toString().split(",");
						aspectOriginX = Integer.parseInt(aVList[1]) + xOffset + m_boxWidth;
						aspectOriginY = Integer.parseInt(aVList[2]) + yOffset - m_boxHeight / 4;
						aVList = aPtPlacement.get(aspectDestination).toString().split(",");
						aspectDestX = Integer.parseInt(aVList[1]) + xOffset - m_boxWidth / 12;
						aspectDestY = Integer.parseInt(aVList[2]) + yOffset - m_boxHeight / 4;
						// Determine slope and offset to apply
						int xDelta = 0;
						int yDelta = 0;
						int slopeX = aspectDestX - aspectOriginX;
						int slopeY = aspectDestY - aspectOriginY;
						if (slopeY == 0 || Math.abs(slopeX / slopeY) < 5)
						{
							//xDelta = strokeWidth + 1;
						}
						if (slopeX == 0 || Math.abs(slopeY / slopeX) < 5)
						{
							//yDelta = strokeWidth + 1;
						}
						aspectOriginX += xDelta;
						aspectOriginY += yDelta;
						aspectDestX += xDelta;
						aspectDestY += yDelta;
						xOffset += xDelta;
						yOffset += yDelta;

						// Get from m_aHouseAspectPaths[origin_house-1][aspect-3]
						String pathData;
						String planetId = m_Points[j].GetName() + "-" + m_Points[k].GetName();
						pathData = "";
						int aspectIndex = m_Points[j].m_AspectSigns[k] - 3;
						int houseIndex = (m_Points[j].GetSign(m_nDivision) + 12 - nStartHouse) % 12;
						if (aspectIndex >= 0 && aspectIndex <= 7)
						{
							// Handle translation and scaling (was )
							pathData = RenderAspectSvg( houseIndex, aspectIndex, planetId, aspectOriginX, aspectOriginY, aspectDestX, aspectDestY, strokeColor, strokeWidth );
							//sEnd += ("<p>dbg: j=" + j + ", k=" + k + " " + planetId + ", aspectIndex=" + aspectIndex + ", houseIndex=" + houseIndex + ": " + m_aHouseAspectPaths[houseIndex][aspectIndex] + "</p>");
						}
						else
						{
							sEnd += ("<p>dbg (no hit): j=" + j + ", k=" + k + ", aspectIndex=" + aspectIndex + ", houseIndex=" + houseIndex + "</p>");
						}
						o.println( pathData );
						aspectsDisplayed++;
					}
				} // for all points (inner)
			} // for all aspect grades

		} // for all points (outer)

		// Format: ptindex,x,y,align,valign
		for (i = 0; i < aPtPlacement.size(); i++)
		{
			String[] aVList;
			aVList = aPtPlacement.get(i).toString().split(",");
			//if (aVList.size()==0) continue; // FIXME report error
			int j = Integer.parseInt( aVList[0] );
			DumpSlotSvg( o, Integer.parseInt(aVList[1]), Integer.parseInt(aVList[2]),
				m_aPointAbbr[ j ] + ": " + m_Points[ j ].Fmt( m_nPrecMult, m_nDivision ) );
		}

		// Close g container
		o.println( "</g>" );

		// Close svg
		o.println( "</svg>" );

		// Dump final html we've been saving and close out division
		o.println( sEnd + "</div>" );

		// List points
		o.println( "<div id=\"dd" + m_nDivision + "\" class=\"ctable\">" );
		o.println( "<table cellpadding=0 cellspacing=1>" );
		// Cells are:
		// 1. Name + longitude
		// 2. Naksatra + padam
		// 3. Exalt / Debil / occupation
		// 4. Aspects
		for (i = 0; i < amath_const.MAX_POINTS; i++) {
			int j = m_PGrade[ i ];
			o.println( "<tr><td>" );
			o.println( m_aPointAbbr[ j ] + ": " + m_Points[ j ].Fmt( m_nPrecMult, m_nDivision ) );
			o.println( "</td><td>" );
			m_Points[ j ].SetNakDiv( m_nDivision );
			o.println( m_Points[ j ].m_nak.Fmt() );
			o.println( "</td><td>" );
			if (j == amath_const.PT_LAGNA || j >= amath_const.MAX_PLANETS) { // Skip Lagna, Rahu and Ketu
				o.println( "&nbsp;" );
			}
			else {
				o.println( m_Points[ j ].GetPositionString( m_nDivision ) + " " );
				o.println( m_Points[ j ].GetOwnerRelString( m_Points[ m_Points[ j ].GetOwner( m_nDivision ) ], m_nDivision ) );
			} // for all divisions
			o.println( "</td>" );
			o.println( "<td>&nbsp;" );
			for (int aspectGrade = 4; aspectGrade >= 1; aspectGrade--)
			{
				int aspectsDisplayed = 0;
				String aspectGradeStr = "1/4";
				if (aspectGrade == 2) aspectGradeStr = "1/2";
				else if (aspectGrade == 3) aspectGradeStr = "3/4";
				else if (aspectGrade == 4) aspectGradeStr = "full";
				for (int k = 0; k < amath_const.MAX_PLANETS; k++)
				{
					if (m_Points[j].m_Aspects[k] == aspectGrade)
					{
						if (aspectsDisplayed == 0)
						{
							o.print( "  [" + aspectGradeStr + "]-> " + m_aPointAbbr[k] );
						}
						else
						{
							o.print( ", " + m_aPointAbbr[k] );
						}
						aspectsDisplayed++;
					}
				}
			}
			o.println( "</td>" );
			o.println( "</tr>" );
			o.println( "<tr><td colspan=\"4\" style=\"font-size:8pt;\">" + m_Points[j].GetSymbolic( m_nDivision, 1, m_am.GetTithi(), m_am.GetPaksa() )  + "</td></tr>" );
		} // for all points

		// Close table of point info
		o.println( "</table>" );

		// Close out div
		o.println( "</div>" );

	} // DumpDivisionNorthernSvg()

	// Dump a single division (m_nDivision) along with longitudes, aspects, etc.
	protected void DumpDivision( PrintStream o, int format ) {
		for (int i = amath_const.PT_FIRST; i <= amath_const.PT_LAST; i++)
		{
			m_Points[i].RecalcAspects( m_nDivision, m_Points );
		}
		switch (format)
		{
			case 0:
				// Not yet supported
			case 1:
				// Southern style using tables
				DumpDivisionSouthern( o );
				break;
			default:
				o.println( "<p>Unknown format: " + format + "</p>" );
				// Fall through to northern style
			case 2:
				DumpDivisionNorthern( o );
				break;
			case 3:
				DumpDivisionNorthernSvg( o );
				break;
		}
	} // DumpDivision()

	// Dump text information to print stream or text stream (to be used for browser output)
	// Format is 0 for native print, 1 for southern table-based html, 2 for northern div-based html,
	// 3 for html5 inline svg
        // withNavamsa now means "use m_htmlDivs to determine which divisions to print"
        protected void DumpText( PrintStream o, int format, int maxDivisions, boolean withNavamsa ) {

		if (format == 2 || format == 3)
		{
                        if (format == 3) o.println( "<!DOCTYPE html>" );
                        o.println( "<html><head><title>" + m_Text + "</title>" );
			// Style preamble
			o.println( NORTHERN_HTML_CSS );
                        o.println( "</head><body>" );
		}

		int oldChartWidth = m_chartWidthHtml;
		int oldChartHeight = m_chartHeightHtml;

		if (format == 3)
		{
			//m_chartWidthHtml = 600;
			//m_chartHeightHtml = 600;
		}

                o.println( "<h1>Java Jyotish " + m_CoreVersion + "</h1>" );
		o.println( "<h3>" + m_Text + "</h3>" );

		String szDST = "";
		if (m_nDST != 0) szDST = " DST:" + m_nDST;
		o.println( "<p>" + m_Time + " " + m_Date + " " + m_TZ + szDST + "<br>" );
		o.println( m_Lat + " " + m_Long + "<br>" );
		/**if (m_am.m_dJD != 0.0) {
			o.println( "JD: " + String.valueOf( (new Double( m_am.m_dJD )).longValue() ) + "<br>" );
		}**/
		o.println( m_am.TithiName() + "</p>" );

		// First Rasi and Candra
		int nOldDiv = m_nDivision;
		m_nDivision = 1;
		DumpDivision( o, format );
		if (maxDivisions >= 2)
		{
			m_nDivision = APoint.MOON_VARGA;
			DumpDivision( o, format );
		}

		// Dump all other divisions
		int nDiv;
		for (nDiv = 1; nDiv <= maxDivisions-2; nDiv++) {
			m_nDivision = m_DivisionCycle[ nDiv ];
			DumpDivision( o, format );
		} // for all divisions

                // If not dumping all divisions but withNavamsa specified, go through
                // m_htmlDivs
                if (maxDivisions < 4 && withNavamsa)
		{
                    if (m_htmlDivs.contains(",4,")) { m_nDivision = 4; DumpDivision( o, format ); }
                    if (m_htmlDivs.contains(",7,")) { m_nDivision = 7; DumpDivision( o, format ); }
                    if (m_htmlDivs.contains(",9,")) { m_nDivision = 9; DumpDivision( o, format ); }
                    if (m_htmlDivs.contains(",10,")) { m_nDivision = 10; DumpDivision( o, format ); }
                    if (m_htmlDivs.contains(",12,")) { m_nDivision = 12; DumpDivision( o, format ); }
                    if (m_htmlDivs.contains(",16,")) { m_nDivision = 16; DumpDivision( o, format ); }
                    if (m_htmlDivs.contains(",20,")) { m_nDivision = 20; DumpDivision( o, format ); }
                    if (m_htmlDivs.contains(",24,")) { m_nDivision = 24; DumpDivision( o, format ); }
                    if (m_htmlDivs.contains(",27,")) { m_nDivision = 27; DumpDivision( o, format ); }
                    if (m_htmlDivs.contains(",30,")) { m_nDivision = 30; DumpDivision( o, format ); }
                    if (m_htmlDivs.contains(",40,")) { m_nDivision = 40; DumpDivision( o, format ); }
                    if (m_htmlDivs.contains(",45,")) { m_nDivision = 45; DumpDivision( o, format ); }
		}

		if (m_nDivision != nOldDiv)
		{
			for (int i = amath_const.PT_FIRST; i <= amath_const.PT_LAST; i++)
			{
				m_Points[i].RecalcAspects( nOldDiv, m_Points );
			}
		}

		m_nDivision = nOldDiv;

		// Dasha information
		//o.println( "<hr>" );

		// Sad-bala
		//o.println( "<hr>" );

                if ((format == 2 || format == 3))
		{
			o.println( "</body></html>" );
		}

		// Restore original html chart size values
		m_chartWidthHtml = oldChartWidth;
		m_chartHeightHtml = oldChartHeight;

                // Caller would need to redraw panels
                //MyRepaint();

	} // DumpText

	// Post to URL
	public void Post()
	{
		// Try connecting
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		PrintStream o = new PrintStream(bo); // DataOutputStream uses writeChars

		debugOut( "Creating output..." );
		// Dump only rasi chart and navamsa, or use MAX_DIVISIONS for third parameter for all
		DumpText( o, 0, 1, true );
		// This posts everything
		o.flush();
		o.close();

		debugOut( "Sending output to browser..." );
		browserOut( bo.toString() );

	} // Post()

	// Render southern style HTML using tables
	public void RenderHtmlSouthern()
	{
		// Try connecting
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		PrintStream o = new PrintStream(bo); // DataOutputStream uses writeChars

		debugOut( "Creating output..." );
		// Dump only rasi chart and navamsa, or use MAX_DIVISIONS for third parameter for all
		DumpText( o, 1, 1, true );
		// This posts everything
		o.flush();
		o.close();

		debugOut( "Sending output to browser..." );
		browserOut( bo.toString() );

	} // RenderHtmlSouthern()

	// Render northern style HTML using div positioning
	public void RenderHtml()
	{
		// Try connecting
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		PrintStream o = new PrintStream(bo); // DataOutputStream uses writeChars

		debugOut( "Creating output..." );
		// Dump only rasi chart and navamsa, or MAX_DIVISIONS for all
		DumpText( o, 2, 1, true );
		// This posts everything
		o.flush();
		o.close();

		debugOut( "Sending output to browser..." );
		browserOut( bo.toString() );

	} // RenderHtml()

	// Render northern style HTML using HTML5 inline svg
	public void RenderHtml5()
	{
		// Try connecting
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		PrintStream o = new PrintStream(bo); // DataOutputStream uses writeChars

		debugOut( "Creating output..." );
		// Dump only rasi chart and navamsa, or MAX_DIVISIONS for all
		DumpText( o, 3, 1, true );
		// This posts everything
		o.flush();
		o.close();

		debugOut( "Sending output to browser..." );
		browserOut( bo.toString() );

	} // RenderHtml()

        // Display general strength information
        public String ShowGeneralStrength() {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            PrintStream o = new PrintStream(bo); // DataOutputStream uses writeChars

            debugOut( "Creating output..." );
            DumpGeneralStrength( o );

            // This posts everything
            o.flush();
            o.close();

            debugOut( "Sending output to browser..." );
            return bo.toString();

        } // ShowGeneralStrength()

        public String ShowVimsottariDasa()
        {
                // Do we need to calculate?
                if (m_bDasaDirty) RecalcDasa();

            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            PrintStream o = new PrintStream(bo); // DataOutputStream uses writeChars

            debugOut( "Creating output..." );
            DumpVimsottariDasa( o, 2 );

            // This posts everything
            o.flush();
            o.close();

            debugOut( "Sending output to browser..." );
            return bo.toString();

        }

        protected void DumpVimsottariDasa( PrintStream o, int level )
        {

            if (m_pDasa == null) return;

            DasaSet dasa = m_pDasa;
            // For level 0 show starting date (birth)
            //g.drawString( " - " + dasaRoot.GetFmtBirth(), nHPos2, nVPos );

            o.println("<div id=\"vimsottari\"><h3>Vimsottari dasa starting from " + m_pDasa.GetRuler() + "</h3>" );
            o.println( "<table border=\"0\" style='font-family: Arial, \"Liberation Sans\", Helvetica, Sans; font-size:8pt;'><tr><th colspan=\"2\">Mahadasa start</th><th colspan=\"9\">Bhukti and antardasa end</th></tr>" );
            for (dasa = m_pDasa.GetFirst(); dasa != null; dasa = m_pDasa.GetNext())
            {
                ShowDasaSub( o, dasa, 1, level, dasa.GetRulerAbbr() + "-" );
            }
            o.println( "</table>" );
            o.println( "</div>" );
        } // ShowDasa()

        // Recursive subroutine for dasa display.
        // Returns new vertical position.
        protected int ShowDasaSub( PrintStream o, DasaSet dasaRoot, int recursionLevel, int maxLevel, String prefix )
        {

                DasaSet dasa;
                DasaSet antar[] = {null,null,null, null,null,null, null,null,null};
                int nLastVPos;
                int nCount = 0;
                int nActual = 0;
                int antarActual[] = {0,0,0, 0,0,0, 0,0,0};
                int nAntar;
                int nCol;

                // Three passes are needed: 1. count actual periods (may be less than 9) 2. show antardasas 3. show bhukti columns
                for (dasa = dasaRoot.GetFirst(), nAntar = 0; dasa != null; dasa = dasaRoot.GetNext(), nAntar++)
                {
                    for (antar[nAntar] = dasa.GetFirst(); antar[nAntar] != null; antar[nAntar] = dasa.GetNext()) antarActual[nAntar]++;
                    nActual++;
                }
                // Layout is
                // root ruler | root start | bh1-ant1 | bh2-ant1 | bh3-ant1 ...
                //                         | bh1-ant2 | bh2-ant2 | bh3-ant2 ...
                // ...
                //                         | bh1-ant9 | bh2-ant9 | bh3-ant9 ...
                //                         | bh1      | bh2      | bh3
                //o.print( dasaRoot.GetRulerAbbr() + " start " + dasaRoot.GetFmtStart() + " " );
                // Perform vertical recursion of bhukti header + antardasas
                for (nCount = 0; nCount < 10; nCount++)
                {
                    if (nCount == 0)
                    {
                        o.print( "<tr><td rowspan=\"11\" valign=\"top\" style=\"font-size:10pt;\"><b>" + dasaRoot.GetRuler() + "</b></td><td rowspan=\"11\" valign=\"top\"><b>" );
                        if (dasaRoot.StartsBeforeBirth())
                        {
                            o.print( "-" );
                        }
                        else
                        {
                            o.print( dasaRoot.GetFmtStart() );
                        }
                        o.print( "</b></td>" );
                        if (nActual < 9) o.print( "<td rowspan=\"11\" colspan=\"" + (9 - nActual) + "\">&nbsp;</td>" );
                    } // first pass
                    else o.print( "<tr>" );
                    for (dasa = dasaRoot.GetFirst(), nCol=0; dasa != null; dasa = dasaRoot.GetNext(), nCol++) {
                        if (nCount == 0)
                        {
                            o.print( "<td><b>" + dasa.GetRuler() + "</b></td>" );
                            continue;
                        }
                        else if (nCount == 1)
                        {
                            antar[nCol] = dasa.GetFirst();
                        }
                        if (antar[nCol] == null) continue;
                        o.print( "<td" );
                        o.print( " id=\"" + prefix + dasa.GetRulerAbbr() + "-" + antar[nCol].GetRulerAbbr() + "\"" );
                        if (antarActual[nCol] < 9 && nCount == 1) o.print( " rowspan=\"" + (9 - antarActual[nCol] + 1) + "\" valign=\"bottom\"" );
                        o.print( ">" );
                        o.print( antar[nCol].GetRulerAbbr() + " " + antar[nCol].GetFmtEndDate() );
                        antar[nCol] = dasa.GetNext();
                        o.print( "</td>" );
                    } // columns in antardasa rows
                    o.println( "</tr>" );
                } // antardasa rows
                // Perform horizontal list of bhuktis
                for (dasa = dasaRoot.GetFirst(); dasa != null; dasa = dasaRoot.GetNext(), nCount++) {
                        // Visible: GetStart(), GetEnd(), GetYears(),
                        //	GetFmtStart(), GetFmtEnd(), GetRuler(),
                        //	GetRulerAbbr(), GetFmtBirth()
                        //
                        if (nCount == 0)
                        {
                            o.print( "<tr>" );
                        } // First pass1
                        o.print("<td id=\"" + prefix + dasa.GetRulerAbbr() + "\"><b>");
                        if (dasa.EndsBeforeBirth()) o.print("-");
                        else o.print( "ends " + dasa.GetFmtEndAge() );
                        //else o.print( dasa.GetRulerAbbr() + " " + dasa.GetFmtEndDate() );
                        o.print("</b></td>");

                        // For debugging:
                        //g.drawRect( nHPos, nLastVPos - nVSpace + 2, nHPos2 - nHPos, nVSpace - 2 );
                        //g.drawRect( nHPos2, nLastVPos, 2, nVPos - nLastVPos );
                } // Level 0
                o.println("</tr>");

                return 0;

        } // ShowDasaSub()

        protected void DumpGeneralStrength( PrintStream o )
        {
            // Display
            int i;
            int nDivisions[] = {
                     1,  3,  4,  7,
                     9, 10, 12, 16,
                    20, 24,	27, 30,
                    40, 45, 60
            };
            int nMaxDivisions = MAX_DIVISIONS;
            int nDiv, nDivCount;

            o.println( "<div id=\"sadbala\"><h3>General strength</h3>\n" );
            o.println( "<table border=\"0\"><tr><td colspan=\"" + new Integer(nMaxDivisions+1).toString() + "\" align=\"center\">Planet, position, house owner relationship</td></tr>\n");
            o.print( "<tr><th>Planet</th>" );
            for (nDiv = DivIndex( m_nDivision ), nDivCount = 0; nDiv < nMaxDivisions; nDivCount++, nDiv++) {
                o.print( "<th>" + new Integer(m_DivisionCycle[nDiv]).toString() + "</th>" );
            } // for all divisions
            o.println( "</tr>" );

            for (i = 0; i < amath_const.MAX_POINTS; i++) {
                    int j = m_PGrade[ i ];
                    if (j == amath_const.PT_LAGNA || j >= amath_const.MAX_PLANETS) continue; // Skip Lagna, Rahu and Ketu
                    o.print( "<tr><td>" + m_aPointAbbr[j] + "</td>" );
                    for (nDiv = DivIndex( m_nDivision ), nDivCount = 0; nDiv < nMaxDivisions; nDivCount++, nDiv++) {
                            o.print( "<td>" );
                            o.print( m_Points[ j ].GetPositionString( m_DivisionCycle[nDiv] ) );
                            o.print( " " );
                            o.print( m_Points[ j ].GetOwnerRelString( m_Points[ m_Points[ j ].GetOwner( m_DivisionCycle[nDiv] ) ], m_DivisionCycle[nDiv] ) );
                            o.print( "</td>" );
                    } // for all divisions
                    o.println( "</tr>" );
            } // for all points
            o.println( "</table>" );
            o.println( "</div>" );
        } // DumpGeneralStrength

	// Recalculate dasa values
	protected void RecalcDasa()
	{
		m_bDasaDirty = false;

		// Dasas are defined recursively.
		// Each one has a start, ruler and end.
		// The top level (0) has a duration of
		// 120 or 108 years.
		// The time of birth is also relevant; it
		// may come before a sub-period or within it.
		double dMoon;
		double dRemnant;
		int nMoonNak;
		naksatra nak;

		// Calculate percentage remaining in naksatra
		dMoon = m_Points[ amath_const.PT_MOON ].GetLong();
		nak = new naksatra( dMoon );
		nMoonNak = nak.GetNum();
		// Convert birth time from UTC back to local time
		double dBirth = m_am.m_dJD + m_nDST / 24.0 + m_nTZ / 1440.0;
		dRemnant = (nMoonNak * 40.0 / 3.0 - dMoon) / (40.0 / 3.0);

		// Get end of mahadasa ruling at birth
		//g.drawString( "Remnant = " + new Double( dRemnant * 100.0 ).toString().substring( 0, 6 ) + "%", 10, 10 + nVSpace * 2 );
		System.out.println( "Remnant = " + new Double( dRemnant * 100.0 ).toString().substring(0,6) + "% days = " + nak.GetDaysRemaining() );
		System.out.println( "Moon = " + dMoon + " nak " + nMoonNak + " ruler pd = " + nak.GetRulerPeriod(120) );
		double dEndBirth = dBirth + nak.GetDaysRemaining();

		// Get start of mahadasa cycle - this is start of 120-year period
		double dStartMahadasa = dEndBirth - Math.round( nak.GetRulerPeriod( 120 ) * 365.25 );

                // Initialize to level 3 (antardasa)
		m_pDasa = new DasaSet( dBirth, dStartMahadasa,
			dStartMahadasa + Math.round( 365.25 * 120 ),
                        nak.GetRulerNum(), 0, 3 );

		System.out.println( "local birth jd = " + dBirth + " date: " + m_pDasa.FmtDateTest( dBirth ) + " date + 10d: " + m_pDasa.FmtDateTest( dBirth + 10.0 ) );


	} // RecalcDasa()

	// Build path to indexed image
	private String ImagePath( String szType, int nIndex )
	{
		String szRet;
		// FIXME Handle different image sets
		szRet = "images/" + szType;
		if (nIndex < 10) szRet += "0";
		szRet += nIndex;
		szRet += ".gif";
		return szRet;
	}

	public static String CurTime()
	{
		Date d;
		String szRet;

		d = new Date();
		int nHours = d.getHours();
		int nMinutes = d.getMinutes();
		int nSeconds = d.getSeconds();

		szRet = "";
		szRet += nHours;
		if (nHours < 10) szRet = "0" + szRet;
		if (nMinutes < 10) szRet = szRet + "0" + nMinutes;
		else szRet = szRet + nMinutes;
		if (nSeconds < 10) szRet = szRet + "0" + nSeconds;
		else szRet = szRet + nSeconds;

		return szRet;
	} // CurTime()

	public static String CurDate()
	{
		Date d;
		String szRet;

		d = new Date();
		int nYear = d.getYear();
		if (nYear < 1900) nYear += 1900;
		int nMonth = d.getMonth() + 1;
		int nDay = d.getDate();

		szRet = "";
		szRet += nYear;
		if (nMonth < 10) {
			szRet = szRet + "0" + nMonth;
		}
		else szRet = szRet + nMonth;
		if (nDay < 10) szRet = szRet + "0" + nDay;
		else szRet = szRet + nDay;

		return szRet;
	} // CurDate()

	// Parse date, time and timezone
	public void ParseDate()
	{
		// Date should be YYYYMMDD
		m_nYear = amath.atoi( m_Date.substring( 0, 4 ) );
		m_nMonth= amath.atoi( m_Date.substring( 4, 6 ) );
		m_nDay  = amath.atoi( m_Date.substring( 6, 8 ) );
		// Time should be HHMM[SS]
		m_nHour = amath.atoi( m_Time.substring( 0, 2 ) );
		m_nMinute = amath.atoi( m_Time.substring( 2, 4 ) );
		m_nSecond = 0;
		if (m_Time.length() == 6) {
			m_nSecond = amath.atoi( m_Time.substring( 4, 6 ) );
		}

		// Timezone should be [-]HHMM
		// Convert to signed minutes east of Greenwich
		int nHourLen = m_TZ.length() - 2;
		m_nTZ = 60 * amath.atoi( m_TZ.substring( 0, nHourLen ) );
		if (m_nTZ < 0) {
			m_nTZ -= amath.atoi( m_TZ.substring( nHourLen ) );
		} // Negative
		else {
			m_nTZ += amath.atoi( m_TZ.substring( nHourLen ) );
		} // Positive
	} // ParseDate()

	private boolean TimeDateValid()
	{
		if (m_Time == null || m_Date == null || m_TZ == null) return false;
		if (m_Time.length() != 4 && m_Time.length() != 6) return false;
		if (m_Date.length() != 8) return false;
		if (m_TZ.length() < 4) return false;
		if (m_Lat.length() < 5) return false;
		if (m_Long.length() < 5) return false;
		return true;
	} // TimeDateValid()

        public void Calculate()
	{
		double dLagna;

		debugOut( "Calculating lagna" );

		m_nCalculated = 0;
		m_CodedOutput = "";

		m_am.SetLocation( m_Lat, m_Long );
		m_am.SetTime( m_nYear, m_nMonth, m_nDay, m_nHour, m_nMinute, m_nSecond, m_nDST, m_nTZ );
		m_am.SetAyanamsa( m_Ayanamsa );
		m_am.m_c.ResetData();
		dLagna = m_am.Lagna();
		m_Points[ amath_const.PT_LAGNA ].SetLong( dLagna, amath_const.PT_LAGNA );

		debugOut( "Calculating sun" );

		// Calculate sun
		m_Points[ amath_const.PT_SUN ].SetLong( m_am.Sun(), amath_const.PT_SUN );
		m_Points[ amath_const.PT_SUN ].SetMotion( m_am.m_dMotion );

                debugOut( "Calculating moon and tithi" );

		// Calculate moon
		m_Points[ amath_const.PT_MOON ].SetLong( m_am.Moon(), amath_const.PT_MOON );
		m_Points[ amath_const.PT_MOON ].SetMotion( m_am.m_dMotion );
		//m_Points[ PT_RAHU ].SetLong( m_am.m_dRahu );
		//m_Points[ PT_KETU ].SetLong( m_am.m_dKetu );

		// Calculate tithi and paksa
		m_am.CalcTithi( m_Points[ amath_const.PT_SUN ].GetLong(), m_Points[ amath_const.PT_MOON ].GetLong() );

		debugOut( "Calculating others" );

		// Calculate other planets
		for (int i = amath_const.PT_BUDHA; i <= amath_const.PT_LAST; i++) {
			m_Points[ i ].SetLong( m_am.Planet( i ), i );
			m_Points[ i ].SetMotion( m_am.m_dMotion );
		} // Calculate planets

		debugOut( "Calculating offsets" );

		// Set house and moon offsets
		for (int i = amath_const.PT_FIRST; i <= amath_const.PT_LAST; i++) {
			m_Points[ i ].SetAsc( m_Points[ amath_const.PT_LAGNA ] );
			m_Points[ i ].SetMoon( m_Points[ amath_const.PT_MOON ] );
		} // for all points

		debugOut( "Grading points" );
		if (m_nDivision == APoint.MOON_VARGA) {
			GradePoints( amath_const.PT_MOON );
		} // Candra lagna
		else {
			GradePoints( amath_const.PT_LAGNA );
		} // Rasi

		// Get coded output for navamsa if doing rasi chart
		String sAddition = "";
		if (m_nDivision == 1)
		{
			debugOut( "Calculating navamsa" );
			sAddition += " ";
			for (int i = amath_const.PT_FIRST; i <= amath_const.PT_LAST; i++)
			{
				m_Points[i].RecalcAspects( 9, m_Points );
				sAddition += m_Points[i].GetSymbolic( 9, 1, m_am.GetTithi(), m_am.GetPaksa() );
				sAddition += " ";
			}
		}

		// Recalculate aspects
		debugOut( "Calculating aspects" );
		for (int i = amath_const.PT_FIRST; i <= amath_const.PT_LAST; i++)
		{
			m_Points[i].RecalcAspects( m_nDivision, m_Points );
			m_CodedOutput += m_Points[i].GetSymbolic( m_nDivision, 1, m_am.GetTithi(), m_am.GetPaksa() );
			m_CodedOutput += " ";
		}

		m_CodedOutput += sAddition;

		m_nCalculated = 1;

		debugOut( "Done." );

	} // Calculate()

	// Grade (sort) all points ascendingly by longitude relative to
	// specified point, either PT_LAGNA or PT_MOON
	private void GradePoints( int nOrigin )
	{
		int i, bDone, j;
		bDone = 0;
		// Normally lagna or candra
		double dOrigin = m_Points[ nOrigin ].GetLong();
		// Simple bubble sort
		while (bDone == 0) {
			bDone = 1;
			for (i = amath_const.PT_FIRST; i < amath_const.PT_LAST; i++) {
				if (m_Points[ m_PGrade[ i ] ].RelativeLong( dOrigin ) > m_Points[ m_PGrade[ i + 1 ] ].RelativeLong( dOrigin )) {
					j = m_PGrade[ i ];
					m_PGrade[ i ] = m_PGrade[ i + 1 ];
					m_PGrade[ i + 1 ] = j;
					bDone = 0;
				} // Out of order
			} // for all points
		} // not done
	} // GradePoints()


	// Javascript-accessible function - call this from Javascript
	// after setting time, date, lat, long, etc.
	public void Recalc()
	{
		if (TimeDateValid()) {
			// Parse time, date, timezone
			ParseDate();
			// Do one-time calculation
			Calculate();
		}
		else
		{
			debugOut( "Time/date not valid, no recalc: " + m_Time + ";" + m_Date + ";" + m_Lat + ";" + m_Long + ";" + m_TZ + ";" );
		}
	} // Recalc

	// Given a division, return index within m_DivisionCycle[]
	// return 0 if not found
	protected int DivIndex( int nDiv ) {
		int n;
		for (n = 0; n < MAX_DIVISIONS; n++) {
			if (nDiv == m_DivisionCycle[ n ]) return n;
		} // for all divisions
		return 0; // Default - start with Rasi
	} // DivIndex()

	// Given an origin:0 house index and origin:3 aspect, return the path contents shifted
	// and scaled appropriately. Updates shift counts
	public String GetHouseAspectPath( int nHouse, int nAspect )
	{
		// Temporarily we have unpopulated strings
		if (LayoutData.m_aHouseAspectPaths[nHouse][nAspect].isEmpty())
		{
			System.out.println( "<p>Error: empty " + nHouse + "," + nAspect + "</p>" );
			return "";
		}
		// Shift multiplier is m_aHouseAspectPathCounts[nHouse][nAspect]
		// Split up elements - first four are source-align, dest-align, x shift and y shift
		String[] a = LayoutData.m_aHouseAspectPaths[nHouse][nAspect].split("[ ,]+");
		// At least 5 elements needed
		if (a.length < 5)
		{
			System.out.println(  "<p>Error: insufficient args; parsed " + a.length + " elements from (" + nHouse + "," + nAspect + ") [" + LayoutData.m_aHouseAspectPaths[nHouse][nAspect] + "]</p>" );
			return "";
		}
		// Get source-align and dest-align. Currently L and R are supported
		String sourceAlign = a[0];
		String destAlign = a[1];
		// Normal source alignment is R, dest alignment is L
		m_tempSourceXOffset = 0;
		m_tempDestXOffset = 0;
		if (!sourceAlign.equalsIgnoreCase("R")) m_tempSourceXOffset = -m_boxWidth;
		if (!destAlign.equalsIgnoreCase("L")) m_tempDestXOffset = m_boxWidth;
		// Determine x and y shift
		int xShift = m_tempXOffset = m_aHouseAspectPathCounts[nHouse][nAspect] * Integer.parseInt( a[2] );
		int yShift = m_tempYOffset = m_aHouseAspectPathCounts[nHouse][nAspect] * Integer.parseInt( a[3] );
		// If no shift, return as-is
		String sRet = "";
		int lastLetterIndex = 1; // Assume first term is x
		// FIXME apply scaling if needed. For now assume 800x800
		for (int n=4; n < a.length; n++)
		{
			if (n>4) sRet += " ";
			if ((xShift == 0 && yShift == 0) || a[n].matches("[A-Za-z]"))
			{
				sRet += a[n];
				lastLetterIndex = n;
				//System.out.println( "a[" + n + "]: letter " + a[n] );
			} // Not a number
			else
			{
				Double d = new Double(a[n]);
				if ((n-lastLetterIndex) % 2 != 0)
				{
					d += xShift;
				}
				else
				{
					d += yShift;
				}
				sRet += d.toString();
				//System.out.println( "a[" + n + "]: number " + a[n] + " = " + d.toString() );
			}
		}
		// Update count
		m_aHouseAspectPathCounts[nHouse][nAspect]++;

		return sRet;
	}

	// Construct a full representation using origin and destination circles
	public String RenderAspectSvg( int nHouse, int nAspect, String planetId,
		int aspectOriginX, int aspectOriginY,
		int aspectDestX, int aspectDestY,
		String strokeColor, int strokeWidth  )
	{
		// Create id
		String sId = "h" + (nHouse+1) + "-" + (nAspect+3) + planetId;

		// Get aspect path data and calculate m_tempXOffset, m_tempYOffset, m_tempSourceXOffset, m_tempDestXOffset;
		String pathData = GetHouseAspectPath( nHouse, nAspect );

		aspectOriginX += (m_tempXOffset + m_tempSourceXOffset);
		aspectOriginY += m_tempYOffset;
		aspectDestX += (m_tempXOffset + m_tempDestXOffset);
		aspectDestY += m_tempYOffset;

		// Hollow circle at origin
		String s = "";
		//s += "<circle id=\"c-" + sId + "\" fill=\"none\" stroke=\"grey\" stroke-width=\"1\" cx=\"" + aspectOriginX + "\" cy=\"" + aspectOriginY + "\" r=\"3\"/>\n";
		s += ("<path id=\"p-" + sId + "\" fill=\"none\" stroke=\"" + strokeColor + "\" stroke-width=\"" + strokeWidth + "\" d=\"");
			s += ("M " + aspectOriginX + " " + aspectOriginY + " ");
			s += pathData;
			s += (" L " + aspectDestX + " " + aspectDestY);
		s += "\"/>\n";

		// Determine x, y at vector + 45 and vector - 45 at distance of 10px

		// Solid circle at destination
		s += ( "<circle id=\"ce-" + sId + "\" fill=\"" + strokeColor + "\" stroke=\"none\" cx=\"" + aspectDestX + "\" cy=\"" + aspectDestY + "\" r=\"" + (strokeWidth + 1) + "\"/>\n" );

		return s;
	}

	// Reset house:aspect shift counts
	protected void ResetShiftCounts()
	{
		for (int h = 0; h < 12; h++)
		{
			for (int a = 3; a <= 10; a++) m_aHouseAspectPathCounts[h][a-3] = 0;
		}
	}

}

