<?php
// achart-server.php
// May require chmod g-w for mod_secure
header( "Content-Type: text/plain" );

require_once( "server-calc.inc.php" );

// Sanitize input - script kiddies are posting directly 
function sanitized( $s )
{
	if ($s)
	{
		$cleanlen = strcspn( $s, "\"'`$<>[]\\/" );
		if ($cleanlen != strlen( $s )) 
		{
			// FIXME log all the attempts
			return "";
		}
	}
	return $s;
}

function default_empty( $n )
{
  if (isset( $_REQUEST[$n] )) return sanitized( $_REQUEST[$n] );
  return "";
}

$name = default_empty("name");
$year = default_empty("year");
$month = default_empty("month");
$day = default_empty("day");
$date = default_empty("date");
$time = default_empty("time");
$lat = default_empty("lat");
$lon = default_empty("lon");
$tz = default_empty("tz");
$dst = default_empty("dst");
//$preload = default_empty("preload");
//$content_id = default_empty("content_id");

if ($year != "" && $month != "" && $day != "" && $time != "" && $lat != "" && $lon != "" && $tz != "")
{
    print( "<!-- start calc -->" );
    $s = ServerCalc( $name, $year, $month, $day, $time, $lat, $lon, $tz, $dst  );
    print( "<!-- output -->" );
    print $s;
    print( "<!-- end calc -->" );
}
else if (strlen($date) >= 8 && $time != "" && $lat != "" && $lon != "" && $tz != "")
{
    print( "<!-- start calc date -->" );
    $s = /* "<p>args:{$name}/{$date}/{$time}/{$lat}/{$lon}/{$tz}/{$dst}</p>" . */ ServerCalcDate( $name, $date, $time, $lat, $lon, $tz, $dst );
    print( "<!-- output -->" );
    print $s;
    print( "<!-- end calc -->" );
}
else
{
    print "<h5>Missing required arguments: {$name}/{$date}/{$time}/{$lat}/{$lon}/{$tz}/{$dst}</h5>";
}

// Closing tag intentionally left off
