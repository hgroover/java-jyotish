<?php
// PHP functions used in achart.php

// Get uri without any query data
function curURI()
{
 // Split query from request uri
 $uri = $_SERVER["REQUEST_URI"];
 $queryPos = strpos( $uri, "?" );
 if ($queryPos !== false)
 {
	$uri = substr( $uri, 0, $queryPos );
 }
 return $uri;
}

// Get base absolute URL
function curBaseURL()
{
	$pageURL = 'http';
	if (isset( $_SERVER['HTTPS'] ) && $_SERVER['HTTPS'] == "on") $pageURL .= "s";
	$pageURL .= "://";
	$pageURL .= $_SERVER['SERVER_NAME'];
	if ($_SERVER['SERVER_PORT'] != "80") $pageURL .= ":{$_SERVER[SERVER_PORT]}";
	return $pageURL;
}

// Stolen from http://www.webcheatsheet.com/PHP/get_current_page_url.php
function curPageURL( $withquery = false ) {
 $pageURL = curBaseURL();
 if ($withquery)
 {
   $uri = $_SERVER["REQUEST_URI"];
 }
 else
 {
   $uri = curURI();
 }
 return $pageURL . $uri;
}

function curParentDirURL() {
 $pageURL = curBaseURL();
 $dir = dirname(curURI());
 return $pageURL . $dir;
}

// Transform offset from GMT in seconds to [-]HHMM
function CanonicalOffset($soff)
{
  $pref = "";
  if ($soff < 0)
  {
    $pref = "-";
    $soff = abs($soff);
  }
  $soff /= 60;
  return sprintf( "%s%02d%02d", $pref, $soff / 60, $soff % 60 );
}


// Transform yyyymmdd into yyyy-mm-dd
function CanonicalDate($d)
{
  if (strlen($d) != 8) return $d;
  return substr($d,0,4) . "-" . substr($d,4,2) . "-" . substr($d,6);
}

// Transform hhmm or hhmmss into hh:mm:ss
function CanonicalTime($t)
{
  if (strlen($t) == 4) $t .= "00";
  if (strlen($t) != 6) return $t;
  return substr($t,0,2) . ":" . substr($t,2,2) . ":" . substr($t,4);
}

// Parse DDD[ewns]mm into signed decimal degrees (S and W negative)
function ParseGeoTerm( $term )
{
  //printf( "<p>Parsing: %s</p>\n<pre>", $term );
  preg_match( '/^(\d+)([ewns])(.+)$/i', $term, $a );
  //print_r( $a );
  $d = 0.0 + $a[1] + $a[3] / 60.0;
  //printf( "</pre><p>d = %.5f</p>\n", $d );
  if (strchr( "sSwW", $a[2] ))
  {
    $d = -$d;
  }
  return $d;
}

// Convert signed decimal degrees into DDD[wesn]MM.M
function DecimalToGeoTerm( $dd, $signFlag )
{
	$isNeg = ($dd < 0);
	$dd = abs($dd);
	$s = sprintf( "%02d", (int)$dd );
	if ($isNeg) $s .= substr($signFlag,0,1);
	else $s .= substr($signFlag,1,1);
	return $s . sprintf( "%02.1f", ($dd - (int)$dd) * 60.0 );
}

// Get alternate names from get vars
function SetAlternates( $default, $name1, $name2 )
{
	if (isset( $_REQUEST[$name1] )) return $_REQUEST[$name1];
	if ($name2 != "" && isset( $_REQUEST[$name2] )) return $_REQUEST[$name2];
	return $default;
}

// Dispatch share request to specified email
function DispatchShare($source, $dest)
{
    global $ver_major, $ver_minor, $ver_build;
    printf( "<p>Sending...\n" );
    $subject = "Chart share request from " . $_POST['contact_email'] . " for " . $_POST['txtName2'];
    $body = "This chart was submitted for sharing:\n";
    $body .= curPageURL( false );
    $name = urlencode($_POST['txtName2']);
    $lat = $_POST['txtLat2'];
    $lon = $_POST['txtLong2'];
    $time = $_POST['txtTime2'];
    $date = $_POST['txtDate2'];
    $tzname = $_POST['tzName2'];
    $dst = $_POST['chkDST2'];
    $link = "name={$name}&lat={$lat}&lon={$lon}&date={$date}&time={$time}&tz={$tz}&tzname={$tzname}&dst={$dst}";
    $body .= "?{$link}";
    $body .= "\n\nLink as a site-relative URL: ";
    $relURL = curURI() . "?{$link}";
    $body .= "<a href=\"{$relURL}\">{$relURL}</a>";
    $body .= "\n\nUse the above link to view. (sent by achart v{$ver_major}.{$ver_minor}.{$ver_build})\n";
    $headers = 'From: ' . $source . '\r\n' .
        "X-Mailer: achart v{$ver_major}.{$ver_minor}.{$ver_build} php v" . phpversion();
    if (mail( $dest, $subject, $body, $headers ))
    {
        printf( " Mail sent.</p>\n" );
    }
    else
    {
        printf( " Failed to send email.</p>\n" );
    }
    //printf( "<pre>\n" );
    //print_r($_POST);
    //printf( "\n</pre>\n" );
}

// Parse __bands from achart-tzdata.js and set global vars $g_bands and $g_bandCount
function ParseJavascriptBands()
{
	global $g_bands;
	global $g_bandCount;
	$g_bandCount = 0;
	// Find var __bands = [ [-90,-66] , [-66,-48] , [-48,-42] , [-42,-36] , [-36,-30] , [-30,-22] , [-22,-12] , [-12,0] , [0,12] , [12,22] , [22,30] , [30,36] , [36,42] , [42,48] , [48,66] , [66,90] ];
	// in achart-tzdata.js
	$f = fopen("achart-tzdata.js", "r" );
	if ($f===FALSE) return;
	$arrayData = "";
	while (!$arrayData)
	{
		$line = fgets( $f );
		if ($line===FALSE) break;
		if (preg_match( '/^var +__bands += +\[ *\[(.*)\] *\];/', $line, $a ) > 0)
		{
			$arrayData = $a[1];
		}
	}
	fclose( $f );
	if (!$arrayData) return;
	// We have pairs in the form "min,max] , [min,max] ..., [min,max"
	$a = preg_split( '/\] *, *\[/', $arrayData );
	if (sizeof($a)==0) return;
	foreach ($a as $pair)
	{
		$g_bands[] = explode( ",", $pair );
		$g_bandCount++;
	}
}

// Determine band index for digital latitude
// We do this by parsing __bands out of achart-tzdata.js
// (mirrors javascript LatToBandIndex in achart-tz.js)
function LatToBandIndex( $dlat )
{
	global $g_bands;
	global $g_bandCount;
	if (!isset($g_bandCount)) ParseJavascriptBands();
	if ($g_bandCount == 0) return 0;
	for ($band=0; $band<$g_bandCount; $band++)
	{
		// Assume bands output by shapegrid.py are sorted from low to high
		// This allows us to check only the upper edge
		if ($dlat < $g_bands[$band][1])
		{
		  return $band;
		}
	}
	// Fallthrough to Antarctica
	return 0;
}

// Determine lune index for digital longitude
// (mirrors javascript LonToSegmentIndex in achart-tz.js)
function LonToSegmentIndex( $dlon )
{
	// Handle negative wrap on far west / dateline longitudes
	return (24 + floor( ($dlon + 180 - 7.5) / 15.0 )) % 24;
}

// Trailing php tag intentionally ommitted
