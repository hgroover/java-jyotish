<?php

function default_empty( $n )
{
  if (isset( $_REQUEST[$n] )) return $_REQUEST[$n];
  return "";
}

$name = default_empty("name");
$year = default_empty("year");
$month = default_empty("month");
$day = default_empty("day");
$time = default_empty("time");
$lat = default_empty("lat");
$lon = default_empty("lon");
$tz = default_empty("tz");
$dst = default_empty("dst");
$preload = default_empty("preload");
$content_id = default_empty("content_id");
if (
	(
		$name != ""
		&& $year != ""
		&& $month != ""
		&& $day != ""
		&& $time != ""
		&& $lat != ""
		&& $lon != ""
		&& $tz != ""
		&& $dst != ""
		&& $preload != "1"
	) 
	|| $content_id != ""
	)
{
  if ($content_id == "")
  {
  $cmd = "SUBJECT_NAME=" . escapeshellarg($name) . " /home/hgweb/jj/runchart.sh auto";
  $cmd .= sprintf( " %04d", $year );
  $cmd .= sprintf( " %02d", $month );
  $cmd .= sprintf( " %02d", $day );
  $cmd .= sprintf( " %04d", $time );
  $cmd .= (" " . escapeshellarg($lat));
  $cmd .= (" " . escapeshellarg($lon));
  $cmd .= (" " . escapeshellarg($tz));
  $cmd .= sprintf( " %d", $dst );
  $output = shell_exec( $cmd );
  // We should have Output path ...
  if (!strncmp( $output, "Output path", 11 ))
  {
    $a = explode( " ", $output );
    if (sizeof($a) >= 3)
    {
	$content_path = trim($a[2]);
	if (preg_match( '/\.out\/([0-9]+\/[0-9]+\.[0-9]+)$/', $content_path, $acm ))
		$content_id = str_replace( "/", "-", $acm[1] );
    }
  }
  } // Need to get content
  else
  {
	// Sanitize
	$content_id = str_replace( "..", "", $content_id );
	$content_id = str_replace( "/", "", $content_id );
	$content_path = ".out/" . str_replace( "-", "/", $content_id );
  } // Specified on command line
  if ($content_id != "")
  {
	//readfile( trim($a[2]) . ".html" );
	$ac = @file( "{$content_path}.html" );
	// Perform some editing
	$jj_ver = "";
	for ($n = 0; $n < sizeof($ac); $n++)
	{
		if (preg_match( '/^<h1>(Java J.+)<\/h1>$/', $ac[$n], $acm ))
		{
			$jj_ver = $acm[1];
			continue;
		}
		if (preg_match( '/^<h3>(.+)<\/h3>$/', $ac[$n], $acm ) && $jj_ver != "")
		{
			printf( "<h1>%s</h1>\n", $acm[1] );
			printf( "<h5>%s</h5>\n", $jj_ver );
			continue;
		}
		if (preg_match( '/^<\/body><\/html>$/', $ac[$n] ))
		{
			// Add end content
			printf( "<p style=\"font-family: Arial, 'Liberation Sans', Helvetica, Swiss, sans; font-size:10pt;\">Content id: %s Link: <a href=\"?content_id=%s\" title=\"Cached content\">%s</a></p>\n", $content_id, $content_id, $content_id ); 
		}
		print $ac[$n];
	} // for all lines
	exit;
  } // content_id set
} // Vars set
?><!DOCTYPE html>
<html>
<head>
<title>Quick test 1</title>
</head>
<body>
<h1>Form</h1>
<?php
if (isset( $output ) && $output != "") printf( "<p>Output was: %s</p>\n", $output );
?>
<p><form method="post" action="quick-test.php">
<br/>Name: <input type="text" name="name" value="<?php print $name;?>"/>
<br/>Date (year, month, day): <input type="text" name="year" value="<?php print $year;?>"/> <input type="text" name="month" value="<?php print $month;?>"/> <input type="text" name="day" value="<?php print $day;?>"/>
<br/>Time (hhmm 24 hr fmt): <input type="text" name="time" value="<?php print $time;?>"/>
<br/>Latitude (ddNmm or ddSmm): <input type="text" name="lat" value="<?php print $lat;?>"/>
<br/>Longitude (dddEmm or dddWmm): <input type="text" name="lon" value="<?php print $lon;?>"/>
<br/>Time zone (hhmm or -hhmm for west): <input type="text" name="tz" value="<?php print $tz;?>"/>
<br/>DST (0 or 1): <input type="text" name="dst" value="<?php print $dst;?>"/>
<br/><input type="submit" value="Calculate" />
</form></p>
</body>
</html>

