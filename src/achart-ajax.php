<?php
// AJAX helper for achart.php
require_once( "achart-fns.inc.php" );
require_once( "achart-cookie-fns.inc.php" );

// fn is required for all functions
// Return is a single tab-delimited line
// For fn=="tz", abbr, offset, dst, seconds_from_utc
// For fn=="ck*", status, fn, data

$dbgOut = 0;
//$dbgOut = @fopen( "log/achart-ajax-dbg.log", "w" );
if ($dbgOut)
{
    fprintf( $dbgOut, "request dump:\n%s\n", print_r($_REQUEST,true) );
    fprintf( $dbgOut, "cookie dump:\n%s\n", print_r($_COOKIE,true) );
}

$fn = "tz";
$tzname = "";
$date = date("Y-m-d");
$time = date("H:i:s");
$with_translit = 1;
$with_nagari = 1;
if (isset( $_REQUEST["tzname"] )) $tzname = $_REQUEST["tzname"];
if (isset( $_REQUEST["date"] )) $date = $_REQUEST["date"];
if (isset( $_REQUEST["time"] )) $time = $_REQUEST["time"];
if (isset( $_REQUEST["fn"] )) $fn = $_REQUEST["fn"];
if ($fn == "tz" && $tzname && $date && $time)
{
  $tz = new DateTimeZone($tzname);
  $df = CanonicalDate($date);
  $dt = CanonicalTime($time);
  $localtime = $df . "T" . $dt;
  $dtfmt = "D Y-m-d H:i:s Z e I";
  $t = new DateTime($localtime, $tz);
  //$tnodst = $t;
  $dst = $t->format("I");
  //$tnodst->sub(new DateInterval("PT{$dst}H"));
  $abbr = $t->format("T");
  //$off = $tnodst->format("O");
  // We want the format in canonical form either hhmm or -hhmm
  $secs = $t->format("Z");
  $off = CanonicalOffset($secs - $dst * 3600);
  //printf( "<p>East: %s offset: %d dst %d abbr=%s</p>\n", $t->format($dtfmt), $t->getOffset(), $dst, $abbr );
  printf( "%s\t%s\t%d\t%d\n", $abbr, $off, $dst, $secs );
  // There are a lot of transitions, typically 1x/year
  //print_r( $tzEast->getTransitions() );
  // This would take the UTC time currently defined by date/time in the previous timezone and adjust it
  // to the new timezone, which is not what we want.
  //$tEast->setTimeZone($tzIndia);
  /***********
  // Construct a new date/time object using localtime and the specified timezone
  $tIndia = new DateTime($localtime, $tzIndia);
  $dstIndia = $tIndia->format("I");
  $abbrIndia = $tIndia->format("e");
  printf( "<p>India: %s offset: %d dst %d</p>\n", $tIndia->format($dtfmt), $tIndia->getOffset(), $dstIndia );
 **********/
} // tz function
// ck_enable, ck_disable, ck_add, ck_update
else if ($fn == "ck_enable")
{
    $acookie = new ACookie("", "", 0, 1);
    // $_REQUEST['save_enabled'] will determine actual final state
    $acookie->Init( $_GET, "" );
    printf( "200\t%s\t%d\t%s\n", $fn, $acookie->save_enabled, $acookie->debugShort );
} // enable cookie save
else if ($fn == "ck_disable")
{
    $acookie = new ACookie("", "", 0, 0);
    // $_REQUEST['save_disabled'] will determine actual final state
    $acookie->Init( $_GET, "" );
    printf( "200\t%s\t%d\t%s\n", $fn, $acookie->save_enabled, $acookie->debugShort );
} // disable cookie save
else if ($fn == "ck_add")
{
	$acookie = new ACookie("", "", 0, 1);
	// $_REQUEST['save_new'] should be 1 (non-empty non-zero)
	// $_REQUEST['data'] should have args
	$acookie->Init( $_GET, $_GET['data'] );
	printf( "200\t%s\t%d\t%s\n", $fn, $acookie->saved, $acookie->debugShort );
} // add to cookies
else if ($fn == "ck_update")
{
	$acookie = new ACookie("", "", 0, 1);
	// $_REQUEST['save_id'] will determine actual save id
	// $_REQUEST['data'] should have args
	$acookie->Init( $_GET, $_GET['data'] );
	printf( "200\t%s\t%d\t%s\n", $fn, $acookie->saved, $acookie->debugShort );
}
else if ($fn == "ck_delete")
{
	$acookie = new ACookie("", "", 0, 1);
	// $_REQUEST['delete_id'] will determine id to delete
	$acookie->Init( $_GET, "" );
	printf( "200\t%s\t%d\t%s\n", $fn, $acookie->saved, $acookie->debugShort );
}
else if ($fn == "ck_setdefs")
{
	// default_lat, _lon, _tz, _tzname, _dst
	$numberSet = 0;
	$numberSet += SetDefaultValue( "lat", "default_lat" );
	$numberSet += SetDefaultValue( "lon", "default_lon" );
	$numberSet += SetDefaultValue( "tz", "default_tz" );
	$numberSet += SetDefaultValue( "tzname", "default_tzname" );
	$numberSet += SetDefaultValue( "dst", "default_dst" );
	printf( "200\t%s\t%d\t1\n", $fn, $numberSet );
}
else if ($fn == "ev_rules")
{
	require_once( "achart-parashara.inc.php" );
	$with_dump = 0;
	if (isset( $_REQUEST["with_dump"] )) $with_dump = $_REQUEST["with_dump"];
	$a = EvaluateParashara( $_REQUEST["data"], $with_dump );
	printf( "200\t%s\t%d\thtml\n", $fn, sizeof($a) );
	for ($n = 0; $n < sizeof($a); $n++)
	{
		printf( "<p>%s</p>\n", Translit($a[$n], $with_translit, $with_nagari) );
	}
}
else
{
	printf( "no_tz\t+0000\t0\t0\tinvalid\n" );
}

if ($dbgOut)
{
	fclose( $dbgOut );
	$dbgOut = 0;
}

// Set default cookie from either $_GET or $_POST. If empty, delete
function SetDefaultValue( $varname, $cookiename )
{
  if (isset( $_GET[$varname] )) $value = $_GET[$varname];
  else if (isset( $_POST[$varname] )) $value = $_POST[$varname];
  else return 0;
  if ($value!="")
  {
	setcookie( $cookiename, $value, time() + 86400*365*5 );
  }
  else
  {
    // Set to expire one hour ago to delete
	setcookie( $cookiename, $value, time() - 3600 );
  }
  // Number of cookies modified
  return 1;
}

// Trailing tag intentionally ommitted
