package amath_base;

// Astro constants class
public class amath_const {

	public amath_const() {
		Pconst_ptr = 0;
	}

	// Point indices
	public static final int MAX_POINTS = 10; // 9 Planets plus lagna, others
	public static final int PT_SUN = 0;
	public static final int PT_MOON = 1;
	public static final int PT_LAGNA = 2;
	public static final int PT_BUDHA = 3;
	public static final int PT_SUKRA = 4;
	public static final int PT_KUJA = 5;
	public static final int PT_BRHASPATI = 6;
	public static final int PT_SANI = 7;
	public static final int MAX_PLANETS = 8; // 7 planets plus lagna (not included)
	public static final int PT_RAHU = 8;
	public static final int PT_KETU = 9;
	public static final int PT_FIRST = 0;
	public static final int PT_LAST = 9;

	// Math.PI
	public final static double Modulus360 = 360.0;
	public final static double Semi = 180.0;

//#define Pconst_size 394
	private int Pconst_ptr;
	public void ResetData() { Pconst_ptr = 0; }
	public double NextTerm() { return m_Data[ Pconst_ptr++ ]; }

//static LDOUBLE  Planet_const[Pconst_size] = {
	private double m_Data[] = {
// Sun	/* Mean Anomaly */
	358.4758, 35999.0498, -0.0002,
		///* Eccentricity */
		0.01675, -0.4E-04, 0.0,
			///* Semi-major axis */
			1.0,
				///* Terms c and d (used in Rotate()) */
				101.2208, 1.7192, 0.00045,	0.0, 0.0, 0.0,
					///* Inclination */
					0.0, 0.0, 0.0,
// Mercury
	102.2794, 149472.515, 0.0,
		0.205614, 0.2E-04, 0.0,
			0.3871,
				28.7538, 0.3703, 0.0001,	47.1459, 1.1852, 0.0002,
					7.009, 0.00186, 0.0,
// Venus
	212.6032, 58517.8039, 0.0013,
		0.00682, -0.5E-04, 0.0,
			0.7233,
				54.3842, 0.5082, -0.14E-02,	75.7796, 0.8999, .4E-03,
					3.3936, 0.1E-02, 0.0,
// Mars
	319.5294, 19139.8585, 0.2E-03,
		0.09331, 0.9E-04, 0.0,
			1.5237,
				285.4318, 1.0698, 0.1E-03,	48.7864, 0.77099, 0.0,
					1.8503, -0.7E-03, 0.0,
// Outer planets have additional terms (harmonics)
// Jupiter: Harm=11 /* Mean Anomaly */
		///* Eccentricity */
			///* Semi-major axis */
				///* Arg of perihelion and ascending node (used in Rotate()) */
					///* Inclination */
	225.4928, 3033.6879, 0.0,
		0.04838, -0.2E-04, 0.0,
			5.2029,
				273.393, 1.3383, 0.0,	99.4198, 1.0583, 0.0,
					1.3097, -0.52E-02, 0.0,
	// Set 1 Harm4
	-.001, -.0005, .0045,
		// Harm6-8 for Harm iterations
		.0051, 581.7, -9.7,
		-.0005,2510.7,-12.5,
		-.0026,1313.7,-61.4,
		.0013,2370.79,-24.6,
		-.0013,3599.3,37.7,
		-.001,2574.7,31.4,
		-.00096,6708.2,-114.5,
		-.0006,5499.4,-74.97,
		-.0013,1419.0,54.2,
		.0006,6339.3,-109,
		.0007,4824.5,-50.9,
	// Set 2 Harm4
	.0020,-.0134,.0127,
		// Harm6-8 for Harm
		-.0023,676.2,.9,
		.00045,2361.4,174.9,
		.0015,1427.5,-188.8,
		.0006,2110.1,153.6,
		.0014,3606.8,-57.7,
		-.0017,2540.2,121.7,
		-.00099,6704.8,-22.3,
		-.0006,5480.2,24.5,
		.00096,1651.3,-118.3,
		.0006,6310.8,-4.8,
		.0007,4826.6,36.2,
	// Jupiter has no set 3
// Saturn: Harm=5 /* Mean Anomaly */
		///* Eccentricity */
			///* Semi-major axis */
				///* Arg of perihelion and ascending node (used in Rotate()) */
					///* Inclination */
	174.2153,1223.50796,0,
		.05423,-.2E-03,0.0,
			9.5525,
				338.9117,-.3167,0.0,	112.8261,.8259,0.0,
					2.4908,-.0047,0.0,
	// Set 1 Harm4
	-.0009,.0037,0,
		// Harm6-8 for Harm
		.0134,1238.9,-16.4,
		-.00426,3040.9,-25.2,
		.0064,1835.3,36.1,
		-.0153,610.8,-44.2,
		-.0015,2480.5,-69.4,
	// Set 2 Harm4
	-.0014,.0026,0,
		// Harm6-8 for Harm
		.0111,1242.2,78.3,
		-.0045,3034.96,62.8,
		-.0066,1829.2,-51.5,
		-.0078,640.6,24.2,
		-.0016,2363.4,-141.4,
	// Set 3 Harm4
	.0006,-.0002,0,
		// Harm6-8 for Harm-1
		-.0005,1251.1,43.7,
		.0005,622.8,13.7,
		.0003,1824.7,-71.1,
		.0001,2997.1,78.2
	};

}

