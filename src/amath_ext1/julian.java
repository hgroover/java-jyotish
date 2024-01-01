// julian.java - Julian date conversion

package amath_ext1;

import amath_base.amath_rot;
import java.util.*;

public class julian {

	// Days in year preceding first of each month
	// (instance 0 = normal year, instance 1 = leap year)
	private int aiMonths[][] = {
		//	31	28	31	30	31	30	31	31	30	31	30	31
		{	0,	31,	59,	90,	120,151,181,212,243,273,304,334,365 },
		//	31	29	31	30	31	30	31	31	30	31	30	31
		{	0,	31,	60,	91,	121,152,182,213,244,274,305,335,366 }
	};

        // Month abbreviations in English. FIXME handle localization for date formats
        // and for this!!!
        private final String[] aMonthNames_en = {
            "Jan", "Feb", "Mar", "Apr",
            "May", "Jun", "Jul", "Aug",
            "Sep", "Oct", "Nov", "Dec"
        };

	// 400 years = 303 yrs + 97 leap yrs = 110595 + 35502 = 146097
	// 100 years = 76 yrs + 24 leap yrs = 27740 + 8784 = 36524
	// 4 years = 3 yrs + 1 leap yr = 1095 + 366 = 1461
	// 1 year = 365
	public int YR400_DAYS=	146097;
	public int YR100_DAYS=	36524;
	public int YR4_DAYS=	1461;
	public int YR1_DAYS=	365;

	// Given days within year, return month (origin:1) and day
	// nLeap is 1 for leap year
	public int YMonth( long lDay, int nLeap ) {

		int i;

		for (i = 0; i < 12; i++) {
			if (aiMonths[nLeap][i] >= lDay) {
				break;
			} // Found end of target month
		} // for all months

		// Origin:0 index of days preceding next month ==
		// origin:1 index of current month
		return i;

	} // YMonth()

	// Returns 1 if leap year, 0 if not
	static public int
	IsLeap( int nYear ) {
		if (nYear != 0 && (nYear % 4 == 0) && (
			(nYear % 100 != 0) || (nYear % 400 == 0)
			)) {
			return 1;
		} // Leap year
		return 0;
	} // IsLeap()

	// Convert Y, M, D to days since 1/1/1
	public double EpochDay( int nYear, int nMonth, int nDay ) {

		double uVal = nDay;
		int nIsLeap = IsLeap( nYear );
		uVal += aiMonths[nIsLeap][nMonth - 1];
			// Year is origin:1 - make it origin:0 to calculate
			// total days preceding this year.
			// If leap year adjust month
			int iCur = nYear - 1;
			// Add days
			uVal += ((iCur / 400) * YR400_DAYS);
			iCur %= 400;
			uVal += ((iCur / 100) * YR100_DAYS);
			iCur %= 100;
			uVal += ((iCur / 4) * YR4_DAYS);
			iCur %= 4;
			uVal += (iCur * YR1_DAYS);

		return uVal;

	} // EpochDay()

	// Convert Y, M, D to julian day number
	public double JulianDay( int nYear, int nMonth, int nDay ) {
		double Jtemp;

		// Julian epoch begins some time around the start of Kali-yuga
		// AT MIDNIGHT.
	    //Temp = 12.0 * (nYear + 4800.0) + nMonth - 3.0;
	    //Jtemp = (2.0* (Temp - 12.0 * amath._Int( Temp/12.0 )) + 7.0 +
    	//	365.0 * Temp) / 12.0;
		//Jtemp = amath._Int(Jtemp) + nDay + amath._Int(Temp/48.0) - 32083.0;
		//// 2299170 is : start of Julian calendar with leap years
		//if (Jtemp>2299170.0) {
		//	Jtemp = Jtemp + amath._Int(Temp/4800.0) -
      	//			amath._Int(Temp/1200.0) + 38.0;
		//} // if ...

		// 2299170 = 24 October 1582 = day 577745
		// Difference is 1721425

		Jtemp = EpochDay( nYear, nMonth, nDay );
		Jtemp += 1721425;

		return Jtemp;

	} // JulianDay()

	protected String m_Days[] = {
		"Sun",	"Mon",	"Tue",	"Wed",	"Thu",	"Fri",	"Sat"
	};

	// Instance data
	// Set on output from Reverse()
	public int m_nYear, m_nMonth, m_nDay;
	public int m_nHour, m_nMinute, m_nSecond;

	// Split julian value into month, day, year, hour, minute, second
	public void Reverse( double JD ) {

		//int NewLeap;

		//if (JD>2299170.0) {
		//	NewLeap = 1;
		//} else {
		//	NewLeap = 0;
		//}

		//JD = JD + 32083.0;

		// Input: DA# = julian day
		// Output: JT=month, K=day, IK=yyyy, DTO$="<_mm/dd/yy"
		// 26500 'RVSE JUL DAY ROUTN
		// 26510 L = Int(DA# + 0.5) + 68569!: N = Int(4 * L / 146097!): L = L - Int((146097! * N + 3) / 4)
		// 26520 IT = Int(4000 * (L + 1) / 1461000!): L = L - Int(1461 * IT / 4) + 31: JT = Int(80 * L / 2447)
		// 26530 K = L - Int(2447 * JT / 80): L = Int(JT / 11): JT = JT + 2 - 12 * L: IK = 100 * (N - 49) + IT + L
		// 26540 K$ = STR$(K): If VAL(K$) < 10 Then K$ = "0" + RIGHT$(K$, 1)
		// 26550 J$ = "-" + RIGHT$(K$, 2) + "-" + RIGHT$(STR$(IK), 2)
		// 26560 DTO$ = "<" + CHR$(95) + RIGHT$(STR$(JT), 2) + J$: Return

		// Save fractional portion
		// 4 yrs = 1461 days
		// 100 yrs = 36524 days
		// 400 yrs = 146097 days
		// 1000 yrs = 365242 days
		// 10000 yrs = 3652425 days
		//JD += 0.5; // Start from midnight, not noon
		double dHours = 24.0 * amath_rot.Frac( JD ); // Decimal hours within day

		//int nL = amath._Int( JD ) + 68569; // Days starting from 1/1/-48??
		//int nC = amath._Int( 4 * nL / 146097 ); // Centuries, rounded modulo 4
		//nL -= amath._Int( (146097 * nC + 3) / 4 ); // Subtract century value
		//int nIT = amath._Int( 4000 * (nL + 1) / 1461000 ); // ??? adjust for 4 millenia
		//nL = nL - amath._Int( 1461 * nIT / 4 ) + 31; // More millenium rounding ?
		//m_nMonth = amath._Int( 80 * nL / 2447 ); // More magic numbers...
		//m_nDay = nL - amath._Int( 2447 * m_nMonth / 80 );
		//nL = amath._Int( m_nMonth / 11 );
		//m_nMonth += (2 - 12 * nL); // We started in March?
		//m_nYear = 100 * (nC - 49) + nIT + nL;

		// Convert to epoch day
                //double JD_org = JD;
		JD = amath_rot._Int( JD );
		JD -= 1721425;
		m_nYear = 0;
		while (JD > YR400_DAYS) {
			m_nYear += 400;
			JD -= YR400_DAYS;
		}
		while (JD > YR100_DAYS) {
			m_nYear += 100;
			JD -= YR100_DAYS;
		}
		while (JD > YR4_DAYS) {
			m_nYear += 4;
			JD -= YR4_DAYS;
		}
                // Long-hidden bug on next-to last day of a leap year (e.g. 30-Dec-1960):
                // Leap year will be year 4, so 30 Dec JD will be 1460, 31 Dec JD will be 1461
                int yearsInLeapCycle = 0;
                int n;
                for (n = 0; n < 3; n++)
                {
                    if (JD <= YR1_DAYS) break;
                    yearsInLeapCycle++;
                    JD -= YR1_DAYS;
                }
                m_nYear += yearsInLeapCycle;
                // Convert year to origin:1
		m_nYear++;
                // lDays should never be 0
                long lDays = new Double( JD ).longValue();
		int nIsLeap = IsLeap( m_nYear );
		m_nMonth = YMonth( lDays, nIsLeap );
                //System.err.println( "pre-exception: " + m_nMonth + " y=" + m_nYear + " yilc=" + yearsInLeapCycle + " ld=" + lDays + " isleap=" + nIsLeap + " JD=" + JD + " JDO=" + JD_org + " JL=" + ((new Double(JD)).longValue()) + " JLM=" + ((new Double(JD)).longValue() % YR1_DAYS) );
                //try {
		lDays -= aiMonths[nIsLeap][m_nMonth-1];
		m_nDay = (int)lDays;
                //}
                //catch (Exception e)
                //{
                //    Double D = new Double(JD);
                //    System.err.println( "Exception: " + e + " m_nMonth=" + m_nMonth + " y=" + m_nYear + " ld=" + lDays + " isleap=" + nIsLeap + " JDO=" + JD_org + " JL=" + (D.longValue()) + " JLM=" + (D.longValue() % YR1_DAYS) );
                //    return;
                //}


		// Now get hours, minutes, seconds
		m_nHour = amath_rot._Int( dHours );
		dHours -= m_nHour;
		dHours *= 60.0;
		// Avoid rounding problems - add 0.1 second
		dHours += (1.0 / 600.0);
		m_nMinute = amath_rot._Int( dHours );
		dHours -= m_nMinute;
		m_nSecond = amath_rot._Int( dHours * 60.0 );

	} // Reverse()

	// Return day of week, Sun=0
	public int DayOfWk( double JD ) {
		// Our epoch begins on a Monday
		// return (int) Trunc( JD - Trunc( JD / 7.0 ) * 7.0 );
		return (1 + amath_rot._Int( JD - 7.0 * amath_rot._Int( JD / 7.0 ) )) % 7;
	} // DayOfWk

	// Return name of weekday (JDtoDay)
	public String WeekDay( double JD ) {
		return m_Days[ DayOfWk( JD ) ];
	} // WeekDay()

	// Format an integer left zero-padded to specified number of places
	public String ZStr( int n, int nPlaces ) {
		String sz = "00000000";
		sz += n;
		int nLen = sz.length();
		return sz.substring( nLen - nPlaces );
	} // ZStr()

	// Return date as mm/dd/yyyy hh:mm:ss
	// (Reverse must be called first)
	public String FullDate() {
		String sz = "";
		sz += ZStr( m_nMonth, 2 );
		sz += "/";
		sz += ZStr( m_nDay, 2 );
		sz += "/";
		sz += ZStr( m_nYear, 4 );
		sz += " ";
		sz += ZStr( m_nHour, 2 );
		sz += ":";
		sz += ZStr( m_nMinute, 2 );
		sz += ":";
		sz += ZStr( m_nSecond, 2 );
		return sz;
	} // FullDate()

        // Return date only
        public String ShortDate() {
            String sz = "";
            sz += ZStr( m_nDay, 2 );
            sz += '-';
            sz += aMonthNames_en[m_nMonth - 1];
            sz += '-';
            sz += ZStr( m_nYear, 4 );
            return sz;
        } // ShortDate()

} // class julian
