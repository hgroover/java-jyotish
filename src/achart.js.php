<!-- Javascript from main body of achart.php which requires php interpretation -->
<?php
// Checks for $usingApplet set outside
?>
<script type="text/javascript"><!--

// Turn on debugging via alert() statements - enabled via ?dbg=0x0
var g_dbg = <?php print $dbg; ?>;
// 0x01 = geo marker select
// 0x02 = zone list load from lat/long
// 0x04 = debug output div test (digital lat/long from marker click)
// 0x08 = digital degree conversion debug
// 0x10 = debug calendar controls
// 0x20 = debug data AJAX operations
// 0x40 = step through initialization using alert() (use with reload_delay=120)
// 0x8000 = temp php output debug at load time

var g_xr;
var gSerial = 1;
var gSubmitCount = 0;

// 1 to use object tag for applets, 0 for applet
var g_javaObjectTag = <?php
if (isset( $_REQUEST['apptag'] ))
{
	print $_REQUEST['apptag'];
}
// object tag on Windows browers requires clsid in classid, which works differently based on browser
else if (strpos( $_SERVER['HTTP_USER_AGENT'], "Win" ) > 0)
{
	print 0;
}
else
{
	print 1;
}
?>;

// Alternate html to display in applet div (usually if no java supported)
var g_noJavaHtml = "<b>Java applet to display chart information.</b><br/>\n\
If the applet is not visible here, your browser or mobile device <br/>\n\
may not support java applets.<br/>\n\
We are working on a solution for this.<br/>\n\
It's also possible your browser has Java disabled or is missing a required plugin. To check whether Java is installed and properly working, visit:<br/>\n\
<a href='http://www.java.com/en/download/testjava.jsp' title='Sun/Oracle Java test page'>http://www.java.com/en/download/testjava.jsp</a><br/>\n\
You may also need to restart with <a href='?apptag=" + (g_javaObjectTag+1)%2 + "' title='Reload this page using applet tag instead'>different Java applet settings</a>\n\
";

// Load initialization
function init(withApplet) {
  var n = new Date();
  var s, s2;

  // Set date
  s = "";
  s += (1900 + n.getYear());
  s2 = "" + (n.getMonth() + 1);
  if (s2.length < 2) s += "0";
  s += s2;
  s2 = "" + n.getDate();
  if (s2.length < 2) s += "0";
  s += s2;

  <?php
  // Check for date passed as parameter
  if (isset( $_REQUEST["date"] ) || isset( $_REQUEST["txtDate"] ))
  {
     printf( "document.forms[0].txtDate.value='%s'; // Set value from URL\n", $date );
  }
  else
  {
	printf( "document.forms[0].txtDate.value = s; // Set current value\n" );
  }
  ?>

  if (g_dbg&0x40) alert( "[init:1] Date = " + s );

  // Set time
  s = "" + n.getHours();
  if (s.length < 2) s = "0" + s;
  s2 = "" + n.getMinutes();
  if (s2.length < 2) s += "0";
  s += s2;

  <?php
  // Check for date passed as parameter
  if (isset( $_REQUEST["time"] ) || isset( $_REQUEST["txtTime"] ))
  {
     printf( "document.forms[0].txtTime.value='%s'; // Set value from URL\n", $time );
  }
  else
  {
	printf( "document.forms[0].txtTime.value = s; // Set current value\n" );
  }
  ?>

  // Convert time zone offset in minutes
  // to hhmm
  // FIXME Need a way to determine if DST
  // is in effect and adjust TZ
  var nTZh = -n.getTimezoneOffset();
  s = "";
  if (nTZh < 0) {
	s += "-";
	nTZh *= -1;
  }
  var nTZm = nTZh % 60;
  nTZh /= 60;
  if (nTZh < 10) {
	s += "0";
  }
  s += nTZh;
  if (nTZm < 10) s += "0";
  s += nTZm;
  // Uncomment to put in browser TZ
  //document.forms[0].txtTZ.value = s;

  var lat, lon;
  // Landmarks and hospitals:
  // Columbia Hospital for Women
  //38.90389350500124, -77.05236627502438
  // Zero milestone (north edge of the Ellipse, which is just south of the White House)
  //38.89510263107891, -77.03655391931534
  // Holy Family hospital, New Delhi
  // 28.5614178198437, 77.2751606141968
  // Max Hospital Saket, New Delhi
  // 28.527903617831154, 77.21173173541263
  // 0.1 minutes is about 1 city block at 38d latitude
  <?php
  printf( "lat=%.6f; lon=%.6f;\n", $latd, $lond );
  ?>
  if (g_dbg&0x40) alert( "[init:2]" );
  initialize_map( lat, lon );
  if (g_dbg&0x40) alert( "[init:3]" );
  if (withApplet)
  {
      // Set timer to write applet (as object tag)
      setTimeout("writeApplet(g_javaObjectTag)", <?php printf( "%d000", $reload_delay ); ?>);
  }
  // Initialize data block
  <?php
  print $acookie->IssueJSInit();
  ?>if (g_dbg&0x40) alert( "[init:4]" );
  // Write data block html
  rewriteDataBlock();
  // Assert tz values from selection
  UpdateZones();
  //setTimeout("UpdateZones()", 5000);
  if (g_dbg&0x40) alert( "[init:exit]" );
} // init()

// initialize_map(), myLatlng, g_map, g_marker moved to achart-map.js

// Send output to dbgDiv
function dbgOut(s)
{
    var dbgDiv = document.getElementById("debug_output");
    dbgDiv.innerHTML = dbgDiv.innerHTML + s;
}

// Clear dbgDiv
function dbgOutReset()
{
    var dbgDiv = document.getElementById("debug_output");
    dbgDiv.innerHTML = '';
}

// Send contents of debug output to a new window or tab
function SendDebugOutputToWindow(title)
{
	var dbgDiv = document.getElementById("debug_output");
	var frm = document.getElementById("frmSendOutput");
	if (!frm)
	{
		alert("Fatal error in scripting: could not find send output form");
		return;
	}
	var s = "<!" + "DOCTYPE html>\n";
	s += "<html><head><title>" + title + "</title></head><body>";
	s += dbgDiv.innerHTML;
	s += "</body></html>\n";
	frm.data.value = s;
	frm.submit();
}

// Handle response from asynchronous chart load request
function HandleChartLoad(s)
{
    var dbgDiv = document.getElementById("debug_output");
    var existing;
    //existing = dbgDiv.innerHTML;
    existing = '<p style="font-size:8pt;"><br/>&nbsp;</p>';
    // Hack: xmlhttp.responseText has content returned from http request twice, the second time
    // with headers.
    var xOffset = s.indexOf("X-Powered-By: PHP");
    if (xOffset > 0)
    {
        dbgDiv.innerHTML = existing + s.substr(0,xOffset);
    }
    else
    {
        dbgDiv.innerHTML = existing + s;
    }
} // HandleChartLoad()

// Handle user entry
function SubmitChart() {
<?php if ($usingApplet) { ?>
	//document.achart_main.m_nRecalc = 10;
	if (document.forms[0].txtName.value!='')
	{
		document.achart_main.m_Text = document.forms[0].txtName.value;
	}
	else
	{
		document.achart_main.m_Text = "(name not specified)";
	}
	document.achart_main.m_Date = document.forms[0].txtDate.value;
	document.achart_main.m_Time = document.forms[0].txtTime.value;
	document.achart_main.m_Lat = document.forms[0].txtLat.value;
	document.achart_main.m_Long = document.forms[0].txtLong.value;
	document.achart_main.m_TZ = document.forms[0].txtTZ.value;
	document.achart_main.m_nDST = 0;
	if (document.forms[0].chkDST.checked) {
		document.achart_main.m_nDST = 1;
	}
	if (document.forms[0].chkRecalc.checked) {
		document.achart_main.m_nRecalc = document.forms[0].txtRecalc.value;
	}
	else {
		document.achart_main.m_nRecalc = -1; // Recalculate one time
	}
	// Parse lat, long
	//document.writeln( "<p><image src=http://tiger.census.gov/cgi-bin/mapgen?lon=77.0&lat=38.83&ht=1&wid=1&iht=200&iwd=200 border=0>" );
<?php } else { ?>
var aDiv = document.getElementById('applet_container');
var sDate = document.forms[0].txtDate.value;
var sTime = document.forms[0].txtTime.value;
var sName = document.forms[0].txtName.value;
var sTZ = document.forms[0].txtTZ.value;
var sLat = document.forms[0].txtLat.value;
var sLong = document.forms[0].txtLong.value;
var sDST = "0";
if (document.forms[0].chkDST.checked) 
{
	sDST = "1";
}

// If TZ has not been set, wait
if (sTZ == '')
{
	dbgOut('<p style="8pt;">Waiting for TZ</p>');
	return;
}
gSubmitCount++;
var getURL = 'achart-server.php?name='+sName+'&date='+sDate+'&time='+sTime+'&tz='+sTZ+'&lat='+sLat+'&lon='+sLong+'&dst='+sDST;
var xmlhttp = new XMLHttpRequest();
//alert('dbg:send ' + getURL);

/**
 * Removed all this from inline function
if (xmlhttp.responseType == 'text')
{
  dbgOut(xmlhttp.responseText);
  dbgOut( "<p>textser: " + (gSerial++) + "</p>" );
}
else if (xmlhttp.responseType == 'document')
{
  g_xr = xmlhttp.responseXML;
  var dbgDiv = document.getElementById("debug_output");
  if (g_xr != null)
  {
      dbgDiv.innerHTML = g_xr.body;
      dbgOut( "<p>xml " + (gSerial++) + "</p>" );
  }
  else dbgOut( xmlhttp.responseText + "<p>docnoxml: " + (gSerial++) + "</p>" );
}
else
{
  dbgOut('<p>type: ' + xmlhttp.responseType + ' ser ' + (gSerial++) + '</p>');
}
//dbgOut(xmlhttp.statusText);
//dbgOut(xmlhttp.response);
**/



xmlhttp.onreadystatechange=function() {
  if (xmlhttp.readyState==4 && xmlhttp.status==200)  {
    HandleChartLoad( xmlhttp.responseText );
    //xmlhttp.close();
  }
  else if (xmlhttp.readyState==4) {
    dbgOut( "<p>status: " + xmlhttp.status + " ser: " + (gSerial++) + "</p>" );
  }
}

aDiv.innerHTML = aDiv.innerHTML + '<p>Submitted ' + sDate + ' ' +
sTime + ' ' +
sLat + ' ' +
sLong + ' ' + ' ' + sTZ + ' ' + sDST
+ '</p>';
//dbgOutReset();
ToggleMainBodyVisible();
xmlhttp.open("GET",getURL,true);
//xmlhttp.responseType = "document";
xmlhttp.send();
<?php } // not using applet
?>
} // SubmitChart()

// Update save link
function UpdateLink() {
    var saveDiv = document.getElementById("save_link");
    var copyDiv = document.getElementById("copy_link");
    var url = '<?php print curPageUrl(); ?>?' + constructGetFromForm(false);
	var nonPrivate = '';
    var urlText = 'Redisplay';
    var html;
    var htmlCopy;
	if (document.forms[0].txtName.value!='')
	{
		nonPrivate = '&name=' + encodeURI(document.forms[0].txtName.value);
		html = '<a href="' + url + nonPrivate + '" title="Reload page or copy this link to share ALL information, including name">' + urlText + '</a>';
		htmlCopy = 'Full URL with name: <a href="' + url + nonPrivate + '">' + url + nonPrivate + '</a>';
		html = html + ' <a href="' + url + '" title="Copy this link to share chart WITHOUT name">anonymous</a>';
		htmlCopy = htmlCopy + '<br/>Without name: <a href="' + url + '">' + url + '</a>';
	}
	else
	{
		html = '<a href="' + url + '" title="Reload page or copy this link to share chart">' + urlText + '</a>';
		htmlCopy = 'Full URL: <a href="' + url + '">' + url + '</a>';
	}
    saveDiv.innerHTML = html;
    copyDiv.innerHTML = htmlCopy;
} // UpdateLink()

var g_lastBandIndex = -1;
var g_lastLuneIndex = -1;
// Apply new band and lune indices if different
function ApplyZoneIndices(bandIndex,luneIndex,lastSelected)
{
  if (g_dbg & 0x02)
  {
	alert('dLat='+dLat + ' dLon=' + dLon + ' band=' + bandIndex + ' lune=' + luneIndex + ' last='+g_lastBandIndex+',' + g_lastLuneIndex + ' len=' + document.forms[0].tzName.length);
  }
  if (bandIndex == g_lastBandIndex && luneIndex == g_lastLuneIndex && lastSelected != -1) return;
  //dbgOut( "<p>ApplyZoneIndices(" + bandIndex + "," + luneIndex + "," + lastSelected + ")</p>" );
  g_lastBandIndex = bandIndex;
  g_lastLuneIndex = luneIndex;
  var lastSelectedName = '';
  if (lastSelected != -1) lastSelectedName = document.forms[0].tzName.options[lastSelected].value;
  var n;
  if (document.forms[0].tzName.options!=null) document.forms[0].tzName.options.length = 0;
  if (g_dbg & 0x02)
  {
	//alert( 'g_tz['+bandIndex+'].length=' + g_tz[bandIndex].length + ' luneIndex=' + luneIndex);
	alert( 'g_tz[' + bandIndex + '][' + luneIndex + '].length=' + g_tz[bandIndex][luneIndex].length + ' lastsel=' + lastSelected + ' lastname=' + lastSelectedName);
  }
  var o;
  // Check for match against currently selected name iff one was selected
  var foundSelected = (lastSelectedName == '');
  //var dbgStr;
  //dbgStr = 'len=' + g_tz[bandIndex][luneIndex].length;
  for (n = 0; n < g_tz[bandIndex][luneIndex].length; n++)
  {
    o = document.createElement("option");
    o.text = g_tz[bandIndex][luneIndex][n];
    if (o.text == lastSelectedName)
    {
		o.selected = true;
		foundSelected = true;
    }
	document.forms[0].tzName.add(o,null);
	//dbgStr = dbgStr + ' o=' + o.text;
  }
  if (!foundSelected && lastSelectedName!='')
  {
    o = document.createElement("option");
    o.text = lastSelectedName;
	o.selected = true;
	// Insert at top of list
	document.forms[0].tzName.add(o,document.forms[0].tzName.options[0]);
  }
  if (lastSelectedName != '')
  {
    UpdateCurrentTimeOffsets(lastSelectedName);
  }
  /**** THis is not an actual problem - fixed -1 return from LonToSegmentIndex
  if (g_tz[bandIndex][luneIndex].length == 0)
  {
	alert('empty - using uninhabited');
	o = document.createElement("option");
	o.text = __uninhabited[0];
	document.forms[0].tzName.add(o,null);
  }
  ******************/
  //alert(dbgStr);
}

// Handle updated geo position
function UpdateZones()
{
  var dLat = DigitalDegrees(document.forms[0].txtLat.value);
  var dLon = DigitalDegrees(document.forms[0].txtLong.value);
  if (g_dbg & 0x08)
  {
	alert('dLat='+dLat + ' dLon=' + dLon);
  }
  //dbgOut( "<p>UpdateZones(" + dLat + "," + dLon + ")</p>" );
  ApplyZoneIndices( LatToBandIndex( dLat ), LonToSegmentIndex( dLon ), document.forms[0].tzName.selectedIndex );
}

// Update time offset and dst for specified timezone name, date and time
function UpdateTimeOffsets(tzName, sDate, sTime)
{
if (window.XMLHttpRequest)
{
      xmlhttp=new XMLHttpRequest();
}
// FIXME handle older IE
else
{
      alert('IE7 not currently supported');
      return;
}
var getURL = "achart-ajax.php?fn=tz&tzname="+tzName+'&date='+sDate+'&time='+sTime;
//alert('dbg:send ' + getURL);
xmlhttp.onreadystatechange=function()
{
if (xmlhttp.readyState==4 && xmlhttp.status==200)
  {
  handleTZData(xmlhttp.responseText);
  }
}
xmlhttp.open("GET",getURL,true);
xmlhttp.send();
} // UpdateTimeOffsets()

// Update time offsets for specified zone name using current date/time in form
function UpdateCurrentTimeOffsets(tzName)
{
  UpdateTimeOffsets( tzName, document.forms[0].txtDate.value, document.forms[0].txtTime.value );
}

// Handle tz selection changed event
function onSelectionChanged(selectObj) {
  var idx = selectObj.selectedIndex;
  if (idx<0) return;
  var tzName = selectObj.options[idx].value;
  var sDate = document.forms[0].txtDate.value;
  var sTime = document.forms[0].txtTime.value;
  if (tzName=='' || sDate=='' || sTime=='') return;
  UpdateTimeOffsets( tzName, sDate, sTime );
} // onSelectionChanged()

// Handle tab-delimited return data from AJAX call
function handleTZData(s) {
	// tz_abbr	offset_shhmm	dst_hours	offset_seconds
	// offset_shhmm is signed only if negative and is independent of dst
	// tz_abbr reflects dst
	// offset_seconds is instantaneous offset including dst
	var a = s.split('\t');
	//alert('handleTZData('+s+') len='+a.length);
	if (a.length < 3) return;
	document.forms[0].txtTZ.value = a[1];
	document.forms[0].chkDST.checked = (a[2]!=0);
	UpdateLink();
} // handleTZData()

// Write applet in applet_container div (typically to support delayed reload)
function writeApplet(asObject)
{
    var aDiv = document.getElementById('applet_container');
    var tagName;
    var tagSpecific;
    var codeTag;
    var codeParam;
	 if (asObject)
	 {
		tagName = 'object';
		tagSpecific = 'codetype="application/java" ';
		// For IE and Chrome on Windows 8, this is needed
		// Firefox wants classid="java:AChart.class"
		codeTag = 'classid="java:AChart.class"';
		codeParam = '';
		/*** Only works in Windows, fails firefox
		codeTag = 'classid="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93"';
		codeParam = '   <param name="code" value="AChart.class"/>\n';
		****/
	 }
	 else
	 {
		tagName = 'applet';
		tagSpecific = '';
		codeTag = 'code="AChart.class"';
		codeParam = '';
	 }
    aDiv.innerHTML = '<' + tagName + '\n\
    ' + tagSpecific + codeTag + '\
    codebase="<?php print curParentDirURL(); ?>"\n\
    name="achart_main"\n\
    width="640"\n\
    height="480"\n\
    archive="AChart-<?php printf( '%d%d', $ver_major, $ver_minor ); ?>.jar, SwissEph-2011.jar"\n\
    style="z-index:11;">\n\
' + codeParam + '\
	<param name="mayscript" value="true"/>\n\
	<param name="Style" value="North"/>\n\
	<param name="Description" value="<?php print $name; ?>"/>\n\
	<param name="Latitude" value="<?php print $lat; ?>"/>\n\
	<param name="Longitude" value="<?php print $lon; ?>"/>\n\
	<param name="TZ" value="<?php print $tz; ?>"/>\n\
	<param name="DST" value="<?php print $dst; ?>"/>\n\
	<param name="Time" value="<?php print $time; ?>"/>\n\
	<param name="Date" value="<?php print $date; ?>"/>\n\
	<param name="Ayanamsa" value="Lahiri"/>\n\
	<param name="EphPath" value="<?php print curParentDirURL(); ?>/ephe"/>\n' + g_noJavaHtml + '\n</' + tagName + '>';
	// Enable recalc
	var recalcBtn = document.getElementById('recalc_btn');
	recalcBtn.disabled = false;
} // writeApplet()

//-->
</script>
