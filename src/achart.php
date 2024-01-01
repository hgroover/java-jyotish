<?php
// Build ver is version of this file and associated javascript and php sources
$ver_build = 80;
// Major and minor are from applet and maintained in Makefile
// $js_tz_ver and other values (except $js_tzdata_ver and $js_dtpicker_ver)
// are extracted from sources and placed here as well
require_once( "version.inc.php" );

// Versions to force reload of javascript components - not displayed anywhere, increment when deploying new versions
// Most are defined within sources - these are either generated or ones we don't "own"
$js_tzdata_ver = 107; // achart-tzdata.js
$js_dtpicker_ver = 2227; // datetimepicker_css.js

$version = sprintf( "%d.%02d.%03d", $ver_major, $ver_minor, $ver_build );
$ver_info_str = "achart v{$version}; <span style=\"font-size:8px;\">"
	. "tz:{$js_tz_ver} tzdata:{$js_tzdata_ver} help:{$js_help_ver} dt:{$js_datetime_ver} dtpick:{$js_dtpicker_ver} map:{$js_map_ver}"
	. " java:{$js_java_ver} data:{$js_data_ver}"
	. "</span>";

// Get local config with google maps API key
include_once 'config.inc.php';

require_once( "achart-fns.inc.php" );
require_once( "menu.inc.php" );
require_once( "quick-nav.inc.php" );
require_once( "achart-cookie-fns.inc.php" );
require_once( "server-calc.inc.php" );

// Load plugins
include_once 'plugin.inc.php';

// Global settings
$useAbs = 0;

$dbg = SetAlternates( "0x0", "dbg", "" );
$dbgx = 0;
if ($dbg != "0x0" && strlen($dbg)>2 && !strncmp($dbg,"0x",2))
{
  $dbgx = hexdec( substr($dbg,2) );
}
$early_debug_text = "";

// Get persistent defaults
$default_lat = "38N54.2";
$default_lon = "77W03.1";
$default_tz = "-0500";
$default_tzname = "America/New_York";
$default_dst = 0;

if (isset( $_COOKIE['default_lat'] )) $default_lat = $_COOKIE['default_lat'];
if (isset( $_COOKIE['default_lon'] )) $default_lon = $_COOKIE['default_lon'];
if (isset( $_COOKIE['default_tz'] )) $default_tz = $_COOKIE['default_tz'];
if (isset( $_COOKIE['default_tzname'] )) $default_tzname = $_COOKIE['default_tzname'];
if (isset( $_COOKIE['default_dst'] )) $default_dst = $_COOKIE['default_dst'];

// Define default values to use
$name = SetAlternates( "My Chart", "name", "txtName" );
$lat = SetAlternates( $default_lat, "lat", "txtLat" );
$lon = SetAlternates( $default_lon, "lon", "txtLong" );

// Get default date and time on server. Server's timezone is America/Denver
$default_date = date("Y-m-d");
$default_time = date("H:i:s");
// $server_tzname should be set in config.inc.php
if (!isset( $server_tzname )) $server_tzname = "America/Denver"; // Bluehost default
//$early_debug_text .= sprintf( "<p>Server date/time=%s %s tz %s</p>\n", $default_date, $default_time, $server_tzname );
$server_tz = new DateTimeZone($server_tzname);
$df = CanonicalDate($default_date);
$dt = CanonicalTime($default_time);
//$early_debug_text .= sprintf( "<p>Canonical: %s %s</p>\n", $df, $dt );
$localtime = $df . "T" . $dt;
//$dtfmt = "D Y-m-d H:i:s Z e I";
$t = new DateTime($localtime, $server_tz);
$default_date = $t->format("Ymd");
$default_time = $t->format("Hi");
if ($default_tzname != $server_tzname)
{
	$local_tz = new DateTimeZone($default_tzname);
	$tl = new DateTime($localtime, $local_tz );
	//$early_debug_text .= sprintf( "<p>Server offset: %d, local offset (%s): %d</p>\n", $t->format("Z"), $default_tzname, $tl->format("Z") );
	$second_delta = $tl->format("Z") - $t->format("Z");
	// Requires php 5.2.0+
	//$t->add(new DateInterval("PT{$second_delta}S"));
	$nowsecs = time();
	$default_date = date("Ymd", $nowsecs + $second_delta);
	$default_time = date("Hi", $nowsecs + $second_delta);
	//$early_debug_text .= sprintf( "<p>Second delta: %d (%.2f h)</p>\n", $second_delta, $second_delta / 3600.0 );
}
//else
//{
//	$early_debug_text .= sprintf( "<p>Same as server, offset: %s</p>\n", $t->format("Z") );
//}

// Handled separately in javascript init
// These global vars will be used in embedded snippets in achart.js.php in WriteApplet
$date = SetAlternates( $default_date, "date", "txtDate" ); // Will use current date in javascript
$time = SetAlternates( $default_time, "time", "txtTime" ); // Will use current time in javascript
$tz = SetAlternates( $default_tz, "tz", "txtTZ" );
$dst = SetAlternates( $default_dst, "dst", "chkDST" );
$tzname = SetAlternates( $default_tzname, "tzName", "tzname" );
$saved = SetAlternates( 0, "saved", "" );
$show_intro = SetAlternates( -1, "show_intro", "" );
$reload_delay = SetAlternates( 3, "reload_delay", "" ); // Seconds to wait before loading app, to avoid problems with IcedTea reload

// Construct get url with and without name
$url_with_name = "name={$name}&lat={$lat}&lon={$lon}&date={$date}&time={$time}&tz={$tz}&tzname={$tzname}&dst={$dst}";
$url_without_name = "lat={$lat}&lon={$lon}&date={$date}&time={$time}&tz={$tz}&tzname={$tzname}&dst={$dst}";

$latd = ParseGeoTerm( $lat );
$lond = ParseGeoTerm( $lon );

// Since cookies don't show up until the next load,
// keep track of any we update or add
//$cookie_add_update -> $acookie->add_update
//$cookie_new_data -> $acookie->new_data
//$saved -> $acookie->saved
//$save_enabled -> $acookie->save_enabled

// Manage cookie data
$acookie = new ACookie("", "", $saved);
$acookie->Init( $_POST, $url_with_name ); // Handle POST and COOKIE, possibly issue setcookie()

// All cookie changes completed before starting output...

// Initialize plugins
$plugin_init_output = "";
if (isset( $plugins ))
{
	foreach ($plugins as $pkey => $pval)
	{
		if ($pval > 0)
		{
			$fname = "{$pkey}_plugin_init";
			$plugin_init_output .= $fname();
		}
	}
} // plugins exist

// We can finally dispense with anything but HTML 5...
?><!DOCTYPE html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<?php
// PHP init with output allowed
// Share requests are handled below in body
InitMenu();
?>
<title><?php if (isset($main_title)) print $main_title; else print 'AChart'; ?></title>
<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=<?php print $google_maps_key; ?>&amp;sensor=false"></script>
<script type="text/javascript" src="achart-tz.js?v=<?php print $js_tz_ver; ?>"></script>
<script type="text/javascript" src="achart-tzdata.js?v=<?php print $js_tzdata_ver; ?>"></script>
<script type="text/javascript" src="achart-map.js?v=<?php print $js_map_ver; ?>"></script>
<!-- v=2: changed start year to 1910 -->
<script type="text/javascript" src="datetimepicker_css.js?v=<?php print $js_dtpicker_ver; ?>"></script>
<script type="text/javascript" src="achart-datetime.js?v=<?php print $js_datetime_ver; ?>"></script>
<script type="text/javascript" src="achart-java.js?v=<?php print $js_java_ver; ?>"></script>
<script type="text/javascript" src="achart-help.js?v=<?php print $js_help_ver; ?>"></script>
<script type="text/javascript" src="achart-data.js?v=<?php print $js_data_ver; ?>"></script>
<style type="text/css">
  body { height: 100%; margin: 0; padding: 0; font-family: Arial,"Liberation Sans", "Bitstream Vera Sans",Helvetica,Sans,sans-serif; font-size:10pt; }
  #map_canvas { height: 200px; width: 320px }
  .databtn { font-size:8px; }
  .databtn_sel { font-size:11px; }
  .data_list { font-size:10px; background-color:#f3d2ff; }
  .data_add_list { font-size:10px; background-color:#c4ffe3; }
</style>
<?php
global $usingApplet;
$usingApplet = 0;
require_once("achart.js.php");
?>
</head>

<!-- init() in achart-js.php is where we used to write applet tag -->
<body onload="init(false);">
<div id="page_top" style="display: flex; flex-direction: column; margin-left: 6px;">
  <div id="top_view_links" style="clear: both; display: flex; flex-direction: column">
<p style="font-size:8pt;"><?php print $ver_info_str; ?> Copyright (C) 2001-2024 Henry Groover. Source available under GPL 2
<!-- <a href="javascript:ToggleJSOutput()" title="Toggle display of debug output from applet in main page" id="togglejs_link">(dbg on)</a>
-->
<a href="javascript:ToggleMainBodyVisible()" title="Show or hide date/time/place entry page">Show/hide</a>
<a href="javascript:SendDebugOutputToWindow('JavaJyotish output')" title="Send everything below this line to a new window">Pop out</a>
<a href="javascript:dbgOutReset()" title="Clear contents below this line">Clear output</a>
<!--
<a href="javascript:AJAXEvaluateRules(document.achart_main.m_CodedOutput,0)" title="Evaluate rules according to Maharshi Parashara">Parashara Rishi</a>
<a href="javascript:AJAXEvaluateRules(document.achart_main.m_CodedOutput,1)" title="debug">debug rules</a>
-->
</p>
  </div>
  <div id="main_body" style="clear: both; display: flex; flex-direction: column">
    <div id="top_menu" ><?php print TopMenu(); ?></div>
	<div id="top_form" >
      <form id="frmChartEntry" method="post" action="<?php print curURI(); ?>">
<!-- removed z-index: 10 -->
        <div id="applet_container">
<?php
global $content_id;
global $content_html;
$content_id = "";
$content_html = "";
if (HasServerCalcVars())
{
    print "<h5>Make any changes to birth info at right and click Calculate</h5>";
    $content_html = ServerCalcDate( $_REQUEST["name"],
        $_REQUEST["date"], $_REQUEST["time"],
        $_REQUEST["lat"], $_REQUEST["lon"],
        $_REQUEST["tz"], isset( $_REQUEST["dst"] ) ? $_REQUEST["dst"] : "0" );
    if ($content_id != "") print( "<p>Calculated content id {$content_id} - see below</p>" );
    else if (strlen( $content_html ) < 1024) printf( "<div style=\"8pt; color:red;\">%s</div>", $content_html );
    else printf( "<p>%d bytes received - see below</p>\n", strlen($content_html) );
    printf( "<!-- html received: %d bytes -->\n", strlen($content_html) );
}
else
      {
?>
<h5>Enter birth information as follows:</h5>
<ol style="font-size:9pt;">
 <li>Enter your name</li>
 <li>Click either the AM or 24 icon to enter time and date of birth in AM/PM or 24-hour time format (try to be accurate within 10 minutes)</li>
 <li>Find your place of birth on the map and click on it. You can click on one of the "Quick navigation" links to quickly go to the nearest major
 city; you can zoom out using the controls on the map; you can scroll around by dragging.</li>
 <li>Once you've selected place of birth, the time zone list should allow you to select the time change in effect at the time of birth. Select
 the correct time change. This will automatically set daylight savings correctly along with time zone.</li>
 <li>Click the large Calculate button. Chart will appear and this entry section will be hidden. Use the Show/Hide link to bring it back.</li>
</ol>
<?php } ?>
<!-- not used	<param name="PostURL" value="<?php print curParentDirURL(); ?>/achart-output.php"> -->
</div>
<div id="form_container">
<p>Name: <input type="text" name="txtName" size="20" value="<?php print $name; ?>" title="(Optional) Name of person" onchange="UpdateLink();"/><br/>
Time and date: <input type="text" name="txtTime" size="4" value="2050" readonly="readonly"/>
<input type="text" name="txtDate" size="8" value="19601107" readonly="readonly"/>
<img src="images2/cal_am.gif" alt="Enter time of event using 12-hr clock" title="Use 12-hr clock" onclick="javascript:pickDateTime(12)" style="cursor:pointer"/>
<img src="images2/cal_24.gif" alt="Enter time of event using 24-hr clock" title="Use 24-hr clock" onclick="javascript:pickDateTime(24)" style="cursor:pointer"/>
<input type="text" id="dtime" style="font-size:8px; visibility:hidden;"/>
</p>

<div id="geo_container">
 <div id="map_canvas" style="width:320px; height:200px; float:left;"></div>
 <div id="geo_fields" style="margin-left:5px; float:left; width:240px;">
<p>Lat:  <input type="text" name="txtLat" size="6"
	title="Enter latitude in the form ddNmm.m or ddSmm.m, or use the map widget to find the location and click on it"
	value="<?php print $lat; ?>"><br/>
Long: <input type="text" name="txtLong" size="7"
	title="Enter longitude in the form ddWmm.m or ddEmm.m, or use the map widget to find the location and click on it"
	value="<?php print $lon; ?>"><br/><br/>
	<span style="font-size:8pt;"><strong>Quick navigation:</strong><?php print ConstructQuickNav();	?></span></p>
 </div>
</div>
<div id="tz_container" style="">
<p>TZ:   <input type="text" name="txtTZ" size=5
	title="Enter timezone as -hhmm (hours and minutes W of GMT) or hhmm (E of GMT) or select from the list below"
	value="<?php print $tz; ?>"/> (-HHMM for time zones W of Greenwich, HHMM for E)<br/>
<input type="checkbox" name="chkDST"
	title="Check if 1 hour DST is in effect, or select a civic timezone from the list below"
	value="1" <?php if ($dst) print "checked=\"checked\""; ?>/> Daylight Savings Time in effect for given time</p>
<select name="tzName"
	size="5"
	title="Select the civic timezone which applied on the specified date/time to automatically determine offset from GMT and daylight savings"
	onchange="onSelectionChanged(this);">
<?php
$dtz[] = "America/Los_Angeles";
$dtz[] = "America/New_York";
$dtz[] = "Europe/London";
$dtz[] = "Asia/Kolkata";
$nMatch = -1;
for ($n = 0; $n < sizeof($dtz); $n++)
{
  if ($dtz[$n] == $tzname) $nMatch = $n;
}
if ($nMatch == -1)
{
  $dtz[] = $tzname;
  $nMatch = sizeof($dtz)-1;
}
for ($n = 0; $n < sizeof($dtz); $n++)
{
  printf( "<option%s>%s</option>\n", $n==$nMatch ? " selected" : "", $dtz[$n] );
}
?></select>
</div>
<div id="submit_container" style="">
<div id="extra_link_container" style=""><div id="save_link" style="font-size:8pt;">
<a href="<?php printf( "%s?%s", curURI(), htmlspecialchars($url_with_name) ); ?>">Redisplay</a>
<?php if ($url_without_name!=$url_with_name) printf (" <a href=\"%s?%s\">anonymous</a>", curURI(), htmlspecialchars($url_without_name) ); ?>
</div> <div id="save_default_link" style="font-size:8pt;"></div></div>
<p style="clear:both; float:left;"><input type="button" value="Calculate" id="recalc_btn" onclick="SubmitChart();" style="width:360px; height:40px;"/>
</p>
</div>
</div> <!-- form_container -->
<div id="footer_container" style="clear:both;">
<?php
// Handle share requests
if (isset( $_POST['share_requested'] ) && $_POST['share_requested'] == "Share my chart")
{
  $checkbox_value = "";
  if (isset( $_POST['i_agree_to_share'] )) $checkbox_value = $_POST['i_agree_to_share'];
  $contact_email = $_POST['contact_email'];
  if ($checkbox_value != "yes")
  {
    printf( "<p>You need to read and agree to the privacy disclosure</p>\n" );
  }
  else
  {
    DispatchShare( 'info@learn-ayurveda.com', 'hgweb@learn-ayurveda.com' );
  }
}
// Debug early init
if ($dbgx & 0x20)
{
	print "<p>Acookie debug:</p>" . $acookie->debugInfo;
	print "<p>Plugin init output:</p>" . $plugin_init_output;
}
?><div id="data_block"></div>
<?php
//printf( "<p>dbg=%s = %u = %x</p>\n", $dbg, $u, $u );
if ($dbgx & 0x8000)
{
printf( "<p>curURI()=[%s] curPageURL(false)=[%s] curPageURL(true)=[%s] curParentDirURL()=[%s]</p>\n",
    curURI(), curPageURL(false), curPageURL(true), curParentDirURL() );
}
//else printf( "<p>dbg=[%s]</p>\n", $dbg );
?>
</div>
<div id="copy_link" style="font-size:8pt;">URL for this page: <?php $fullURL = curPageURL() . '?' . htmlspecialchars($url_with_name); printf( "<a href=\"%s\">%s</a>", $fullURL, $fullURL  ); ?></div>
</form>
</div><!-- top_form -->
</div><!-- main_body -->
<form id="frmSendOutput" method="post" action="achart-output.php" target="_blank">
<input type="hidden" name="do_logging" value="1"/>
<input type="hidden" name="data" value=""/>
</form>
<?php
// Construct a load url from saved cookie data
function LoadURL( $key, $value )
{
	// Crack url-encoded vars into array $a
	parse_str( $value, $a );
	$title = "Saved id {$key}";
	if (isset( $a['date'] ) && isset( $a['time'] ))
	{
		$key = $a['date'] . 'T' . $a['time'];
	}
	if (isset( $a['name'] )) $title = $a['name'];
	return sprintf( "<a href=\"%s?%s\" title=\"%s\">%s</a>", curURI(), htmlspecialchars($value), $title, $key );
}


/*****
print("<pre>\n");
print_r($_SERVER);
print( "</pre>\n" );
******/
?>
<div id="debug_output" style="clear: both;"><?php
// Removed position: absolute from div
global $early_debug_text;
global $content_id;
global $content_html;
printf( "<!-- content_id = %s ; content_html length = %d -->", $content_id, strlen($content_html) );
print $early_debug_text;
print $content_html;
?></div>
<?php
//phpinfo();
?>
<!-- removed z-index: 100 from help, z-index: 90 from data -->
<div id="help" style="<?php if ($useAbs) print 'position:absolute;'; ?> visibility: hidden; margin:5px; padding:8px; left:500px; top:400px; width:600px; height:2px; border:thick solid #0000FF; background-color:#f6f8f6;"></div>
<div id="data_management" style="<?php if ($useAbs) print 'position:absolute;'; ?> visibility: hidden; margin:5px; padding:8px; left:500px; top:400px; width:600px; height:2px; border:thick solid #0000FF; background-color:#f6f8f6;"></div>
<div id="end_marker" style="margin-top: 10px;"><!-- end --></div>
</div>
</body>
</html>
