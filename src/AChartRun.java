//******************************************************************************
// AChartRun.java:	Runnable application
//
//******************************************************************************
//package astro;  // the package name maps to a directory

import java.util.*;
import java.net.*;
import java.io.*;
//import astro.*;
import amath_ext2.*;
import amath_base.*;
//import amath_ext1.*;
import generated.*;

// Swiss ephemeris
//import swisseph.*;

//==============================================================================
// Main Class for runnable AChartRun
// Flow of execution (application):
// static main instantiates AChart instance "a" and sets AChartFrame within the instance,
// then calls a.init(), a.start(), and a.m_frame.show()
//==============================================================================
public class AChartRun
{
    // By convention, the major and minor should match what's defined in Makefile
        public final String m_Version = "v1.54.125";

        // All of the core functionality
        private JyotishCore m_core = null;
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


	// Other public members
	public String m_args[];


	// STANDALONE APPLICATION SUPPORT:
	//		m_fStandAlone will be set to true if applet is run standalone
	//		m_fTextOnly true if running without graphics
	//--------------------------------------------------------------------------
        static boolean m_fStandAlone = true;
        static boolean m_fTextOnly = false;

	// STANDALONE APPLICATION SUPPORT
	// 	The main() method acts as the applet's entry point when it is run
	// as a standalone application. It is ignored if the applet is run from
	// within an HTML page.
	//--------------------------------------------------------------------------
	public static void main(String args[])
	{
                AChartRun app_AChart = null;
                m_fStandAlone = true;
                m_fTextOnly = true;
                try {
                    app_AChart = new AChartRun();
                }
                catch (Exception e)
                {
                    System.err.println("Headless exception (constructor)");
                    e.printStackTrace(System.err);
                    return;
                }
		// Partially parse args to determine whether we're running text-only
                app_AChart.m_args = args;

			for (int i = 0; i < args.length; i++) {
				StringTokenizer t = new StringTokenizer( args[ i ], "=", false );
				if (t.countTokens() < 1) continue;
				String s1 = t.nextToken();
                                if (s1.equalsIgnoreCase( "TextOnly" )) { app_AChart.m_fTextOnly = true; }
			} // for all args

                        app_AChart.init();
                        app_AChart.init2();
                        app_AChart.Recalc();
                        System.out.println( "--- chart ---" );
                        app_AChart.RenderHtml5();
                        System.out.println( "--- code ---" );
                        System.out.println( app_AChart.CodedOutput() );
                        System.out.println( "--- dashas ---" );
                        System.out.println( app_AChart.ShowVimsottariDasa() );
                        System.out.println( "--- sadbala ---" );
                        System.out.println( app_AChart.ShowGeneralStrength() );
                        System.out.println( "--- end ---" );
			// We should exit after this since we haven't started any threads
	} // main()

	// AChart Class Constructor
	//--------------------------------------------------------------------------
        public AChartRun()
	{
		m_nPtImageX = 0;
		m_nPtImageY = 0;
		m_nSgnImageX = 0;
		m_nSgnImageY = 0;
		// TODO: Add constructor code here
		//debugOut( "Constructor, V1.0" ); // Can't do that
                m_core = null;
                try {
                    m_core = new JyotishCore();
                }
                catch (Exception e)
                {
                        debugOut( "Exception 1: " + e.toString() );
                }
        }

	// Debug support
	public void debugOut( String szMsg )
	{
		if (m_nDebug != 0) {

			try {
				// This appears on Java console in Netscape (not!)
				System.err.println( szMsg );
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

	} // debugOut()


	// The init() method is called by the AWT when an applet is first loaded or
	// reloaded.  Override this method to perform whatever initialization your
	// applet needs, such as initializing data structures, loading images or
	// fonts, creating frame windows, setting the layout manager, or adding UI
	// components.
    //--------------------------------------------------------------------------
	public void init()
	{
            m_core.init();
	}

	// Second stage of init called within Thread context
	public void init2()
	{
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
                if (s1.equalsIgnoreCase( PARAM_Text )) { m_core.m_Text = s2; continue; }
                if (s1.equalsIgnoreCase( PARAM_Ayanamsa )) { m_core.m_Ayanamsa = s2; continue; }
                if (s1.equalsIgnoreCase( PARAM_Time )) { m_Time = s2; continue; }
                if (s1.equalsIgnoreCase( PARAM_Date )) { m_Date = s2; continue; }
                if (s1.equalsIgnoreCase( PARAM_Lat )) { m_Lat = s2; continue; }
                if (s1.equalsIgnoreCase( PARAM_Long )) { m_Long = s2; continue; }
                if (s1.equalsIgnoreCase( PARAM_TZ )) { m_TZ = s2; continue; }
                if (s1.equalsIgnoreCase( PARAM_DST )) { m_core.m_nDST = amath.atoi( s2 ); continue; }
                if (s1.equalsIgnoreCase( PARAM_Debug )) { m_nDebug = amath.atoi( s2 ); continue; }
                if (s1.equalsIgnoreCase( PARAM_Post )) { m_PostAddr = s2; continue; }
                if (s1.equalsIgnoreCase( PARAM_PostTarget )) { m_PostTarget = s2; continue; }
                if (s1.equalsIgnoreCase( "TextOnly" )) { m_fTextOnly = true; continue; }
                if (s1.equalsIgnoreCase( "HtmlDivs" )) { m_core.m_htmlDivs = s2; continue; }


                // Check for points
                for (int iPoint = amath_const.PT_FIRST; iPoint <= amath_const.PT_LAST; iPoint++) {
                        if (s1.equalsIgnoreCase( m_aPoints[ iPoint ] ) ||
                                s1.equalsIgnoreCase( m_aSPoints[ iPoint ] )) {
                                //Double d;
                                //d = new Double( s2 );
                                //m_Points[ iPoint ] = d.doubleValue();
                                m_core.m_Points[ iPoint ].Parse( s2, iPoint );
                                break;
                        } // Got a match
                } // for all points

        } // for all args
            m_core.init2();
	}

        // Render northern style HTML using HTML5 inline svg
        public void RenderHtml5()
        {
            m_core.RenderHtml5();
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
                m_core.m_Date = m_Date;
                m_core.m_Time = m_Time;
                m_core.m_nYear = amath.atoi( m_Date.substring( 0, 4 ) );
                m_core.m_nMonth= amath.atoi( m_Date.substring( 4, 6 ) );
                m_core.m_nDay  = amath.atoi( m_Date.substring( 6, 8 ) );
		// Time should be HHMM[SS]
                m_core.m_nHour = amath.atoi( m_Time.substring( 0, 2 ) );
                m_core.m_nMinute = amath.atoi( m_Time.substring( 2, 4 ) );
                m_core.m_nSecond = 0;
		if (m_Time.length() == 6) {
                        m_core.m_nSecond = amath.atoi( m_Time.substring( 4, 6 ) );
		}

		// Timezone should be [-]HHMM
		// Convert to signed minutes east of Greenwich
		int nHourLen = m_TZ.length() - 2;
                m_core.m_TZ = m_TZ;
                m_core.m_nTZ = 60 * amath.atoi( m_TZ.substring( 0, nHourLen ) );
                if (m_core.m_nTZ < 0) {
                        m_core.m_nTZ -= amath.atoi( m_TZ.substring( nHourLen ) );
		} // Negative
		else {
                        m_core.m_nTZ += amath.atoi( m_TZ.substring( nHourLen ) );
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


	// Javascript-accessible function - call this from Javascript
	// after setting time, date, lat, long, etc.
	public void Recalc()
	{
		if (TimeDateValid()) {
			// Parse time, date, timezone
			ParseDate();
			// Do one-time calculation
                        m_core.m_Lat = m_Lat;
                        m_core.m_Long = m_Long;
                        m_core.Calculate();
		}
		else
		{
			debugOut( "Time/date not valid, no recalc: " + m_Time + ";" + m_Date + ";" + m_Lat + ";" + m_Long + ";" + m_TZ + ";" );
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


	// Given a division, return index within m_DivisionCycle[]
	// return 0 if not found
	protected int DivIndex( int nDiv ) {
		int n;
		for (n = 0; n < MAX_DIVISIONS; n++) {
			if (nDiv == m_DivisionCycle[ n ]) return n;
		} // for all divisions
		return 0; // Default - start with Rasi
	} // DivIndex()

        // Raw output to html in browser (via exposed Javascript)
        public void browserOut( String html )
        {
                        System.out.println( "--- browserOut() called standalone ---" );
                        System.out.println( html );
        } // browserOut()

    // Reflect coded output
    public String CodedOutput()
    {
        return m_core.m_CodedOutput;
    }

    // Reflect general strength
    // Display general strength information
    public String ShowGeneralStrength() {
        return m_core.ShowGeneralStrength();
    } // ShowGeneralStrength()

    // Reflect vimsottari dasas
    public String ShowVimsottariDasa() {
        return m_core.ShowVimsottariDasa();
    }

}

