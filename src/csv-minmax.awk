BEGIN { 
	sun_min=100; sun_max=-100; 
	moon_min=100; moon_max=-100; 
	mercury_min=100; mercury_max=-100;
	venus_min=100;	venus_max=-100;
	mars_min=100;	max_max=-100;
	jupiter_min=100; jupiter_max=-100;
	saturn_min=100;	saturn_max=-100;
}
{ 
	if ($2<sun_min) sun_min=$2;
	if ($2>sun_max) sun_max=$2;
	if ($3<moon_min) moon_min=$3;
	if ($3>moon_max) moon_max=$3;
	if ($4<mercury_min) mercury_min=$4;
	if ($4>mercury_max) mercury_max=$4;
	if ($5<venus_min) venus_min=$5;
	if ($5>venus_max) venus_max=$5;
	if ($6<mars_min) mars_min=$6;
	if ($6>mars_max) mars_max=$6;
	if ($7<jupiter_min) jupiter_min=$7;
	if ($7>jupiter_max) jupiter_max=$7;
	if ($8<saturn_min) saturn_min=$8;
	if ($8>saturn_max) saturn_max=$8;
}
END {
	printf( "Sun: { %.5f, %.5f }; ", sun_min, sun_max );
	printf( "Moon: { %.5f, %.5f }; ", moon_min, moon_max );
	printf( "Mercury: { %.5f, %.5f }; ", mercury_min, mercury_max ); 
	printf( "Venus: { %.5f, %.5f }; ", venus_min, venus_max );
	printf( "Mars: { %.5f, %.5f }; ", mars_min, mars_max );
	printf( "Jupiter: { %.5f, %.5f }; ", jupiter_min, jupiter_max );
	printf( "Saturn: { %.5f, %.5f }\n", saturn_min, saturn_max );
}
