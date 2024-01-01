//******************************************************************************
// AChart.java:	Applet
//
//******************************************************************************
//package astro;  // the package name maps to a directory

import java.applet.*;
import java.awt.*;
import java.util.*;
import java.net.*;
import java.io.*;
import netscape.javascript.*;
//import astro.*;
import amath_ext2.*;
import amath_base.*;
import amath_ext1.*;
import generated.*;

// Debug stuff
//import com.mhuss.AstroLib.*;

// Swiss ephemeris
import swisseph.*;

// Main panel class - this draws the selected chart type
class ChartPanel extends Panel
{
	private AChart m_p = null;
	private Rectangle m_rc;

	public ChartPanel( AChart p, Rectangle rc ) {
		m_p = p;
		m_rc = rc;
	} // Constructor

	// Override paint method
	public void paint(Graphics g) {
		if (m_p != null) m_p.paint_common( g, false, size() );
	} // paint()

	public void printAll(Graphics g)
	{
		if (m_p != null) m_p.paint_common( g, true, size() );
	} // printAll()

} // class ChartPanel

// Data panel class - this displays text-only information
class TextPanel extends Panel
{
	private AChart m_p = null;
	private Rectangle m_rc;

	public TextPanel( AChart p, Rectangle rc ) {
		m_p = p;
		m_rc = rc;
	} // Constructor

	public void paint(Graphics g)
	{
		if (m_p != null) m_p.paint_textinfo( g, false, size() );
	} // paint()

	public void printAll(Graphics g)
	{
		if (m_p != null) m_p.paint_textinfo( g, true, size() );
	} // printAll()

	// Override Component.preferredSize() (returns Dimension)
	// so we use the space we need and leave the rest for chart
	public Dimension preferredSize() {
		// Use 75% of width, 90% of height
		return new Dimension( Math.max( m_rc.width / 4, getFontMetrics( getFont() ).stringWidth( "Ww: 88Ww88  27: Uttarashadha, P4 Abhij" )), 9 * m_rc.height / 10 );
	} // preferredSize()

} // class TextPanel

// Caption panel class - this displays text under the chart
class CaptionPanel extends Panel
{
	private AChart m_p = null;
	private Rectangle m_rc;

	public CaptionPanel( AChart p, Rectangle rc ) {
		m_p = p;
		m_rc = rc;
	} // Constructor

	public void paint(Graphics g)
	{
		if (m_p != null) m_p.paint_caption( g, false, size() );
	} // paint()

	public void printAll(Graphics g)
	{
		if (m_p != null) m_p.paint_caption( g, true, size() );
	} // printAll()

	// Override Component.preferredSize() (returns Dimension)
	// so we use the space we need and leave the rest for chart
	public Dimension preferredSize() {
		// Use 100% of width, 9% of height
		return new Dimension( m_rc.width, m_rc.height / 11 );
	} // preferredSize()

} // class CaptionPanel()

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

	// Expose for testing
    public String FmtDateTest( double dJ )
    {
		return FmtDate( dJ );
    }

	public void Recalc( int nLevel ) {


	} // Recalc()

} // class DasaSet

//==============================================================================
// Main Class for applet AChart
// Flow of execution (application):
// static main instantiates AChart instance "a" and sets AChartFrame within the instance,
// then calls a.init(), a.start(), and a.m_frame.show()
// Flow of execution (applet):
// AChart constructor is called by framework to create "this",
// then framework calls
// this.init() which instantiates everything
// then framework calls
// this.start() which instantiates Thread m_AChart and starts it
// this.m_AChart invokes
//   this.run() which loops and sleeps
// this.stop() is called when about to unload
// See http://docs.oracle.com/javase/tutorial/deployment/applet/appletExecutionEnv.html
// We now do all the init() stuff involving calculation from run() (within thread context)
//==============================================================================
public class AChart extends Applet implements Runnable
{
    // By convention, the major and minor should match what's defined in Makefile
	public final String m_Version = "v1.51.121";

	// THREAD SUPPORT:
	//		m_AChart	is the Thread object for the applet
	//	Use of Thread may cause
	// problems loading Swiss Ephemeris via http
	//--------------------------------------------------------------------------
	private volatile Thread	 m_AChart;
	private MediaTracker m_Tracker = null;
	private Graphics m_Graphics;
	private Image	 m_PtImages[];
	private	Image	 m_SgnImages[];
	private amath	 m_am;
	private volatile AppletContext m_Browser;
	private ChartPanel m_ChartPanel = null;
	private TextPanel m_TextPanel = null;
	private CaptionPanel m_CaptionPanel = null;
	private Panel    m_MenuPanel = null;
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

	private Label   m_Lbl = null;	// Menu label
	private Button	m_Btn[] = {
								 null,	null,
								 null,	null,
								 null,	null,
								 null,	null
	};
	private Button	m_BtnUp = null;	// Parent menu link

	private AChartFrame m_Frame;

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

	//private DlgStyle m_dlgStyle;

    // Parameter names.  To change a name of a parameter, you need only make
	// a single change.  Simply modify the value of the parameter string below.
    //--------------------------------------------------------------------------
	private final String PARAM_Text = "Description";
	private final String PARAM_Style =  "Style";
	private final String PARAM_Recalc = "Recalc";
	private final String PARAM_Date = "Date";
	private final String PARAM_Time = "Time";
	private final String PARAM_Lat = "Latitude";
	private final String PARAM_Long = "Longitude";
	private final String PARAM_TZ = "TZ";
	private final String PARAM_DST = "DST";
	private final String PARAM_Sun = "Sun";
	private final String PARAM_Moon = "Moon";
	private final String PARAM_Lagna = "Lagna";
	private final String PARAM_Ayanamsa = "Ayanamsa";
	private final String PARAM_Debug = "Debug";
	private final String PARAM_Post = "PostURL";
	private final String PARAM_PostTarget = "PostTarget";
	private final String PARAM_EphPath = "EphPath"; // Also checks for EphPath0, EphPath1, etc. up to EphPath9

	// Button label names
	private final String LBL_Rasi = "Rasi";
	private final String LBL_Candra = "Cand";
	private final String LBL_Hora = "Hora";
	private final String LBL_Drekana = "Drek";
	private final String LBL_Caturtha = "Ch:4";
	private final String LBL_Saptamsa = "Sa:7";
	private final String LBL_Navamsa = "Na:9";
	private final String LBL_Dasamsa = "Da:10";
	private final String LBL_Dvadasamsa = "Dv:12";
	private final String LBL_Sodasamsa = "So:16";
	private final String LBL_Vimsamsa = "Vi:20";
	private final String LBL_Caturvimsamsa = "Cv:24";
	private final String LBL_Bhamsa = "Bh:27";
	private final String LBL_Trimsamsa = "Tr:30";
	private final String LBL_Khavedamsa = "Kh:40";
	private final String LBL_Aksavedamsa = "Ak:45";
	private final String LBL_Sasthyamsa = "Sa:60";
	private final String LBL_Options = "Opts";
	private final String LBL_Charts = "Charts";
	private final String LBL_Style = "Style";
	private final String LBL_Post = "Print";
	private final String LBL_HTML = "HTML";
	private final String LBL_HTML5 = "HTML 5";
	private final String LBL_General = "General";
	private final String LBL_Dasas = "Dasas";
	private final String LBL_Blank = "      ";

	// Page names
	private final String PG_Chart = "chart";
	private final String PG_Dasa = "dasa";
	private final String PG_Strength = "strength";

	// Menu label names (all of which except LBL_Main also appear as buttons)
	private final String LBL_Main = "Main";
	private final String LBL_Varga1 = "Varga1";
	private final String LBL_Varga2 = "Varga2";
	private final String LBL_Vimsottari = "Vimsottari";
	private final String LBL_Output = "Output";
	private final String LBL_Strength = "Strength";

	// Menu definition
	// We have a menu label, label 9, then labels for buttons 1-8
	private final String MENUS[][] = {
		{ LBL_Main,		LBL_Blank,		LBL_Charts, LBL_Dasas, LBL_Output, LBL_Strength, LBL_Options, LBL_Style },
		{ LBL_Charts,	LBL_Main,		LBL_Rasi, LBL_Candra, LBL_Navamsa, LBL_Varga1, LBL_Varga2 },
		{ LBL_Varga1,	LBL_Charts,		LBL_Rasi, LBL_Hora, LBL_Drekana, LBL_Caturtha, LBL_Saptamsa, LBL_Navamsa, LBL_Dasamsa, LBL_Dvadasamsa },
		{ LBL_Varga2,	LBL_Charts,		LBL_Sodasamsa, LBL_Vimsamsa, LBL_Caturvimsamsa, LBL_Bhamsa, LBL_Trimsamsa, LBL_Khavedamsa, LBL_Aksavedamsa, LBL_Sasthyamsa },
		{ LBL_Options,	LBL_Main,		LBL_Style },
		{ LBL_Dasas,	LBL_Main,		LBL_Vimsottari },
		{ LBL_Output,	LBL_Main,		LBL_HTML, LBL_HTML5, LBL_Post },
		{ LBL_Strength,	LBL_Main,		LBL_General },
	};
	private int m_nCurMenu = 0;
	private String m_Page = PG_Chart; // chart, dasa

	// Other public members
	public APoint m_Points[];
	public int m_PGrade[];
	public String m_args[];

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
	// Calculated button rectangles
	protected Rectangle m_DasaUpBtn = null;
	protected Rectangle m_DasaDnBtn = null;

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


	// STANDALONE APPLICATION SUPPORT:
	//		m_fStandAlone will be set to true if applet is run standalone
	//		m_fTextOnly true if running without graphics
	//--------------------------------------------------------------------------
	private boolean m_fStandAlone = false;
	private boolean m_fTextOnly = false;

	// STANDALONE APPLICATION SUPPORT
	// 	The main() method acts as the applet's entry point when it is run
	// as a standalone application. It is ignored if the applet is run from
	// within an HTML page.
	//--------------------------------------------------------------------------
	public static void main(String args[])
	{
		AChart applet_AChart = new AChart();
		// Partially parse args to determine whether we're running text-only
		applet_AChart.m_fStandAlone = true;
		applet_AChart.m_args = args;

			for (int i = 0; i < args.length; i++) {
				StringTokenizer t = new StringTokenizer( args[ i ], "=", false );
				if (t.countTokens() < 1) continue;
				String s1 = t.nextToken();
				if (s1.equalsIgnoreCase( "TextOnly" )) { applet_AChart.m_fTextOnly = true; }
			} // for all args

		if (applet_AChart.m_fTextOnly)
		{
			applet_AChart.init();
			applet_AChart.init2();
			applet_AChart.Recalc();
			applet_AChart.RenderHtml5();
			System.out.println( applet_AChart.m_CodedOutput );
			// We should exit after this since we haven't started any threads
		}
		else
		{

			// Create Toplevel Window to contain applet AChart
			//----------------------------------------------------------------------
			AChartFrame frame = new AChartFrame("AChart");

			// Must show Frame before we size it so insets() will return valid values
			//----------------------------------------------------------------------
			frame.show();
			frame.hide();
			frame.resize(frame.insets().left + frame.insets().right  + 430,
						 frame.insets().top  + frame.insets().bottom + 240);

			// The following code starts the applet running within the frame window.
			// It also calls GetParameters() to retrieve parameter values from the
			// command line, and sets m_fStandAlone to true to prevent init() from
			// trying to get them from the HTML page.
			//----------------------------------------------------------------------
			applet_AChart.m_Frame = frame;

			applet_AChart.m_Frame.add("Center", applet_AChart);
			applet_AChart.init();
			applet_AChart.init2();
			applet_AChart.start();
			applet_AChart.m_Frame.show();

        } // With graphics

	} // main()

	// AChart Class Constructor
	//--------------------------------------------------------------------------
	public AChart()
	{
		m_Frame = null;
		m_AChart = null;
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
				if (!m_fStandAlone) {
					showStatus( szMsg );
				}
				/***
				else if (m_Frame != null) {
					m_Frame.setTitle( szMsg );
				} // Do something
				***/
			}
			catch (Exception e)
			{
				System.err.println( "Exception on showStatus(" + szMsg + "): " + e.toString() );
			}
		} // Debugging

		if (m_jsDebugOutput != 0 && !m_fStandAlone)
		{
	/**** This does not work - throws MalformedURLException with "unknown protocol: javascript"
		// Test javascript output via jsj_Output
		try {
		  getAppletContext().showDocument
			(new URL("javascript:jsj_Output(\"" + szMsg +"\")"));
		}
		catch (MalformedURLException me)
		{
			System.err.println("exception on js output:" + me.toString());
		}
	*****/
			// Need to test with IE, Firefox/Mac, Chrome, Safari
			try {
				JSObject win = (JSObject) JSObject.getWindow(this);
				win.eval("jsj_Output(\"" + szMsg +"\")");
			}
			catch (Exception me)
			{
				System.err.println("other exception on js output:" + me.toString());
			}

		} // Javascript debug output enabled

	} // debugOut()

	// Raw output to html in browser (via exposed Javascript)
	public void browserOut( String html )
	{
		//if (m_jsDebugOutput == 0) return;
		if (m_fStandAlone)
		{
			System.out.println( "--- browserOut() called standalone ---" );
			System.out.println( html );
			System.out.println( "=== end browserOut() ===" );
		}
		else try {
			JSObject win = (JSObject) JSObject.getWindow(this);
			// Escape double quotes and remove newlines and CRs
			win.eval("jsj_RawOutput(\"" + html.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "").replace("\r", "") +"\")");
		}
		catch (Exception me)
		{
			System.err.println("other exception on js output:" + me.toString());
		}
	} // browserOut()

	// APPLET INFO SUPPORT:
	//		The getAppletInfo() method returns a string describing the applet's
	// author, copyright date, or miscellaneous information.
    //--------------------------------------------------------------------------
	public String getAppletInfo()
	{
		return "Name: AstroChart " + m_Version + "\r\n" +
		       "Author: Henry Groover";
	}

	// PARAMETER SUPPORT
	//		The getParameterInfo() method returns an array of strings describing
	// the parameters understood by this applet.
	//
    // JApplet1 Parameter Information:
    //  { "Name", "Type", "Description" },
    //--------------------------------------------------------------------------
	public String[][] getParameterInfo()
	{
		String[][] info =
		{
			{ PARAM_Style, "String", "North or South" },
			{ PARAM_Recalc, "int", "Recalc interval in seconds" },
			{ PARAM_Text, "String", "Text description of chart" },
			{ PARAM_Date, "String", "Date as YYYYMMDD" },
			{ PARAM_Time, "String", "24hr time as HHMM" },
			{ PARAM_Lat, "String", "Latitude in degrees and minutes as ##N## or ##S##" },
			{ PARAM_Long, "String", "Longitude in degrees and minutes as ###W## or ###E##" },
			{ PARAM_TZ, "String", "Time zone as -HHMM or +HHMM (- is W of Greenwich)" },
			{ PARAM_DST, "int", "Daylight savings time in hours" },
			{ PARAM_Sun, "String", "Sun position" },
			{ PARAM_Moon, "String", "Moon position" },
			{ PARAM_Lagna, "String", "Lagna position" },
			{ PARAM_Ayanamsa, "String", "Ayanamsa type (Lahiri, etc)" },
			{ PARAM_EphPath, "String", "Directory or URI to add to ephemeris data search path" },
			{ "Mercury", "String", "Mercury position" },
			{ "Venus", "String", "Venus" },
			{ "Mars", "String", "Mars" },
			{ "Jupiter", "String", "Jupiter" },
			{ "Saturn", "String", "Saturn" },
			{ "Rahu", "String", "Rahu" },
			{ "Ketu", "String", "Ketu" },
			{ PARAM_Debug, "int", "1 to debug applet" },
		};
		return info;
	}


	// Change menu to specified index in MENUS[]
	private void ChangeMenu( int nNew ) {
		// Number of buttons not counting label and parent
		int nButtons = MENUS[nNew].length;
		int n;
		// Set label and parent
		m_Lbl.setText( MENUS[nNew][0] );
		m_BtnUp.setLabel( MENUS[nNew][1] );
		for (n = 2; n < nButtons; n++) {
			m_Btn[n-2].setLabel( MENUS[nNew][n] );
			m_Btn[n-2].resize( m_Btn[n-2].minimumSize() );
		} // for all remaining buttons
		// Blank all others
		for (n = nButtons; n < 2 + 8; n++) {
			m_Btn[n-2].setLabel( LBL_Blank );
			m_Btn[n-2].resize( m_Btn[n-2].minimumSize() );
		} // for all remaining
		m_nCurMenu = nNew;
		// Re-do layout
		m_MenuPanel.validate();
	} // ChangeMenu()

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

		// Get browser reference - needed for debugOut
		if (!m_fStandAlone)
		{
			m_Browser = getAppletContext();
		}
		else
		{
			debugOut( "Running standalone" );
		}

		debugOut( "Applet init() in thread: "  + Thread.currentThread().toString() );

		// Moved image load initialization to init2

		// We initialized m_am here previously - now moved to init2()

		if (!m_fTextOnly)
		{

			// Determine a font such that 16 lines of text will fit
			Font f = getFont();
			FontMetrics fm = getFontMetrics( f );
			Rectangle rcBounds = bounds();
			if (!m_fStandAlone && fm.getHeight() * m_nMaxText > rcBounds.height ) {
				f = new Font( f.getName(), f.getStyle(), f.getSize() * rcBounds.height / (fm.getHeight() * m_nMaxText) );
				// There's no guarantee that size (points) is proportionate to height reduction,
				// so shrink a little until we find a match.
				while (getFontMetrics( f ).getHeight() * m_nMaxText > rcBounds.height && f.getSize() > 4) {
					f = new Font( f.getName(), f.getStyle(), f.getSize() - 1 );
				}
				setFont( f );
				debugOut( "Font changed" );
			} // Running as applet and font is too big


			// Set up layout
			BorderLayout BLayout = new BorderLayout();
			FlowLayout FLayout = new FlowLayout();
			setLayout( BLayout );
			m_MenuPanel = new Panel();
			m_MenuPanel.setLayout( FLayout );
			add( "North", m_MenuPanel );
				m_MenuPanel.add( m_Lbl = new Label( LBL_Blank, Label.LEFT ) );
				int nBtn;
				for (nBtn = 0; nBtn < 8; nBtn++) {
					m_MenuPanel.add( m_Btn[nBtn] = new Button( LBL_Blank ) );
				}
				m_MenuPanel.add( m_BtnUp = new Button( LBL_Blank ) );
			m_ChartPanel= new ChartPanel( this, rcBounds );
			add( "Center", m_ChartPanel );
			add( "East", m_TextPanel = new TextPanel( this, rcBounds ) );
			add( "South", m_CaptionPanel = new CaptionPanel( this, rcBounds ) );

			// If you use a ResourceWizard-generated "control creator" class to
			// arrange controls in your applet, you may want to call its
			// CreateControls() method from within this method. Remove the following
			// call to resize() before adding the call to CreateControls();
			// CreateControls() does its own resizing.
			//----------------------------------------------------------------------
			if (m_fStandAlone) {
				debugOut( "Resizing" );
				resize((m_nMaxText + m_nMaxText / 2) * fm.getHeight(), m_nMaxText * fm.getHeight());
			}

		} // Not text-only

		/*******************************************/

		m_initStage = 1;

		debugOut( "Exiting AChart init()" );
	}

	// Second stage of init called within Thread context
	public void init2()
	{
		debugOut( "Entering AChart init2()" );

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

		// We can use m_Browser.showDocument( URL, String "Frame name" ) where frame may also be a reserved
		// value such as _top, _parent, or _blank
		// We can also use m_Browser.getApplet( String "Applet name" ) to get another applet by name
		// We can use the Enumeration returned by m_Browser.getApplets() to enumerate all applets.
		// while (e.hasMoreElements()) Applet a = e.nextElement();

		/********* Moved from init2() **************/

		if (!m_fTextOnly)
		{

			// Start images loading
			try {
				m_Tracker = new MediaTracker( this );
				m_Graphics = getGraphics();

				m_PtImages = new Image[amath_const.MAX_POINTS];
				m_SgnImages= new Image[12];
			}
			catch (Exception e)
			{
				debugOut( "Exception 2: " + e.toString() );
			}

			// Load in all the images
			//------------------------------------------------------------------

			try {

				// For each image in the animation, this method first constructs a
				// string containing the path to the image file; then it begins
				// loading the image into the m_Images array.  Note that the call to
				// getImage will return before the image is completely loaded.
				//------------------------------------------------------------------
				for (int i = 1; i <= 12; i++)
				{
					// Build path to next sign image
					//--------------------------------------------------------------
					m_SgnImages[i-1] = LoadImage( "sgn", i );
					m_Tracker.addImage(m_SgnImages[i-1], 1);
				}

				for (int i = 0; i < amath_const.MAX_POINTS; i++)
				{
					m_PtImages[i] = LoadImage( "pt", i );
					m_Tracker.addImage( m_PtImages[ i ], 0 );
				}
			}
			catch (Exception e)
			{
				debugOut( "Exception 3: " + e.toString() );
			}

		} // Not text-only

		// Allocate math object
		m_am = new amath();

		// PARAMETER SUPPORT
		//		The following code retrieves the value of each parameter
		// specified with the <PARAM> tag and stores it in a member
		// variable.
		//----------------------------------------------------------------------
		String param;

		// Build composite of all parameters passed

		// Source: Parameter description
		//----------------------------------------------------------------------
		//Dialog dlg;
		//dlg = new Dialog( m_Frame, "Parms", true );
		//dlg.show();
		if (!m_fStandAlone) {

			debugOut( "Not standalone" );

			param = getParameter(PARAM_Style);
			m_nStyle = 0;
			if (param != null) {
				m_Style = param;
				if (m_Style.equalsIgnoreCase( "North") )
					m_nStyle = 1;
			}

			debugOut( "Getting parms" );
			param = getParameter( PARAM_Recalc );
			if (param != null) m_nRecalc = amath.atoi( param );
			param = getParameter( PARAM_Time );
			if (param != null) m_Time = param;
			param = getParameter( PARAM_Date );
			if (param != null) m_Date = param;
			param = getParameter( PARAM_Lat );
			if (param != null) m_Lat = param;
			param = getParameter( PARAM_Long );
			if (param != null) m_Long = param;
			param = getParameter( PARAM_DST );
			if (param != null) m_nDST = amath.atoi( param );
			param = getParameter( PARAM_TZ );
			if (param != null) m_TZ = param;
			param = getParameter( PARAM_Text );
			if (param != null) m_Text = param;
			param = getParameter( PARAM_Ayanamsa );
			if (param != null) m_Ayanamsa = param;
			param = getParameter( PARAM_Debug );
			if (param != null) m_nDebug = amath.atoi( param );
			param = getParameter( PARAM_Post );
			if (param != null) m_PostAddr = param;
			param = getParameter( PARAM_PostTarget );
			if (param != null) m_PostTarget = param;
			// Meaningless in applet context
			param = getParameter( "TextOnly" );
			if (param != null) m_fTextOnly = true;

			// We may have multiple elements to add to ephemeris search path
			// Without parsing, this is how we pass an array as a parameter...
			int pIndex;
			String pName;
			for (pIndex = -1; pIndex < 10; pIndex++)
			{
				if (pIndex < 0) pName = PARAM_EphPath;
				else pName = PARAM_EphPath + pIndex;
				param = getParameter( pName );
				if (param == null) continue;
				// Append to list of searchable paths
				m_am.AddToEphPath( param, 0 );
			}

			int n;
			for (n = amath_const.PT_FIRST; n <= amath_const.PT_LAST; n++) {
				param = getParameter( m_aPoints[ n ] );
				//dlg = new Dialog( m_Frame, "Getting " + m_aPoints[ n ], true );
				//dlg.show();
				if (param == null) param = getParameter( m_aSPoints[ n ] );
				if (param != null) {
					//dlg = new Dialog( m_Frame, param, true );
					//dlg.show();
					m_Points[ n ].Parse( param, n );
				} // got a point
			} // for all points

		} // Running as applet
		// If running standalone, parms passed on command line
		else {
			debugOut( "Standalone" );
			m_nStyle = 0;
			for (int i = 0; i < m_args.length; i++) {
				StringTokenizer t = new StringTokenizer( m_args[ i ], "=", false );
				if (t.countTokens() != 2) continue;
				String s1 = t.nextToken();
				String s2 = t.nextToken();
				if (s1.equalsIgnoreCase( PARAM_Style )) {
					m_Style = s2;
					if (s2.equalsIgnoreCase( "North" )) {
						m_nStyle = 1;
					}
					continue;
				} // Got style
				if (s1.equalsIgnoreCase( PARAM_Text )) { m_Text = s2; continue; }
				if (s1.equalsIgnoreCase( PARAM_Ayanamsa )) { m_Ayanamsa = s2; continue; }
				if (s1.equalsIgnoreCase( PARAM_Time )) { m_Time = s2; continue; }
				if (s1.equalsIgnoreCase( PARAM_Date )) { m_Date = s2; continue; }
				if (s1.equalsIgnoreCase( PARAM_Lat )) { m_Lat = s2; continue; }
				if (s1.equalsIgnoreCase( PARAM_Long )) { m_Long = s2; continue; }
				if (s1.equalsIgnoreCase( PARAM_TZ )) { m_TZ = s2; continue; }
				if (s1.equalsIgnoreCase( PARAM_DST )) { m_nDST = amath.atoi( s2 ); continue; }
				if (s1.equalsIgnoreCase( PARAM_Debug )) { m_nDebug = amath.atoi( s2 ); continue; }
				if (s1.equalsIgnoreCase( PARAM_Post )) { m_PostAddr = s2; continue; }
				if (s1.equalsIgnoreCase( PARAM_PostTarget )) { m_PostTarget = s2; continue; }
				if (s1.equalsIgnoreCase( "TextOnly" )) { m_fTextOnly = true; continue; }


				// Check for points
				for (int iPoint = amath_const.PT_FIRST; iPoint <= amath_const.PT_LAST; iPoint++) {
					if (s1.equalsIgnoreCase( m_aPoints[ iPoint ] ) ||
						s1.equalsIgnoreCase( m_aSPoints[ iPoint ] )) {
						//Double d;
						//d = new Double( s2 );
						//m_Points[ iPoint ] = d.doubleValue();
						m_Points[ iPoint ].Parse( s2, iPoint );
						break;
					} // Got a match
				} // for all points

			} // for all args
		} // Running standalone

		// Search for existing applet with same parameter signature

		// Existing applet's current settings take precedence over
		// parameters.  This is the only way we can handle printing
		// from IE4.

		// Set house and moon offsets
		for (int i = amath_const.PT_FIRST; i <= amath_const.PT_LAST; i++) {
			m_Points[ i ].SetAsc( m_Points[ amath_const.PT_LAGNA ] );
			m_Points[ i ].SetMoon( m_Points[ amath_const.PT_MOON ] );
		} // for all points

		debugOut( "Grading points" );

		// Grade points
		GradePoints( amath_const.PT_LAGNA );

		if (!m_fTextOnly)
		{

			// Set buttons (after giving a chance to override initial menu
			ChangeMenu( 0 );

		}

		// Initialization completed
		m_initStage = 2;

		debugOut( "Exiting AChart init2()" );
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

	// Place additional applet clean up code here.  destroy() is called when
	// when you applet is terminating and being unloaded.
	//-------------------------------------------------------------------------
	public void destroy()
	{
		// TODO: Place applet cleanup code here
	}

	// AChart Paint Handler
	//--------------------------------------------------------------------------
//	public void paint(Graphics g)
//	{
//		// This is handled at the component level
//		//paint_common( g, false, size() );
//	} // paint()

	// Print handler
//	public void print(Graphics g)
//	{
//		//paint_common( g, true );
//	} // print()

//	public void printAll(Graphics g)
//	{
//		// This is handled at the component level
//		//paint_common( g, true, size() );
//	} // printAll()

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
	protected void DumpText( PrintStream o, int format, int maxDivisions, boolean withNavamsa ) {

		if (format == 2 || format == 3)
		{
			if (m_fStandAlone)
			{
				if (format == 3) o.println( "<!DOCTYPE html>" );
				o.println( "<html><head><title>" + m_Text + "</title>" );
			}
			// Style preamble
			o.println( NORTHERN_HTML_CSS );
			if (m_fStandAlone)
			{
				o.println( "</head><body>" );
			}
		}

		int oldChartWidth = m_chartWidthHtml;
		int oldChartHeight = m_chartHeightHtml;

		if (format == 3)
		{
			//m_chartWidthHtml = 600;
			//m_chartHeightHtml = 600;
		}

		o.println( "<h1>Java Jyotish " + m_Version + "</h1>" );
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

		// If not dumping all divisions but withNavamsa specified, do it now
		if (maxDivisions < 4 && withNavamsa)
		{
			m_nDivision = 9;
			DumpDivision( o, format );
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

		if (m_fStandAlone && (format == 2 || format == 3))
		{
			o.println( "</body></html>" );
		}

		// Restore original html chart size values
		m_chartWidthHtml = oldChartWidth;
		m_chartHeightHtml = oldChartHeight;

		// Restore everything
		MyRepaint();

	} // DumpText

	// Post to URL
	public void Post()
	{
		/******
		if (m_PostAddr == null || m_PostAddr.length() < 1) {
			debugOut("Failed to post, m_PostAddr null or empty");
			return;
		} // FIXME complain

		debugOut( "Posting output to: " + m_PostAddr );

		URL url;
		HttpURLConnection urlConn;
		try {
			url = new URL( m_PostAddr );
		}
		catch (java.net.MalformedURLException e) {
			debugOut("Failed to create url:" + e.toString());
			return;
		}

		debugOut( "Opening connection to " + m_PostAddr );
		try {
			urlConn = (HttpURLConnection)url.openConnection();
		}
		catch (java.io.IOException i) {
			debugOut("Failed to open " + m_PostAddr + ": " + i.toString());
			return;
		}

		// Set attributes
		try
		{
			urlConn.setDoOutput( true );
			urlConn.setDoInput( true );
			urlConn.setUseCaches( false );
			//urlConn.setAllowUserInteraction( true );
			urlConn.setRequestProperty( "Content-type", "text/plain" );
			urlConn.setRequestMethod("POST");
		}
		catch (java.net.ProtocolException pe)
		{
			debugOut( "Protocol exception: " + pe.toString() );
			return;
		}
		************/

		// Try connecting
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		PrintStream o = new PrintStream(bo); // DataOutputStream uses writeChars

		/**********
		//DataInputStream inp = null;
		debugOut( "Opening output stream (with connect)..." );
		try {
			urlConn.connect();
			o = new PrintStream( urlConn.getOutputStream() );
			//inp = new DataInputStream( urlConn.getInputStream() );
		}
		//catch (UnknownServiceException s) {
		//	return;
		//}
		catch (java.io.IOException i) {
			debugOut("Failed to postData: " +i.toString());
			return;
		}
		************/

		debugOut( "Creating output..." );
		// Dump only rasi chart and navamsa, or use MAX_DIVISIONS for third parameter for all
		DumpText( o, 0, 1, true );
		// This posts everything
		o.flush();
		o.close();

		debugOut( "Sending output to browser..." );
		browserOut( bo.toString() );

		/*********
		debugOut( "Opening input stream..." );
		try {
			inp = new DataInputStream( urlConn.getInputStream() );
		}
		catch (IOException i) {
			return;
		}

		debugOut( "Reading input..." );
		// echo.cgi returns a numeric token which we can then pass as an argument
		// to echo.cgi to redisplay everything in a window.
		byte b[] = new byte[1024];
		int n = 0;
		String sURL;
		String sRaw = "";
		sURL = m_PostAddr;
		sURL += "?";
		try {
			boolean inBody = true;
			do {
				int bytes = inp.read(b);
				if (bytes <= 0) break;
				String s = new String(b,0,bytes);
				if (inBody)
				{
					// Look for </body>
					int bodyPos = s.indexOf("</body>");
					if (bodyPos >= 0)
					{
						debugOut( "Got body close at " + bodyPos );
						if (bodyPos > 0) sRaw = sRaw + s.substring(0,bodyPos);
						break;
					}
					// Avoid splitting tags
					sRaw = sRaw + s;
					//browserOut( s );
					n++;
					if (s.length()==0) break;
					debugOut( "[" + n + "] len=" + s.length() );
				}
				else
				{
					// Look for <body>
					int bodyPos = s.indexOf("<body");
					if (bodyPos >= 0)
					{
						inBody = true;
						s = s.substring(bodyPos+5);
						bodyPos = s.indexOf(">");
						if (bodyPos >= 0)
						{
							sRaw = sRaw + s.substring( bodyPos+1 );
						}
					}
				}
				//b[n] = inp.readByte();
			} while (true  */ /*b[n] > 32 && ++n < 100*/ /*);
		}
		catch (EOFException e) {
			;
		}
		catch (IOException i) {
			n = 0;
			debugOut( "IO exception reading input: " + i.toString() );
		}

		debugOut( "sRaw.length()=" + sRaw.length() );
		browserOut( sRaw );
		************/

		/******* Don't need this...
		if (n > 0) {
			sURL += new String( b, 0, 0, n );
		} // Finish URL
		else {
			sURL = "";
		}

		debugOut( "Got: " + sURL );

		try {
			// This posts everything to the URL
			//o.close();
			inp.close();
		}
		catch (IOException i) {
			return;
		}

		// This starts a new invocation of the URL with no input
		if (sURL.length() > 0) {
			debugOut( "Re-connecting to " + sURL + ", target " + m_PostTarget );
			try {
				getAppletContext().showDocument( new URL( sURL ), m_PostTarget );
			}
			catch (MalformedURLException e) {
				debugOut( "Failed to reconnect: " + e.toString());
				return;
			}
		} // Show it
		*************/

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

	// Draw text information
	public void paint_textinfo( Graphics g, boolean bPrint, Dimension d )
	{
		// List points
		int i, nXText = 4, nYText = g.getFontMetrics().getHeight();
		int nXOff = 0, nYOff = 0;
		int nNakOff = g.getFontMetrics().stringWidth( "Ww: 88Ww88  " );

		if (m_nCalculated == 0) {
			return;
		} // Not calculated

		g.drawString( DivisionName() + ":", nXOff + nXText, nYOff + nYText );

		// Include ascendant
		System.out.println( "Using division " + m_nDivision );
		for (i = 0; i < amath_const.MAX_POINTS; i++) {
			int j = m_PGrade[ i ];
			g.drawString( m_aPointAbbr[ j ] + ": " + m_Points[ j ].Fmt( m_nPrecMult, m_nDivision ), nXOff + nXText, nYOff + nYText * (i + 2) );
			m_Points[ j ].SetNakDiv( m_nDivision );
			g.drawString( m_Points[ j ].m_nak.Fmt(), nXOff + nXText + nNakOff, nYOff + nYText * (i + 2) );
			System.out.println( "Index " + i + " grade " + j + " " + m_Points[j].GetDivLong(m_nDivision) + " " + m_aPointAbbr[j] + ": " + m_Points[j].Fmt(m_nPrecMult, m_nDivision) + " nak " + m_Points[j].m_nak.Fmt() );
		} // for all points

		i += 3;
		String szDST = "";
		if (m_nDST != 0) szDST = " DST:" + m_nDST;
		g.drawString( m_Time + " " + m_Date + " TZ: " + m_TZ + szDST, nXOff + nXText, nYOff + nYText * i );
		i++;
		g.drawString( m_Lat + " " + m_Long, nXOff + nXText, nYOff + nYText * i );
		i++;
		/***
		if (m_am.m_dJD != 0.0) {
			//g.drawString( "JD: " + String.valueOf( (new Double( m_am.m_dJD )).longValue() ), nXOff + nXText, nYOff + nYText * i );
			g.drawString( "JD: " + m_am.m_dJD, nXOff + nXText, nYOff + nYText * i );
			i++;
		}
		***/
		g.drawString( m_am.TithiName(), nXOff + nXText, nYOff + nYText * i );
		i++;
		// Show ayanamsa
		g.drawString( m_Ayanamsa + ": " + m_am.m_dAyanamsa, nXOff + nXText, nYOff + nYText * i );
		i++;
		// Check for oddity in rec to spherical
		/***
		if (m_am.m_rotReduced)
		{
			g.drawString( "Rotation reduced: " + m_am.m_dRA, nXOff + nXText, nYOff + nYText * i );
			i++;
		}
		***/
		g.drawString( "Calc status: " + m_am.m_se_Status, nXOff + nXText, nYOff + nYText * i );
		i++;
		/***************
		// Debug - compare our jd with Meeus' calculation
		double dS = m_nHour * 3600.0 + m_nMinute * 60.0 + m_nSecond;
		// Constructor uses year, month, day, and seconds past midnight
		AstroDate ad = new AstroDate( m_nYear, m_nMonth, m_nDay, dS );
		// Get hours adjusted to UTC
		double dUTCHours = m_nDST + m_nTZ / 60.0;
		double dMJD = ad.jd() - dUTCHours / 24.0;
		***************/
		/***
		g.drawString( "MJD(" + m_nYear + "," + m_nMonth + "," + m_nDay + "," + dS + "): " + dMJD + " h=" + dUTCHours, nXOff + nXText, nYOff + nYText * i );
		i++;
		***/

	} // paint_textinfo()

	// Draw caption information
	public void paint_caption( Graphics g, boolean bPrint, Dimension d )
	{
		// Draw caption text at bottom
		int nXOff = 4, nYOff = g.getFontMetrics().getHeight();
		g.drawString( m_Text, nXOff, nYOff );
		g.drawString( m_Version, d.width - g.getFontMetrics().stringWidth( m_Version ), d.height - 2 );

	} // paint_caption()

	// Draw background and fill in endpoints for southern style
	private void PrepareSouthern( Graphics g, boolean bPrint, int nStartHouse, int nXOff, int nYOff, Rectangle r, int HPEndPts[][] )
	{
				int nQX, nQY;
				g.drawRect( nXOff, nYOff, r.width, r.height );
				nQX = r.width / 4;
				nQY = r.height / 4;
				// Numbered according to order we draw in
				//      1  2  4
				//   +--+--+--+--+
				//   |  |  |  |  |
				// 5 +--+--+--+--+
				//   |  |     |  |
				// 6 +--+    7+--+
				//   |  |  3  |  |
				// 8 +--+--+--+--+
				//   |  |  |  |  |
				//   +--+--+--+--+

				// Vertical lines
				g.drawLine( nXOff + nQX, nYOff, nXOff + nQX, nYOff + r.height );
				g.drawLine( nXOff + nQX * 2, nYOff, nXOff + nQX * 2, nYOff + nQY );
				g.drawLine( nXOff + nQX * 2, nYOff + nQY * 3, nXOff + nQX * 2, nYOff + r.height );
				g.drawLine( nXOff + nQX * 3, nYOff, nXOff + nQX * 3, nYOff + r.height );

				// Horizontals
				g.drawLine( nXOff, nYOff + nQY, nXOff + r.width, nYOff + nQY );
				g.drawLine( nXOff, nYOff + nQY * 2, nXOff + nQX, nYOff + nQY * 2 );
				g.drawLine( nXOff + nQX * 3, nYOff + nQY * 2, nXOff + r.width, nYOff + nQY * 2 );
				g.drawLine( nXOff, nYOff + nQY * 3, nXOff + r.width, nYOff + nQY * 3 );

				// Set sign positions
				debugOut( "Drawing signs, size = " + m_nSgnImageX + ", " + m_nSgnImageY );
				int nEOff = 12; // How far from leading edge of house
				int nEOff2 = nEOff / 3; // Close placement for inside signs
				if (m_SgnImages[ 0 ] != null) {
				g.drawImage( m_SgnImages[ 0 ], nXOff + nQX + nEOff2, nYOff + nQY - m_nSgnImageY - nEOff2, null );
				g.drawImage( m_SgnImages[ 1 ], nXOff + nQX * 2 + nEOff2, nYOff + nQY - m_nSgnImageY - nEOff2, null );
				g.drawImage( m_SgnImages[ 2 ], nXOff + nQX * 3 + nEOff2, nYOff + nQY - m_nSgnImageY - nEOff2, null );
				g.drawImage( m_SgnImages[ 3 ], nXOff + nQX * 3 + nEOff2, nYOff + nQY * 2 - m_nSgnImageY - nEOff2, null );
				g.drawImage( m_SgnImages[ 4 ], nXOff + nQX * 3 + nEOff2, nYOff + nQY * 3 - m_nSgnImageY - nEOff2, null );
				g.drawImage( m_SgnImages[ 5 ], nXOff + nQX * 3 + nEOff2, nYOff + nQY * 3 + nEOff2, null );
				g.drawImage( m_SgnImages[ 6 ], nXOff + nQX * 2 + nEOff2, nYOff + nQY * 3 + nEOff2, null );
				g.drawImage( m_SgnImages[ 7 ], nXOff + nQX     + nEOff2, nYOff + nQY * 3 + nEOff2, null );
				g.drawImage( m_SgnImages[ 8 ], nXOff + nQX - nEOff2 - m_nSgnImageX, nYOff + nQY * 3 + nEOff2, null );
				g.drawImage( m_SgnImages[ 9 ], nXOff + nQX - nEOff2 - m_nSgnImageX, nYOff + nQY * 3 - m_nSgnImageY - nEOff2, null );
				g.drawImage( m_SgnImages[10 ], nXOff + nQX - nEOff2 - m_nSgnImageX, nYOff + nQY * 2 - m_nSgnImageY - nEOff2, null );
				g.drawImage( m_SgnImages[11 ], nXOff + nQX - nEOff2 - m_nSgnImageX, nYOff + nQY - m_nSgnImageY - nEOff2, null );
				}

				// Set planet endpoints.  These are fixed for southern.
				// These values do not include nXOff or nYOff.
				int nOffset = nQX / 5; // How far from top / bottom / left / right edge
				HPEndPts[ 0 ][0] = nQX + nEOff;
				HPEndPts[ 0 ][1] = nOffset;
				HPEndPts[ 0 ][2] = nQX * 2 - nEOff;
				HPEndPts[ 0 ][3] = nOffset;
				HPEndPts[ 1 ][0] = nQX * 2 + nEOff;
				HPEndPts[ 1 ][1] = nOffset;
				HPEndPts[ 1 ][2] = nQX * 3 - nEOff;
				HPEndPts[ 1 ][3] = nOffset;
				HPEndPts[ 2 ][0] = nQX * 3 + nEOff;
				HPEndPts[ 2 ][1] = nEOff;
				HPEndPts[ 2 ][2] = r.width - nEOff;
				HPEndPts[ 2 ][3] = nQY - nEOff;
				HPEndPts[ 3 ][0] = r.width - nOffset;
				HPEndPts[ 3 ][1] = nQY + nEOff;
				HPEndPts[ 3 ][2] = r.width - nOffset;
				HPEndPts[ 3 ][3] = nQY * 2 - nEOff;
				HPEndPts[ 4 ][0] = r.width - nOffset;
				HPEndPts[ 4 ][1] = nQY * 2 + nEOff;
				HPEndPts[ 4 ][2] = r.width - nOffset;
				HPEndPts[ 4 ][3] = nQY * 3 - nEOff;
				HPEndPts[ 5 ][0] = r.width - nEOff;
				HPEndPts[ 5 ][1] = nQY * 3 + nEOff;
				HPEndPts[ 5 ][2] = nQX * 3 + nEOff;
				HPEndPts[ 5 ][3] = r.height - nEOff;
				HPEndPts[ 6 ][0] = nQX * 3 - nEOff;
				HPEndPts[ 6 ][1] = r.height - nOffset;
				HPEndPts[ 6 ][2] = nQX * 2 + nEOff;
				HPEndPts[ 6 ][3] = r.height - nOffset;
				HPEndPts[ 7 ][0] = nQX * 2 - nEOff;
				HPEndPts[ 7 ][1] = r.height - nOffset;
				HPEndPts[ 7 ][2] = nQX     + nEOff;
				HPEndPts[ 7 ][3] = r.height - nOffset;
				HPEndPts[ 8 ][0] = nQX - nEOff;
				HPEndPts[ 8 ][1] = r.height - nEOff;
				HPEndPts[ 8 ][2] = nEOff;
				HPEndPts[ 8 ][3] = nQY * 3 + nEOff;
				HPEndPts[ 9 ][0] = nOffset;
				HPEndPts[ 9 ][1] = nQY * 3 - nEOff;
				HPEndPts[ 9 ][2] = nOffset;
				HPEndPts[ 9 ][3] = nQY * 2 + nEOff;
				HPEndPts[10 ][0] = nOffset;
				HPEndPts[10 ][1] = nQY * 2 - nEOff;
				HPEndPts[10 ][2] = nOffset;
				HPEndPts[10 ][3] = nQY + nEOff;
				HPEndPts[11 ][0] = nEOff;
				HPEndPts[11 ][1] = nQY - nEOff;
				HPEndPts[11 ][2] = nQX - nEOff;
				HPEndPts[11 ][3] = nEOff;
	} // PrepareSouthern()

	private void PrepareNorthern( Graphics g, boolean bPrint, int nStartHouse, int nXOff, int nYOff, Rectangle r, int HPEndPts[][] )
	{
				//                C
				// B+-------------+-------------+D
				//  |\           / \           /|
				//  |  \       /  1  \       /  |
				//  |    \ 2 /         \ 12/    |
				//  |     3*             *11    |
				//  |    / 4 \         /   \    |
				//  |  /       \     /       \  |
				//  |/           \ /           \|
				// A+             *             +E
				//  |\          /   \          /|
				//  |  \      /       \      /  |
				//  |    \  /           \10/    |
				//  |     5*7             *9    |
				//  |    / 6 \          / 8 \   |
				//  |  /       \      /       \ |
				//  |/           \  /           \
				// H+--------------+------------+F
				//                 G
				int nHX, nHY;
				g.drawRect( nXOff, nYOff, r.width, r.height );
				nHX = (r.width) / 2;
				nHY = (r.height) / 2;

				// Draw vertex diagonals
				g.drawLine( nXOff, nYOff, nXOff + r.width, nYOff + r.height ); // B-F
				g.drawLine( nXOff + r.width, nYOff, nXOff, nYOff + r.height ); // D-H

				// Draw midpoint diagonals
				g.drawLine( nXOff, nYOff + nHY, nXOff + nHX, nYOff ); // A-C
				g.drawLine( nXOff + nHX, nYOff, nXOff + r.width, nYOff + nHY ); // C-E
				g.drawLine( nXOff + r.width, nYOff + nHY, nXOff + nHX, nYOff + r.height ); // E-G
				g.drawLine( nXOff + nHX, nYOff + r.height, nXOff, nYOff + nHY ); // G-A

				// Set sign positions
				int nEOff = 16; // Offset from leading edge of house.
				int nEOff2 = m_nPtImageX + 10; // Slightly larger than nEOff for corner placement
				int nEOff3 = 4; // Closer to edge for corner placement
				if (m_SgnImages[ 0 ] != null) {
				g.drawImage( m_SgnImages[ nStartHouse ], nXOff + nHX - m_nSgnImageX / 2, nYOff + nHY - m_nSgnImageY - nEOff, null );
				g.drawImage( m_SgnImages[ (nStartHouse + 1) % 12 ], nXOff + nHX / 2 - m_nSgnImageX / 2, nYOff + nHY / 2 - m_nSgnImageY - nEOff, null );
				g.drawImage( m_SgnImages[ (nStartHouse + 2) % 12 ], nXOff + nHX / 2 - m_nSgnImageX - nEOff, nYOff + nHY / 2 - m_nSgnImageY / 2, null );
				g.drawImage( m_SgnImages[ (nStartHouse + 3) % 12 ], nXOff + nHX - m_nSgnImageX - nEOff, nYOff + nHY - m_nSgnImageY / 2, null );
				g.drawImage( m_SgnImages[ (nStartHouse + 4) % 12 ], nXOff + nHX / 2 - m_nSgnImageX - nEOff, nYOff + nHY + nHY / 2 - m_nSgnImageY / 2, null );
				g.drawImage( m_SgnImages[ (nStartHouse + 5) % 12 ], nXOff + nHX / 2 - m_nSgnImageX / 2, nYOff + nHY + nHY / 2 + nEOff, null );
				g.drawImage( m_SgnImages[ (nStartHouse + 6) % 12 ], nXOff + nHX - m_nSgnImageX / 2, nYOff + nHY + nEOff, null );
				g.drawImage( m_SgnImages[ (nStartHouse + 7) % 12 ], nXOff + nHX + nHX / 2 - m_nSgnImageX / 2, nYOff + nHY + nHY / 2 + nEOff, null );
				g.drawImage( m_SgnImages[ (nStartHouse + 8) % 12 ], nXOff + nHX + nHX / 2 + nEOff, nYOff + nHY + nHY / 2 - m_nSgnImageY / 2, null );
				g.drawImage( m_SgnImages[ (nStartHouse + 9) % 12 ], nXOff + nHX + nEOff, nYOff + nHY - m_nSgnImageY / 2, null );
				g.drawImage( m_SgnImages[ (nStartHouse + 10)% 12 ], nXOff + nHX + nHX / 2 + nEOff, nYOff + nHY / 2 - m_nSgnImageY / 2, null );
				g.drawImage( m_SgnImages[ (nStartHouse + 11)% 12 ], nXOff + nHX + nHX / 2 - m_nSgnImageX / 2, nYOff + nHY / 2 - m_nSgnImageY - nEOff, null );
				}

				// Set planet endpoints.  These are variable for northern
				//int nOffset = 8; // Offset from outer edge.  This is increased for house % 3 == 1
				//int HOff[] = { 0, 7, 0 };
				// Note that these are end points for planets within signs.  Also note
				// that we're assigning them starting with house 1 but the array starts
				// with sign 1.
				debugOut( "Setting plan endpts" );
				HPEndPts[ nStartHouse + 0 ][0] = nHX + nHX / 2 - nEOff - m_nPtImageX / 2;
				HPEndPts[ nStartHouse + 0 ][1] = nHY / 2;
				HPEndPts[ nStartHouse + 0 ][2] = nHX - nHX / 2 + nEOff + m_nPtImageX / 2;
				HPEndPts[ nStartHouse + 0 ][3] = nHY / 2;
				HPEndPts[ (nStartHouse + 1) % 12 ][0] = nHX - m_nPtImageX / 2 - nEOff2;
				HPEndPts[ (nStartHouse + 1) % 12 ][1] = nEOff3 + m_nPtImageY / 2;
				HPEndPts[ (nStartHouse + 1) % 12 ][2] = nEOff2 + m_nPtImageX / 2;
				HPEndPts[ (nStartHouse + 1) % 12 ][3] = nEOff3 + m_nPtImageY / 2;
				HPEndPts[ (nStartHouse + 2) % 12 ][0] = nEOff3 + m_nPtImageX / 2;
				HPEndPts[ (nStartHouse + 2) % 12 ][1] = nEOff2 + m_nPtImageY / 2;
				HPEndPts[ (nStartHouse + 2) % 12 ][2] = nEOff3 + m_nPtImageX / 2;
				HPEndPts[ (nStartHouse + 2) % 12 ][3] = nHY - nEOff2 - m_nPtImageY / 2;

				HPEndPts[ (nStartHouse + 3) % 12 ][0] = nHX / 2;
				HPEndPts[ (nStartHouse + 3) % 12 ][1] = nHY / 2 + m_nPtImageY / 2 + nEOff;
				HPEndPts[ (nStartHouse + 3) % 12 ][2] = nHX / 2;
				HPEndPts[ (nStartHouse + 3) % 12 ][3] = r.height - nHY / 2 - m_nPtImageY / 2 - nEOff;
				HPEndPts[ (nStartHouse + 4) % 12 ][0] = nEOff3 + m_nPtImageY / 2;
				HPEndPts[ (nStartHouse + 4) % 12 ][1] = nHY + nEOff2;
				HPEndPts[ (nStartHouse + 4) % 12 ][2] = nEOff3 + m_nPtImageY / 2;
				HPEndPts[ (nStartHouse + 4) % 12 ][3] = r.height - nEOff2 - m_nPtImageY / 2;
				HPEndPts[ (nStartHouse + 5) % 12 ][0] = nEOff2 + m_nPtImageY / 2;
				HPEndPts[ (nStartHouse + 5) % 12 ][1] = r.height - nEOff3 - m_nPtImageY / 2;
				HPEndPts[ (nStartHouse + 5) % 12 ][2] = nHX - m_nPtImageY / 2 - nEOff2;
				HPEndPts[ (nStartHouse + 5) % 12 ][3] = r.height - nEOff3 - m_nPtImageY / 2;

				HPEndPts[ (nStartHouse + 6) % 12 ][0] = nHX / 2 + m_nPtImageX / 2 + nEOff;
				HPEndPts[ (nStartHouse + 6) % 12 ][1] = nHY + nHY / 2;
				HPEndPts[ (nStartHouse + 6) % 12 ][2] = nHX + nHX / 2 - m_nPtImageX / 2 - nEOff;
				HPEndPts[ (nStartHouse + 6) % 12 ][3] = nHY + nHY / 2;
				HPEndPts[ (nStartHouse + 7) % 12 ][0] = nHX + + m_nPtImageX / 2 + nEOff2;
				HPEndPts[ (nStartHouse + 7) % 12 ][1] = r.height - nEOff3 - m_nPtImageY / 2;
				HPEndPts[ (nStartHouse + 7) % 12 ][2] = r.width - nEOff2 - m_nPtImageX / 2;
				HPEndPts[ (nStartHouse + 7) % 12 ][3] = r.height - nEOff3 - m_nPtImageY / 2;
				HPEndPts[ (nStartHouse + 8) % 12 ][0] = r.width - nEOff3 - m_nPtImageX / 2;
				HPEndPts[ (nStartHouse + 8) % 12 ][1] = r.height - nEOff2 - m_nPtImageY / 2;
				HPEndPts[ (nStartHouse + 8) % 12 ][2] = r.width - nEOff3 - m_nPtImageX / 2;
				HPEndPts[ (nStartHouse + 8) % 12 ][3] = nHY + nEOff2 + m_nPtImageY / 2;

				HPEndPts[ (nStartHouse + 9) % 12 ][0] = nHX + nHX / 2;
				HPEndPts[ (nStartHouse + 9) % 12 ][1] = nHY + nHY / 2 - m_nPtImageY / 2 - nEOff;
				HPEndPts[ (nStartHouse + 9) % 12 ][2] = nHX + nHX / 2;
				HPEndPts[ (nStartHouse + 9) % 12 ][3] = nHY / 2 + m_nPtImageY / 2 + nEOff;
				HPEndPts[ (nStartHouse +10) % 12 ][0] = r.width - nEOff3 - m_nPtImageX / 2;
				HPEndPts[ (nStartHouse +10) % 12 ][1] = nHY - nEOff2 - m_nPtImageY / 2;
				HPEndPts[ (nStartHouse +10) % 12 ][2] = r.width - nEOff3 - m_nPtImageX / 2;
				HPEndPts[ (nStartHouse +10) % 12 ][3] = nEOff2 + m_nPtImageY / 2;
				HPEndPts[ (nStartHouse +11) % 12 ][0] = r.width - nEOff2 - m_nPtImageX / 2;
				HPEndPts[ (nStartHouse +11) % 12 ][1] = nEOff3 + m_nPtImageY / 2;
				HPEndPts[ (nStartHouse +11) % 12 ][2] = nHX + nEOff2 + m_nPtImageX / 2;
				HPEndPts[ (nStartHouse +11) % 12 ][3] = nEOff3 + m_nPtImageY / 2;
	} // PrepareNorthern()

	// Helper function for PrepareChakra()
	private void SetRayPoints( int nH, int nRad, int nERad1, int nAng1, int nERad2, int nAng2, int nSX, int nSY, int Pts[][] )
	{
		// Get opposite house
		int nH2 = (nH + 6) % 12;
		int nXStart, nXEnd, nYStart, nYEnd;
		double dAng1 = amath.Rad( nAng1 );
		double dAng2 = amath.Rad( nAng2 );

		// Calculate offset for start
		nXStart = (int)Math.round( nERad1 * Math.cos( dAng1 ) );
		nYStart = (int)Math.round( nERad1 * Math.sin( dAng1 ) );

		// Calculate offset for end
		nXEnd = (int)Math.round( nERad2 * Math.cos( dAng2 ) );
		nYEnd = (int)Math.round( nERad2 * Math.sin( dAng2 ) );

		Pts[ nH ][0] = nRad + nSX * nXStart;
		Pts[ nH ][1] = nRad + nSY * nYStart;
		Pts[ nH ][2] = nRad + nSX * nXEnd;
		Pts[ nH ][3] = nRad + nSY * nYEnd;

		Pts[ nH2 ][0] = nRad - nSX * nXStart;
		Pts[ nH2 ][1] = nRad - nSY * nYStart;
		Pts[ nH2 ][2] = nRad - nSX * nXEnd;
		Pts[ nH2 ][3] = nRad - nSY * nYEnd;

	} // SetRayPoints()

	private void PrepareChakra( Graphics g, boolean bPrint, int nStartHouse, int nXOff, int nYOff, Rectangle r, int HPEndPts[][] )
	{
				int nRad = Math.min( r.width / 2, r.height / 2 );
				int nSgn = Math.max( m_nSgnImageX, m_nSgnImageY );
				int nPt = Math.max( m_nPtImageX, m_nPtImageY );
				// Draw outer ring, radius = nRad
				g.drawOval( nXOff, nYOff, nRad * 2, nRad * 2 );
				// Draw inner ring, r = nRad - nSgn
				g.drawOval( nXOff + nSgn, nYOff + nSgn, 2 * (nRad - nSgn), 2 * (nRad - nSgn) );
				// Draw spokes all the way through.  Later we'll blast in the cutout.
				g.drawLine( nXOff, nYOff + nRad, nXOff + 2 * nRad, nYOff + nRad );
				g.drawLine( nXOff + nRad, nYOff, nXOff + nRad, nYOff + 2 * nRad );
				// For intermediate spokes, get x and y offsets from center for 30, 60 degrees up from horizon
				int nX30, nX60, nY30, nY60;
				nX30 = (int)Math.round( nRad * Math.cos( amath.Rad( 30 ) ) );
				nY30 = (int)Math.round( nRad * Math.sin( amath.Rad( 30 ) ) );
				g.drawLine( nXOff + nRad - nX30, nYOff + nRad - nY30, nXOff + nRad + nX30, nYOff + nRad + nY30 );
				g.drawLine( nXOff + nRad + nX30, nYOff + nRad - nY30, nXOff + nRad - nX30, nYOff + nRad + nY30 );
				nX60 = (int)Math.round( nRad * Math.cos( amath.Rad( 60 ) ) );
				nY60 = (int)Math.round( nRad * Math.sin( amath.Rad( 60 ) ) );
				g.drawLine( nXOff + nRad - nX60, nYOff + nRad - nY60, nXOff + nRad + nX60, nYOff + nRad + nY60 );
				g.drawLine( nXOff + nRad + nX60, nYOff + nRad - nY60, nXOff + nRad - nX60, nYOff + nRad + nY60 );
				// Draw cutout ring, which should have 1/4
				// the diameter of the outer ring.  r = nRad / 4
				g.fillOval( nXOff + 3 * nRad / 4, nYOff + 3 * nRad / 4, nRad / 2, nRad / 2 );
				// Clear inside of cutout ring
				Color prev = g.getColor();
				g.setColor( Color.white );
				g.fillOval( nXOff + 3 * nRad / 4 + 2, nYOff + 3 * nRad / 4 + 2, nRad / 2 - 4, nRad / 2 - 4 );
				g.setColor( prev );

				// Debugging: Draw center "dot"
				g.drawLine( nXOff + nRad, nYOff + nRad, nXOff + nRad, nYOff + nRad );

				// Put signs between inner and outer rings
				if (m_SgnImages[ 0 ] != null) {

					// Get 15, 45, and 75 degree positions
					int nSRad = nRad - nSgn / 2;
					int nX5, nY5;

					// Calculate 15 degree offsets
					nX5 = (int)Math.round( nSRad * Math.cos( amath.Rad( 15 ) ) );
					nY5 = (int)Math.round( nSRad * Math.sin( amath.Rad( 15 ) ) );

					// Debugging: draw bounding box
					//g.drawLine( nXOff + nRad - nX5, nYOff + nRad - nY5, nXOff + nRad + nX5, nYOff + nRad - nY5 );
					//g.drawLine( nXOff + nRad + nX5, nYOff + nRad - nY5, nXOff + nRad + nX5, nYOff + nRad + nY5 );
					//g.drawLine( nXOff + nRad + nX5, nYOff + nRad + nY5, nXOff + nRad - nX5, nYOff + nRad + nY5 );
					//g.drawLine( nXOff + nRad - nX5, nYOff + nRad + nY5, nXOff + nRad - nX5, nYOff + nRad - nY5 );

					// Temporarily offset to compensate for center of image
					nXOff -= (nSgn / 2);
					nYOff -= (nSgn / 2);

					// Draw houses 1, 6, 7, and 12 at 15 degrees
					g.drawImage( m_SgnImages[ (nStartHouse +  0) % 12 ], nXOff + nRad - nX5, nYOff + nRad + nY5, null );
					g.drawImage( m_SgnImages[ (nStartHouse +  5) % 12 ], nXOff + nRad + nX5, nYOff + nRad + nY5, null );
					g.drawImage( m_SgnImages[ (nStartHouse +  6) % 12 ], nXOff + nRad + nX5, nYOff + nRad - nY5, null );
					g.drawImage( m_SgnImages[ (nStartHouse + 11) % 12 ], nXOff + nRad - nX5, nYOff + nRad - nY5, null );

					// Calculate 45 degree offsets
					nX5 = (int)Math.round( nSRad * Math.cos( amath.Rad( 45 ) ) );
					nY5 = (int)Math.round( nSRad * Math.sin( amath.Rad( 45 ) ) );

					// Draw houses 2, 5, 8, and 11 at 45 degrees
					g.drawImage( m_SgnImages[ (nStartHouse +  1) % 12 ], nXOff + nRad - nX5, nYOff + nRad + nY5, null );
					g.drawImage( m_SgnImages[ (nStartHouse +  4) % 12 ], nXOff + nRad + nX5, nYOff + nRad + nY5, null );
					g.drawImage( m_SgnImages[ (nStartHouse +  7) % 12 ], nXOff + nRad + nX5, nYOff + nRad - nY5, null );
					g.drawImage( m_SgnImages[ (nStartHouse + 10) % 12 ], nXOff + nRad - nX5, nYOff + nRad - nY5, null );

					// Calculate 75 degree offsets
					nX5 = (int)Math.round( nSRad * Math.cos( amath.Rad( 75 ) ) );
					nY5 = (int)Math.round( nSRad * Math.sin( amath.Rad( 75 ) ) );

					// Draw houses 3, 4, 9, and 10 at 75 degrees
					g.drawImage( m_SgnImages[ (nStartHouse +  2) % 12 ], nXOff + nRad - nX5, nYOff + nRad + nY5, null );
					g.drawImage( m_SgnImages[ (nStartHouse +  3) % 12 ], nXOff + nRad + nX5, nYOff + nRad + nY5, null );
					g.drawImage( m_SgnImages[ (nStartHouse +  8) % 12 ], nXOff + nRad + nX5, nYOff + nRad - nY5, null );
					g.drawImage( m_SgnImages[ (nStartHouse +  9) % 12 ], nXOff + nRad - nX5, nYOff + nRad - nY5, null );

					// Restore offsets
					nXOff += (nSgn / 2);
					nYOff += (nSgn / 2);

				} // Signs have been loaded

				// Now set house end-points.  Note that nXOff and nYOff are added in
				// later along with compensation for image centering

				// For each house, go from end of ray at 4 degrees from start to end of ray at 4 degrees
				// from end.  Length of starting ray is nRad - nSgn - nPt / 2.
				// Length of ending ray is nRad / 3.  Get offsets of ray endpoints from center.
				int nERad1 = nRad - nSgn - nPt / 2;
				int nERad2 = nRad / 3;

				// Set points for specified house and opposite
				SetRayPoints( (nStartHouse +  0) % 12, nRad, nERad1,  4, nERad2, 26, -1,  1, HPEndPts );
				SetRayPoints( (nStartHouse +  5) % 12, nRad, nERad1, 26, nERad2,  4,  1,  1, HPEndPts );

				// Calculate 34 degree offset for start
				SetRayPoints( (nStartHouse +  1) % 12, nRad, nERad1, 34, nERad2, 56, -1,  1, HPEndPts );
				SetRayPoints( (nStartHouse +  4) % 12, nRad, nERad1, 56, nERad2, 34,  1,  1, HPEndPts );

				// Calculate 64 degree offset for start
				SetRayPoints( (nStartHouse +  2) % 12, nRad, nERad1, 64, nERad2, 86, -1,  1, HPEndPts );
				SetRayPoints( (nStartHouse +  3) % 12, nRad, nERad1, 86, nERad2, 64,  1,  1, HPEndPts );

	} // PrepareChakra()

	// Draw chart background and fill in endpoints
	private void PrepareChart( Graphics g, boolean bPrint, int nStyle, int nStartHouse, int nXOff, int nYOff, Rectangle r, int HPEndPts[][] )
	{
			// Draw actual chart
			if (nStyle == 0) {
				PrepareSouthern( g, bPrint, nStartHouse, nXOff, nYOff, r, HPEndPts );
			} // Southern
			else if (nStyle == 1) {
				PrepareNorthern( g, bPrint, nStartHouse, nXOff, nYOff, r, HPEndPts );
			} // Northern
			else if (nStyle == 2) {
				PrepareChakra( g, bPrint, nStartHouse, nXOff, nYOff, r, HPEndPts );
			} // Chakra

	} // PrepareChart()

	// Display general strength information
	protected void ShowGeneralStrength( Graphics g, boolean bPrint ) {

		// Display
		int nVSpace = 24;
		int nVPos = 10;
		int nHPos = 10;
		int i;
		int nYText = g.getFontMetrics().getHeight();
		int nHPosDelta1 = 26;
		int nHPosDelta2 = 52;
		int nHPosWid = 60;
		int nDivisions[] = {
			 1,  3,  4,  7,
			 9, 10, 12, 16,
			20, 24,	27, 30,
			40, 45, 60
		};
		int nMaxDivisions = 8;
		int nMaxPerLine = 4;
		int nDiv, nDivCount;

		// Maximum y value
		// Set limit according to window boundaries
		Dimension PanelSize = m_ChartPanel.size();
		int nVExtent = PanelSize.height - 4;

		// Clear it
		g.clearRect( 0, 0, PanelSize.width, nVExtent );
		// Call using GetOwner: GetOwnerRelString( m_APoints[ m_APoints[n].GetOwner( nDiv ) ], nDiv )

		g.drawString( "Planet, position, house owner relationship", nHPos, nVPos );
		for (nDiv = DivIndex( m_nDivision ), nDivCount = 0; nDivCount < nMaxDivisions; nDivCount++, nDiv = (nDiv + 1) % MAX_DIVISIONS) {
			if (nDivCount % nMaxPerLine == 0) {
				nVPos += nYText;
			} // Increment line
			g.drawString( new Integer( m_DivisionCycle[nDiv] ).toString(), nHPos + nHPosDelta1 + nHPosWid * (nDivCount % nMaxPerLine), nVPos );
		} // for all divisions

		for (i = 0; i < amath_const.MAX_POINTS; i++) {
			int j = m_PGrade[ i ];
			if (j == amath_const.PT_LAGNA || j >= amath_const.MAX_PLANETS) continue; // Skip Lagna, Rahu and Ketu
			g.drawString( m_aPointAbbr[ j ], nHPos, nVPos + nYText );
			for (nDiv = DivIndex( m_nDivision ), nDivCount = 0; nDivCount < nMaxDivisions; nDivCount++, nDiv = (nDiv + 1) % MAX_DIVISIONS) {
				if (nDivCount % nMaxPerLine == 0) {
					nVPos += nYText;
				} // Increment line
				g.drawString( m_Points[ j ].GetPositionString( m_DivisionCycle[nDiv] ), nHPos + nHPosDelta1 + nHPosWid * (nDivCount % nMaxPerLine), nVPos );
				g.drawString( m_Points[ j ].GetOwnerRelString( m_Points[ m_Points[ j ].GetOwner( m_DivisionCycle[nDiv] ) ], m_DivisionCycle[nDiv] ), nHPos + nHPosDelta2 + nHPosWid * (nDivCount % nMaxPerLine), nVPos );
			} // for all divisions
		} // for all points

	} // ShowGeneralStrength()

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

		// Initialize to level 2 (bhukti)
		m_pDasa = new DasaSet( dBirth, dStartMahadasa,
			dStartMahadasa + Math.round( 365.25 * 120 ),
			nak.GetRulerNum(), 0, 2 );

		System.out.println( "local birth jd = " + dBirth + " date: " + m_pDasa.FmtDateTest( dBirth ) + " date + 10d: " + m_pDasa.FmtDateTest( dBirth + 10.0 ) );

		// Layout the "buttons"
		Dimension d = m_ChartPanel.size();
		m_DasaUpBtn = new Rectangle( d.width - m_nDasaBtnRight - m_nDasaBtnWidth,	// X
									 0 + m_nDasaUpTop,	// Y
									 m_nDasaBtnWidth,	// width
									 m_nDasaBtnHeight ); // height
		m_DasaDnBtn = new Rectangle( d.width - m_nDasaBtnRight - m_nDasaBtnWidth,	// X
									 d.height - m_nDasaDnBot - m_nDasaBtnHeight,	// Y
									 m_nDasaBtnWidth,	// width
									 m_nDasaBtnHeight ); // height

	} // RecalcDasa()

	// Draw dasa page
	private void ShowDasa( Graphics g, boolean bPrint )
	{
		// Do we need to calculate?
		if (m_bDasaDirty) RecalcDasa();

		// Display
		int nVSpace = 24;
		int nVPos = 10;
		int nHPos = 10;
		int nHPos2 = 42;
		// Maximum y value
		// Set limit according to window boundaries
		Dimension PanelSize = m_ChartPanel.size();
		int nVExtent = PanelSize.height - 4;

		// Clear it
		g.clearRect( 0, 0, PanelSize.width, nVExtent );

		if (m_pDasa == null) return;

		// m_nDasaSkipLines is the number of lines we should skip
		// m_nDasaLinesRemain is non-zero if we didn't finish displaying
		m_nDasaLinesRemain = 0;
		m_nDasaSkip = m_nDasaSkipLines; // Temporary countdown

		DasaSet dasaRoot = m_pDasa, dasa;
		if (m_nDasaSkip > 0) {
			m_nDasaSkip--;
		} // Scrolled down
		else {
			// For level 0 show starting date (birth)
			g.drawString( " - " + dasaRoot.GetFmtBirth(), nHPos2, nVPos );
			nVPos += (nVSpace / 2);
		} // Display first line

		dasaRoot.SetHotSpot( 0, 0, 1, 1, nVExtent );

		nVPos = ShowDasaSub( g, dasaRoot, nVPos, nVSpace, nHPos, nHPos2, nVExtent );

		// Draw up and down buttons
		g.setColor( Color.lightGray );
		g.fill3DRect( m_DasaUpBtn.x, m_DasaUpBtn.y, m_DasaUpBtn.width, m_DasaUpBtn.height, true ); // Raised
		g.fill3DRect( m_DasaDnBtn.x, m_DasaDnBtn.y, m_DasaDnBtn.width, m_DasaDnBtn.height, true ); // Raised
		g.setColor( Color.black );
		g.drawString( "Up", m_DasaUpBtn.x + 2, m_DasaUpBtn.y + m_DasaUpBtn.height - 2 );
		g.drawString( "Dn", m_DasaDnBtn.x + 2, m_DasaDnBtn.y + m_DasaDnBtn.height - 2 );

	} // ShowDasa()

	// Recursive subroutine for dasa display.
	// Returns new vertical position.
	protected int ShowDasaSub( Graphics g, DasaSet dasaRoot, int nVPos, int nVSpace, int nHPos, int nHPos2, int nVExtent ) {

		DasaSet dasa;
		int nLastVPos;
		int nCount = 0;

		for (dasa = dasaRoot.GetFirst(); dasa != null; dasa = dasaRoot.GetNext(), nCount++) {
			// Visible: GetStart(), GetEnd(), GetYears(),
			//	GetFmtStart(), GetFmtEnd(), GetRuler(),
			//	GetRulerAbbr(), GetFmtBirth()

			// Ignore counter for level 0
			if (dasaRoot.GetLevel() == 0) nCount--;

			if (nVPos > nVExtent) {
				m_nDasaLinesRemain++;
				break;
			}

			// If scrolled down, don't display
			if (m_nDasaSkip > 0) {
				m_nDasaSkip--;
				nLastVPos = nVPos;
			} // Skip this
			else {

				// Handle open ones
				String s;

				s = dasa.GetRulerAbbr();
				s += " ";
				if (dasa.IsOpen()) {
					s += "-";
				} // Already open
				else {
					s += "+";
				} // Not open
				nLastVPos = nVPos;
				g.drawString( s, nHPos, nVPos );
				if (!dasa.IsOpen()) g.drawString( "|", nHPos2, nVPos );
				nVPos += (nVSpace / 2);

			} // Not skipping

			if (dasa.IsOpen()) {
				// Draw children
				nVPos = ShowDasaSub( g, dasa, nVPos, nVSpace, nHPos2 - 8, nHPos2 - 8 + nHPos2 - nHPos, nVExtent );
				if (m_nDasaSkip > 0) {
					m_nDasaSkip--;
					dasa.SetHotSpot( 0, 0, 0, 0, 0 );
					continue;
				} // Still skipping
				else {
					// Set position of + or - to click on - note that text is drawn bottom-justified
					// This one needs to reflect the range of all children
					dasa.SetHotSpot( nHPos, nHPos2, Math.min( 1, nLastVPos - nVSpace + 2 ), nLastVPos,
						nVPos - (nVSpace / 2) );
					// Now draw end
					if (nCount < 8) {
						if (nVPos <= nVExtent) {
							g.drawString( "|- " + dasa.GetFmtEnd(), nHPos2, nVPos );
							nVPos += (nVSpace / 2);
						}
						else {
							m_nDasaLinesRemain++;
							// Don't break here - exit loop normally so dasa points
							// to next one.  Otherwise our hot spot gets blown away.
						}
					} // not last one
				} // Not skipping
			} // Draw children
			else if (m_nDasaSkip > 0) {
				m_nDasaSkip--;
				dasa.SetHotSpot( 0, 0, 0, 0, 0 );
				continue;
			} // Skip closed line
			else {
				if (nCount < 8)
					g.drawString( "|- " + dasa.GetFmtEnd(), nHPos2, nVPos );
				// Set position of + or - to click on - note that text is drawn bottom-justified
				dasa.SetHotSpot( nHPos, nHPos2, Math.min( 1, nLastVPos - nVSpace + 2 ), nLastVPos,
					nLastVPos );
				if (nCount < 8)
					nVPos += (nVSpace / 2);
			} // Draw closed

			// For debugging:
			//g.drawRect( nHPos, nLastVPos - nVSpace + 2, nHPos2 - nHPos, nVSpace - 2 );
			//g.drawRect( nHPos2, nLastVPos, 2, nVPos - nLastVPos );
		} // Level 0

		// Set hotspots of undisplayed dasas to 0
		for ( ; dasa != null; dasa = dasaRoot.GetNext()) {
			dasa.SetHotSpot( 0, 0, 0, 0, 0 );
		} // for remainder (undisplayed)

		return nVPos;

	} // ShowDasaSub()

	// Draw houses
	public void paint_common( Graphics g, boolean bPrint, Dimension d )
	{
		Rectangle r;

		// The Graphics context passed from the child component should draw
		// everything at the correct x and y offset.

		r = new Rectangle( 0, 0, d.width, d.height );
		if (!bPrint) {
			g.clearRect(r.x, r.y, r.width, r.height);
		} // Not printing
		if (m_bResizing) {
			g.drawRect( r.x, r.y, r.width, r.height );
			g.drawRect( r.width - 5, r.height - 5, 4, 4 );
			return;
		} // Resizing

		if (m_fAllLoaded)
		{
			if (m_am == null || m_nCalculated == 0) {

				g.drawString("Calculating...  ", 10, 20);
				if (!m_fStandAlone)
				{
					g.drawString("Codebase = " + getCodeBase().toString(), 10, 40);
				}
				return;

			} // Defer instantiation

			//g.drawRect( r.width - 5, r.height - 5, 4, 4 );
			m_nDragX1 = r.width - 5;
			m_nDragX2 = r.width;
			m_nDragY1 = r.height - 5;
			m_nDragY2 = r.height;
			int nXOff = 12, nYOff = 4 + g.getFontMetrics().getHeight();
			//int nNakOff = g.getFontMetrics().stringWidth( "Ww: 88Ww88  " );
			//r.width -= (g.getFontMetrics().stringWidth( "27: Purvashadha, P5 " ) + nXOff + 4 + nNakOff);
			// Leave space for upper caption
			r.height -= (nYOff + 4);
			r.width -= (nXOff + 4);
			//r.grow( -80, -10 );
			//r.move( 0, 0 );

			// Handle other pages
			if (m_Page == PG_Dasa) {
				ShowDasa( g, bPrint );
				return;
			} // Dasa page

			if (m_Page == PG_Strength) {
				ShowGeneralStrength( g, bPrint );
				return;
			} // General strength page

			// Array of house planet endpoints.
			// If a line is drawn from point A to point B, this
			// determines the center of each image.
			// We have two pairs of coordinates.  Note that the
			// ordering of the pairs is significant - this determines
			// whether we go from left to right, right to left, top
			// to bottom, etc.
			int HPEndPts[][] = {
				{0,0,0,0},	{0,0,0,0},	{0,0,0,0},	{0,0,0,0},
				{0,0,0,0},	{0,0,0,0},	{0,0,0,0},	{0,0,0,0},
				{0,0,0,0},	{0,0,0,0},	{0,0,0,0},	{0,0,0,0}
			};

			// Each house has a starting sign offset,
			// a text point and alignment (left = 0, center = 1, right = 2),
			// and a planet list start and end point (direction
			// is significant).
			// The Southern style will always have variable house
			// positions, whereas the Northern style will always have
			// variable sign offsets.
			//displayImage(g);

			// Display top caption
			g.drawString( "Style: " + StyleName(), nXOff, nYOff - 2 );

			debugOut( "Style: " + m_Style + " (" + m_nStyle + ")" );

			// Determine starting sign (not used for all drawing styles)
			debugOut( "Getting sign for pt " + amath_const.PT_LAGNA );
			int nStartHouse;
			if (m_nDivision == APoint.MOON_VARGA) {
				nStartHouse = m_Points[ amath_const.PT_MOON ].GetSign( m_nDivision );
			} // Candra
			else {
				nStartHouse = m_Points[ amath_const.PT_LAGNA ].GetSign( m_nDivision );
			} // All others
			debugOut( "Drawing starts from hs " + nStartHouse );

			// Draw background and fill in endpoints
			PrepareChart( g, bPrint, m_nStyle, nStartHouse, nXOff, nYOff, r, HPEndPts );

			debugOut( "Drawing points" );
			//debugOut( "Codebase = " + getCodeBase().toString() + " docbase = " + getDocumentBase().toString() );

			// Draw points in signs
			int i;
			for (i = 0; i < 12; i++) {
				// Go through all planets
				int iX1, iY1, iX2, iY2;
				iX1 = HPEndPts[i][0] + nXOff - m_nPtImageX / 2;
				iY1 = HPEndPts[i][1] + nYOff - m_nPtImageY / 2;
				iX2 = HPEndPts[i][2] + nXOff - m_nPtImageX / 2;
				iY2 = HPEndPts[i][3] + nYOff - m_nPtImageY / 2;

				// Debugging only
				//g.drawLine( iX1, iY1, iX2, iY2 );
				//Color c = g.getColor();
				//g.setColor( Color.red );
				//// Paint destination point red
				//g.drawLine( iX2, iY2, iX2 + 2, iY2 + 2 );
				//g.setColor( c );

				// Make two passes through planets.  First
				// determine total number in this house so
				// we can space them evenly.
				int nInSign = 0;
				int iSer, iPt;
				for (iPt = 0; iPt < amath_const.MAX_POINTS; iPt++) {
					// Order is not important here, we're just tallying
					int iSign = (int)m_Points[ iPt ].GetSign( m_nDivision );
					if (iSign == i) nInSign++;
				} // for all points

				if (nInSign == 0) continue;

				// FIXME determine a perpendicular to direction of
				// travel and start a second row if it gets crowded.
				int iXInc = (iX2 - iX1) / nInSign;
				int iYInc = (iY2 - iY1) / nInSign;
				iSer = 0;
				for (iPt = 0; iPt < amath_const.MAX_POINTS; iPt++) {
					int iSign = (int)m_Points[ m_PGrade[ iPt ] ].GetSign( m_nDivision );
					if (iSign != i) continue;
					if (m_PtImages[ m_PGrade[ iPt ] ] != null) g.drawImage( m_PtImages[ m_PGrade[ iPt ] ], iX1 + iXInc * iSer, iY1 + iYInc * iSer, null );
					iSer++;
				} // for all points
			} // for all signs
		}
		else
			g.drawString("Loading images...", 10, 20);

	}

	//		The start() method is called when the page containing the applet
	// first appears on the screen. The AppletWizard's initial implementation
	// of this method starts execution of the applet's thread.
	//--------------------------------------------------------------------------
	public void start()
	{
		debugOut( "AChart.start() called in thread: " + Thread.currentThread().toString() );
		/***
		Non-threaded
		// This used to be invoked by thread
		run();
		****/
		m_AChart = new Thread(this);
		debugOut( "Created new thread: " + m_AChart.toString() );
		m_AChart.start();
		// TODO: Place additional applet start code here
	}

	//		The stop() method is called when the page containing the applet is
	// no longer on the screen. The AppletWizard's initial implementation of
	// this method stops execution of the applet's thread.
	//--------------------------------------------------------------------------
	public void stop()
	{
		debugOut( "AChart.stop() called in thread: " + Thread.currentThread().toString() );
		if (m_AChart != null)
		{
			// 15-dec-2011 - Thread.stop() is deprecated and inherently unsafe
			// We're going down anyway so just mark it as not present
			//m_AChart.stop();
			debugOut( "Marking thread as null: " + m_AChart.toString() );
			m_AChart = null;
		}

		// TODO: Place additional applet stop code here
	}

	// Build path to indexed image
	private String ImagePath( String szType, int nIndex )
	{
		String szRet;
		// FIXME Handle different image sets
		szRet = "images/" + szType;
		if (!m_fStandAlone) {
			szRet = getCodeBase().toString() + szRet;
		}
		if (nIndex < 10) szRet += "0";
		szRet += nIndex;
		szRet += ".gif";
		return szRet;
	}

	// Get image
	private Image LoadImage( String szType, int nIndex )
	{
		String szRet;
		szRet = "images/" + szType;
		if (nIndex < 10) szRet += "0";
		szRet += nIndex;
		szRet += ".gif";
		Image imgRet = null;
		if (m_fStandAlone) {
			imgRet = Toolkit.getDefaultToolkit().getImage( szRet );
			if (imgRet == null) {
				debugOut( "Unable to get image " + szRet + ", doc base = " + getDocumentBase() );
			} // Bad news
		}
		else {
			// This is really hokey, but getDocumentBase() doesn't work as expected
			// with Netscape.  This still doesn't make any difference, however.
			URL u = null;
			String s = "init";
			try {
				//String s = getDocumentBase().toString().toLowerCase();
				s = getCodeBase().toString().toLowerCase();
				//debugOut( "Got codebase: " + s );
				// If there's an HTML in there we need to doctor it
				int n = s.lastIndexOf( ".html" );
				if (n > 0) {
					n = s.lastIndexOf( '/' );
					s = s.substring( 0, n );
				} // Chop it
				// Assert trailing slash
				n = s.lastIndexOf( '/' );
				if (n < s.length() - 1) {
					s += "/";
				} // Add it
				u = new URL( s + szRet );
				imgRet = getImage( u );
				if (imgRet == null) {
					debugOut( "Failed to load image " + u.toString() );
				}
				/***************
				u = new URL( szRet );
				imgRet = getImage( u );
				if (imgRet == null)
				{
					debugOut( "Failed to load image " + szRet );
				}
				************/
			}
			catch (Exception e) {
				debugOut( "Exception creating URL " + szRet + " s=" + s + " - " + e.toString() + " u=" + u );
			}
		}
		return imgRet;
	}

//	public URL getDocumentBase()
//	{
//		if (m_fStandAlone) {
//			URL u;
//			try {
//				u = new URL( "./" );
//			}
//			catch (Exception e)
//			{
//				return null;
//			}
//			return u;
//		}
//		else {
//			return Applet.getDocumentBase();
//		} // Delegate to base class
//	}

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

	private void Calculate()
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

		debugOut( "Calculating moon" );

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

	// IE 3.02 doesn't repaint all contained panels
	public void MyRepaint()
	{
		if (m_ChartPanel != null) m_ChartPanel.repaint();
		if (m_TextPanel != null) m_TextPanel.repaint();
		if (m_CaptionPanel != null) m_CaptionPanel.repaint();
	} // MyRepaint()

	public void MyRepaint( int nMask )
	{
		// Mask is 0x01 for chart, 0x02 for text, 0x04 for caption
		if (m_ChartPanel != null && (nMask & 0x01) != 0) m_ChartPanel.repaint();
		if (m_TextPanel != null && (nMask & 0x02) != 0) m_TextPanel.repaint();
		if (m_CaptionPanel != null && (nMask & 0x04) != 0) m_CaptionPanel.repaint();
	} // MyRepaint()

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
		if (!m_fTextOnly)
		{
			// Redisplay
			MyRepaint();
		}
	} // Recalc

	// Javascript-accessible function - turn Javascript output via jsj_Output() on or off
	public void SetJSOutput( int on )
	{
	    if (m_fStandAlone)
	    {
			debugOut( "Ignoring JS debug output setting (standalone app)" );
			return;
	    }
		if (on!=0)
		{
			m_jsDebugOutput = on;
			debugOut( "Setting JS debug output to " + on );
		}
		else if (m_jsDebugOutput!=0)
		{
			debugOut( "Setting JS debug output to " + on + " (was:" + m_jsDebugOutput + ")" );
		}
		m_jsDebugOutput = on;
	} // SetJSOutput()

	// THREAD SUPPORT
	//		The run() method is called when the applet's thread is started. If
	// your applet performs any ongoing activities without waiting for user
	// input, the code for implementing that behavior typically goes here. For
	// example, for an applet that performs animation, the run() method controls
	// the display of images.
	//--------------------------------------------------------------------------
	public void run()
	{
		Thread  me  =  Thread.currentThread();
		debugOut( "AChart.run() (thread) starting: " + me.toString() );

		// Complete second stage of init()
		if (m_initStage < 2)
		try {
			debugOut( "m_initStage=" + m_initStage );
			init2();
		}
		catch (Exception e)
		{
			debugOut( "run.e1=" + e.toString() );
		}

		// Show the "Loading images" message
		debugOut( "Waiting for images to finish loading" );
		try {
			MyRepaint();
		}
		catch (Exception e)
		{
			debugOut( "run.e2=" + e.toString() );
		}

        try  {
            m_Tracker.waitForID(0);
	        m_Tracker.waitForID(1);
        }
		catch  (InterruptedException  e)  {
			debugOut( "Got other exception: " + e.toString() );
		        return;
        	}
		catch (NullPointerException ne) {
			debugOut( "Got null pointer exception: " + ne.toString() );
			return;
		}

		// Get maximum sizes
		try {
			for (int i = 0; i < amath_const.MAX_POINTS; i++) {
				m_nPtImageX = Math.max( m_PtImages[i].getWidth( this ), m_nPtImageX );
				m_nPtImageY = Math.max( m_PtImages[i].getHeight( this ), m_nPtImageY );
			} // for all points

			for (int i = 0; i < 12; i++) {
				m_nSgnImageX = Math.max( m_SgnImages[i].getWidth( this ), m_nSgnImageX );
				m_nSgnImageY = Math.max( m_SgnImages[i].getHeight( this ), m_nSgnImageY );
			} // for all signs
		}
		catch (Exception e)
		{
			debugOut( "run.e3=" + e.toString() );
		}

		m_fAllLoaded = true;
		debugOut( "Load complete; max pt x,y=" + m_nPtImageX + "," + m_nPtImageY + "; max sgn x,y=" + m_nSgnImageX + "," + m_nSgnImageY );

		int sleepCount = 0;

        while  (m_AChart ==  me)  {

			try  {
				if (m_nRecalc > 0) {
					// Get current date/time
					m_Time = CurTime();
					m_Date = CurDate();
					// m_TZ is already set
					//debugOut( "Starting recalc: " + m_nRecalc );
					Recalc();
					sleepCount = 0;
					if (m_AChart != null) {
						Thread.sleep( m_nRecalc * 1000 );
					}
				}
				else if (m_nRecalc < 0) {
					debugOut( "Triggered recalc: " + m_nRecalc );
					m_nRecalc = 0;
					Recalc();
					sleepCount = 0;
				} // Recalc once
				else /*if (m_AChart == me)*/ {
					//if (sleepCount != 0 && sleepCount % 12 == 0) debugOut( "sleepCount=" + sleepCount + " m_nRecalc=" + m_nRecalc );
					Thread.sleep(5000);
					sleepCount++;
				}
			}
			catch  (InterruptedException  e)  {
				debugOut( "Interrupted: " + e.toString() + " thread: " + me.toString() );
				break;
			}
			catch (Error e) {
				// ThreadDeath is a subclass of Error, not Exception
				debugOut( "AChart.run() error: " + e.toString() + " thread: " + me.toString() );
				break;
			}
			catch (Exception e) {
				debugOut( "run.e4=" + e.toString() );
			}

		}

		debugOut( "AChart.run() exiting, thread: " + me.toString() + " m_AChart: " + m_AChart );

	}

	// Given a division, return index within m_DivisionCycle[]
	// return 0 if not found
	protected int DivIndex( int nDiv ) {
		int n;
		for (n = 0; n < MAX_DIVISIONS; n++) {
			if (nDiv == m_DivisionCycle[ n ]) return n;
		} // for all divisions
		return 0; // Default - start with Rasi
	} // DivIndex()

	// MOUSE SUPPORT:
	//		The mouseDown() method is called if the mouse button is pressed
	// while the mouse cursor is over the applet's portion of the screen.
	//--------------------------------------------------------------------------
	public boolean mouseDown(Event evt, int x, int y)
	{
		if (m_bResizing) {
			m_bResizing = false;
			MyRepaint();
			return true;
		} // DIdn't get matching mouseUp()

		if (m_bResizable &&
			x >= m_nDragX1 && x <= m_nDragX2 &&
			y >= m_nDragY1 && y <= m_nDragY2) {
			m_bResizing = true;
			MyRepaint();
			return true;
		} // Starting to resize

		// If inside text box and we're showing dasas, check for a hit
		if (m_Page == PG_Dasa && m_pDasa != null &&
			//m_ChartPanel.inside( x, y ) -- this doesn't work for scroll buttons
			y > m_MenuPanel.size().height &&
			x < m_ChartPanel.size().width
			) {
			// Translate window coordinates to component coordinates
			Dimension d = m_MenuPanel.size();
			y -= d.height;
			// Check for up and down scrolling
			// Note that we always scroll in units of 2
			if (m_DasaUpBtn.inside( x, y )) {
				if (m_nDasaSkipLines > 0) {
					m_nDasaSkipLines -= 2;
					MyRepaint( 0x01 );
				} // Scroll up
				return true;
			} // Scroll up
			else if (m_DasaDnBtn.inside( x, y )) {
				if (m_nDasaLinesRemain > 0) {
					m_nDasaSkipLines += 2;
					MyRepaint( 0x01 );
				} // Scroll down
				return true;
			} // Scroll down
			DasaSet dasa = m_pDasa.FindDasa( x, y );
			if (dasa != null) {
				dasa.ToggleOpen();
				MyRepaint( 0x01 );
				return true;
			} // Found it
		} // Check for hot spot

		// Check for hit inside text panel
		if (y > m_MenuPanel.size().height &&
			x > m_ChartPanel.size().width &&
			x < m_ChartPanel.size().width + m_TextPanel.size().width) {
			// Cycle through displays
			int nDivIndex = DivIndex( m_nDivision );
			nDivIndex = (nDivIndex + 1) % MAX_DIVISIONS;
			m_nDivision = m_DivisionCycle[ nDivIndex ];
			MyRepaint( 0x03 );
			return true;
		} // Inside text panel

		// Check for hit inside chart panel on general strength
		//if (m_Page == PG_Strength &&
		//	y > m_MenuPanel.size().height &&
		//	x < m_ChartPanel.size().width) {
		//} // Hit on chart panel displaying strength

		// Can't grow an applet
		//if (m_bResizable &&
		//	x < m_nDragX1 && x >= m_nDragX1 - 4 &&
		//	y >= m_nDragY1 && y <= m_nDragY2) {
		//	resize( x + 12, y + 12 );
		//	repaint();
		//	return true;
		//} // Grow it

		return false;
	}

	// MOUSE SUPPORT:
	//		The mouseUp() method is called if the mouse button is released
	// while the mouse cursor is over the applet's portion of the screen.
	//--------------------------------------------------------------------------
	public boolean mouseUp(Event evt, int x, int y)
	{
		// TODO: Place applet mouseUp code here
		if (m_bResizing) {
			m_bResizing = false;
			repaint();
		} // End resize
		return true;
	}

	public boolean mouseDrag( Event evt, int x, int y )
	{
		// Resize
		if (m_bResizing) {
			resize( x + 4, y + 4 );
			repaint();
		}
		return true;
	}

    public boolean action(Event evt, Object arg)
    {
		int nOld, nRepaint;
       //if (evt.target instanceof Choice)
       //{
       //   ( (CardLayout) cards.getLayout() ).show(cards,(String) arg );
       //}
       //else
       //{
			nOld = m_nDivision;
			nRepaint = 0;
			String szPage = PG_Chart;

			debugOut( "Event " + evt.toString() + " arg " + arg.toString() );
			// Handle menu changes first
			if (LBL_Main.equals(arg)) {
				ChangeMenu( 0 );
			} // Switch to main
			else if (LBL_Charts.equals(arg)) {
				// Switch to charts buttons
				ChangeMenu( 1 );
			} // Charts
			else if (LBL_Varga1.equals(arg)) {
				ChangeMenu( 2 );
			} // Varga 1-12
			else if (LBL_Varga2.equals(arg)) {
				ChangeMenu( 3 );
			} // Varga 16-60
			else if (LBL_Options.equals(arg)) {
				// Switch to options buttons
				ChangeMenu( 4 );
			} // Options
			else if (LBL_Dasas.equals( arg )) {
				ChangeMenu( 5 );
			} // Vimsottari
			else if (LBL_Output.equals(arg)) {
				ChangeMenu( 6 );
			} // Output
			else if (LBL_Strength.equals( arg )) {
				ChangeMenu( 7 );
			} // Sad-bala
			// Handle commands
			else if (LBL_General.equals(arg)) {
				szPage = PG_Strength;
				nRepaint = 1;
			} // General strengths
			else if (LBL_Vimsottari.equals(arg)) {
				szPage = PG_Dasa;
				nRepaint = 1;
				m_bDasaDirty = true;
				m_nDasaSkipLines = 0; // Reset scrolling
			} // Vimsottari
			else if (LBL_Rasi.equals(arg)) {
				if (nOld != 1) {
					m_nDivision = 1;
					nRepaint = 1;
				} // Switch to Rasi
			} // Rasi chart
			else if (LBL_Drekana.equals(arg)) {
				if (nOld != 3) {
					m_nDivision = 3;
					nRepaint = 1;
				} // Switch to Drekana
			} // Drekana
			else if (LBL_Caturtha.equals(arg)) {
				if (nOld != 4) {
					m_nDivision = 4;
					nRepaint = 1;
				} // Switch to Caturthamsa
			} // Caturthamsa
			else if (LBL_Candra.equals(arg)) {
				if (nOld != APoint.MOON_VARGA) {
					m_nDivision = APoint.MOON_VARGA;
					nRepaint = 1;
					// Grade points according to new scheme
					GradePoints( amath_const.PT_MOON );
				} // Switch to Candra
			} // Moon
			else if (LBL_Saptamsa.equals(arg)) {
				if (nOld != 7) {
					m_nDivision = 7;
					nRepaint = 1;
				} // Switch to Saptamsa
			} // Saptamsa
			else if (LBL_Navamsa.equals(arg)) {
				if (nOld != 9) {
					m_nDivision = 9;
					nRepaint = 1;
				} // Switch to Navamsa
			} // Navamsa
			else if (LBL_Dasamsa.equals(arg)) {
				if (nOld != 10) {
					m_nDivision = 10;
					nRepaint = 1;
				} // Switch to Dasamamsa
			} // Dasamamsa
			else if (LBL_Dvadasamsa.equals(arg)) {
				if (nOld != 12) {
					m_nDivision = 12;
					nRepaint = 1;
				} // Switch to Dvadasamsa
			} // Dvadasamsa
			else if (LBL_Sodasamsa.equals(arg)) {
				if (nOld != 16) {
					m_nDivision = 16;
					nRepaint = 1;
				} // Switch to Sodasamsa
			} // Sodasamsa
			else if (LBL_Vimsamsa.equals(arg)) {
				if (nOld != 20) {
					m_nDivision = 20;
					nRepaint = 1;
				} // Switch to Vimsamsa
			} // Vimsamsa
			else if (LBL_Caturvimsamsa.equals(arg)) {
				if (nOld != 24) {
					m_nDivision = 24;
					nRepaint = 1;
				} // Switch to Caturvimsamsa
			} // Caturvimsamsa or Siddhamsa
			else if (LBL_Bhamsa.equals(arg)) {
				if (nOld != 27) {
					m_nDivision = 27;
					nRepaint = 1;
				} // Switch to Bhamsa
			} // Bhamsa or Naksatramsa
			else if (LBL_Trimsamsa.equals(arg)) {
				if (nOld != 30) {
					m_nDivision = 30;
					nRepaint = 1;
				} // Switch to Trimsamsa
			} // Trimsamsa
			else if (LBL_Khavedamsa.equals(arg)) {
				if (nOld != 40) {
					m_nDivision = 40;
					nRepaint = 1;
				} // Switch to Khavedamsa
			} // Khavedamsa or Catvarimsamsa
			else if (LBL_Aksavedamsa.equals(arg)) {
				if (nOld != 45) {
					m_nDivision = 45;
					nRepaint = 1;
				} // Switch to Aksavedamsa
			} // Aksavedamsa or Panca-catvarimsamsa
			else if (LBL_Sasthyamsa.equals(arg)) {
				if (nOld != 60) {
					m_nDivision = 60;
					nRepaint = 1;
				} // Switch to Sasthyamsa
			} // Sasthyamsa
			else if (LBL_Style.equals(arg)) {
				debugOut( "Switching style..." );
				m_nStyle = (m_nStyle + 1) % 3;
				//Dialog dlg;
				//dlg = new Dialog( m_Frame, "New style: " + m_nStyle, true );
				//dlg.show();
				MyRepaint( 0x01 );
			} // Chart style
			else if (LBL_HTML.equals(arg)) {
				RenderHtml();
			} // Output to html
			else if (LBL_HTML5.equals(arg)) {
				// Uses inline svg
				RenderHtml5();
			} // Output to html5
			else if (LBL_Post.equals(arg)) {
				Post();
			} // Post to URL
			else {
				return false;
			} // all others
		//}

			if (m_Page != szPage) {
				nRepaint = 1;
			} // Force redraw

			m_Page = szPage;
			if (nRepaint != 0) {
				// If switching from candra to anything else,
				// re-grade
				if (nOld == APoint.MOON_VARGA && m_nDivision != APoint.MOON_VARGA) {
					GradePoints( amath_const.PT_LAGNA );
				} // Grade from lagna
				// A simple repaint should suffice but not with all VMs
				MyRepaint();
			} // Repainting

		return true; // Event handled by us

	} // action()

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

