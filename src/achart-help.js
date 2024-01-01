// Interactive help functions

var js_help_ver = 57;

// Help content
var g_help = new Array();
var g_helpObj = new Object();
// Common header
var g_help_header = '<table width="100%" border="0"><tr>\
<td align="center" valign="middle" width="75%"><h3>Help</h3></td>\
<td align="right" valign="top"><img src="images/mover.png" alt="Move help pane" style="cursor:move" onmousedown="StartPaneMove(event)" onmousemove="PaneMove(event)" onmouseup="EndPaneMove(event)"></td>\
</tr></table>\n';
// Common footer
var g_help_footer = '<p align="right">\
<a href="javascript:HelpTopic(\'index\')">Help index</a> \
<a href="javascript:HelpClose()" title="Close help window">Close</a> \
<img src="images/sizer.png" alt="Resize help pane" style="cursor:se-resize" onmousedown="StartPaneResize(event)" onmousemove="PaneResize(event)" onmouseup="EndPaneResize(event)"> \
&nbsp;</p>';

// Define default positions
var g_helpDefaultLeft = '25px';
var g_helpDefaultTop = '25px';
var g_helpDefaultHeight = '520px';
var g_helpDefaultWidth = '600px';

// Define indices for backward compatibility
g_help[0] = '<p><strong>General help</strong></p>\n\
<p>You <a href="javascript:HelpTopic(\'tour\')">can take the guided tour</a> or select any of the sections below for further information:</p>\n\
<p><a href="javascript:HelpTopic(\'dtime\');">About dates and times</a></p>\n\
<p><a href="javascript:HelpTopic(\'location\')">Selecting location</a></p>\n\
<p><a href="javascript:HelpTopic(\'timezone\')">Selecting timezone</a></p>\n\
<p><a href="javascript:HelpTopic(\'recalc\')">Recalculating chart</a></p>\n\
<p><a href="javascript:HelpTopic(\'chart\')">Understanding chart output</a></p>\n\
<p><a href="javascript:HelpTopic(\'saving\')">Saving and sharing chart data</a></p>\n\
<p><a href="javascript:HelpTopic(\'bugs\')">Known problems and limitations</a></p>\n\
<p><a href="javascript:HelpTopic(\'diagnose\')">Troubleshooting problems using this application</a></p>\n\
';
g_helpObj.index = g_help[0];
g_helpObj.index_left = '580px';
g_helpObj.index_top = '50px';

g_help[1] = '<p><strong>Dates and times</strong><p>\n\
<p>To enter the date/time of the event, select one of the two options shown above:</p>\n\
<p><img src="images2/cal_am.gif"> Enter times using AM and PM</p>\n\
<p><img src="images2/cal_24.gif"> Enter times using 24-hour time.</p>\n\
<p><a href="javascript:HelpTopic(\'datectl\')">Click here</a> to learn how to use the date and time control</p>\n\
';
g_helpObj.dtime = g_help[1];
g_helpObj.dtime_left = '500px';
g_helpObj.dtime_top = '120px';
g_helpObj.dtime_height = '420px';

g_help[2] = '<p><strong>Using the date and time control</strong></p>\n\
<p>Either the am/pm time format <img src="images2/cal_am.gif" alt="Enter time of event using 12-hr clock" title="Use 12-hr clock" onclick="javascript:pickDateTime(12)" style="cursor:pointer"/> \
or the 24-hour time format <img src="images2/cal_24.gif" alt="Enter time of event using 24-hr clock" title="Use 24-hr clock" onclick="javascript:pickDateTime(24)" style="cursor:pointer"/> \
may be used.</p>\n\
<ol><li>Click one of the controls to show the calendar pop-up</li>\n\
<li>Select the year</li>\n\
<li>Select the month</li>\n\
<li>Select the day of the month. Note that after doing this, if you change the year you\'ll have to select the day again.</li>\n\
<li>Enter the hours and minutes. If using the 24-hour format, you can also change the hour and minute using the up and down spinner controls.</li>\n\
<li>If using the am/pm format, select am or pm</li>\n\
<li>Finally, click OK to use the selected time and date</li></ol>\n\
<p>Next: <a href="javascript:HelpTopic(\'location\')">Selecting location</a></p>\n\
';
g_helpObj.datectl = g_help[2];
g_helpObj.datectl_left = '100px';
g_helpObj.datectl_top = '50px';

g_help[3] = '<p><strong>Selecting location using Google Maps</strong></p>\n\
<p>The map displayed at right allows you to select a location using Google Maps, which you may already be familiar with. There are 4 steps involved in selecting a location:</p>\n\
<ol><li>Use one of the "Quick navigation" links on the far right to go to the nearest world city. \
For example, if we want to find a hospital in Birmingham, England, the nearest of the cities listed is London. \
<a href="javascript:navTo(9,51.4830935147,-0.1313354492188,14,11)">Click here</a> or use the "London" link \
under "Quick navigation" to move the map to a display centered on London, England metropolis.</li>\n\
<li>Use the map controls to move the map display to the area of interest. In our example we click the [ - ] (minus) \
control on the left of the map control to zoom out. Clicking on the "zoom out (-)" control twice will show us \
all of southern England and Wales, and part of Normandy. We can "pan" the map by dragging. Click anywhere on the map other than the \
red marker and the map controls (at the top right and top left). While holding the left mouse button, drag the map down \
and slightly to the left so that Birmingham is in the center of the map. Now you can zoom back in using the "zoom in (+)" \
control. After zooming in a few times it may be necessary to pan the map again to continue focusing on the area of interest. \
You can also select the satellite view when zooming in closer. This overlays satellite imagery on top of roads and can be helpful \
in locating landmarks.</li>\n\
<li>After finding the location (it\'s best to zoom in and be as precise as possible, because even a difference of 5km can change the ascendant) \
left-click the mouse to drop a red marker.</li>\n\
<li>Finally, click on the red marker to use the selected location.</li></ol>\n\
<p>The timezone list is automatically reloaded whenever a map location is selected. Next: <a href="javascript:HelpTopic(\'timezone\')">Selecting timezone</a></p>\n\
';
g_helpObj.location = g_help[3];
g_helpObj.location_left = '15px';
g_helpObj.location_top = '30px';
g_helpObj.location_height = '540px';

// removed
__dead = '<p>For example, the state of Arizona in the US is on Mountain Time, which is generally 7 hours behind \
GMT, but does not observe daylight savings time. However, Navajo reservations in Arizona <strong>do</strong> \
observe daylight savings.</p>\n\
';
g_help[4] = '<p><strong>Selecting timezone</strong></p>\n\
<p>The timezone list shows timezones based on administrative timezones. The list is \
automatically filled from nearby timezone areas that apply based on the geographic location \
you selected in the previous step. We generally think of timezones based on how many hours we \
are from GMT (Greenwich Mean Time) but it\'s a bit more complicated than that.</p>\n\
<p>In India, the prevalent timezone is named Asia/Kolkata. Most timezones are named based on the continent.</p>\n\
<p>Why do it this way? Selecting the correct timezone means that for any given date (often going back to the 1920s) \
we know precisely what timezone was being used, and when daylight savings time was observed.</p>\n\
<p>Using our previous example, we clicked on the city of Birmingham in England and find timezones for the Faeroe Islands \
(in the North Atlantic) as well as several European cities. In our case we want "Europe/London". When we click on the \
correct timezone, if the date we selected is in daylight savings, the "Daylight savings" checkbox will automatically be \
selected (or it will be cleared if DST is not in effect and it was previously selected). The timezone in hours and minutes \
(India, for example, uses 5 hours 30 minutes, and Nepal - Asia/Kathmandu - uses 5 hours 45 minutes ahead of GMT) will \
be filled in as it is defined independently of daylight savings.</p>\n\
<p>Next: <a href="javascript:HelpTopic(\'recalc\')">Calculate chart</a></p>\n\
<p>(For further information on time zones and time changes see this wikipedia article: <a href="http://en.wikipedia.org/wiki/Time_zone" target="_blank">Time zones and time changes</a>)</p>\n\
';
g_helpObj.timezone = g_help[4];
g_helpObj.timezone_left = '10px';
g_helpObj.timezone_top = '10px';
g_helpObj.timezone_height = '580px';

g_help[5] = '<p><strong>Guided tour</strong></p>\n\
<p>This quick tour takes you through the steps needed to calculate a chart.</p>\n\
<p>Charts are calculated using time, date and place of birth. All of these values need to be accurate.</p>\n\
<p>First step: <a href="javascript:HelpTopic(\'tour_dtime\')">Enter date and time</a></p>\n\
';
g_helpObj.tour = g_help[5];
g_helpObj.tour_left = '580px';
g_helpObj.tour_top = '50px';
g_helpObj.tour_height = '420px';

g_help[6] = '<p><strong>Enter date and time</strong></p>\n\
<p>We usually know what day we were born. Knowing the time of birth can be harder than we might think. Our parents may not always remember our time of birth precisely \
and there have been no standards for birth certificates in the US, so birth certificates vary widely and some may \
not require the doctor to record the time of birth. It\'s definitely worth checking your birth certificate for the time of birth. Even a 5-minute difference \
in time of birth can affect the ascendant, and the timing of the major periods based on the lunar position.\n\
<p>To enter the local date and time of the event, select either of these widget icons (shown above next to the date and time):<br/>\n\
&nbsp;<img src="images2/cal_am.gif" alt="Enter time of event using 12-hr clock" title="Use 12-hr clock" onclick="javascript:HelpTopic(\'dtime_am\')" style="cursor:pointer"/> Use AM and PM for time<br/>\n\
&nbsp;<img src="images2/cal_24.gif" alt="Enter time of event using 24-hr clock" title="Use 24-hr clock" onclick="javascript:HelpTopic(\'dtime_24\')" style="cursor:pointer"/> Use 24-hour clock for time (e.g. 3pm is 15:00)\n\
';
g_helpObj.tour_dtime = g_help[6];
g_helpObj.tour_dtime_left = '60px';
g_helpObj.tour_dtime_top = '100px';
g_helpObj.tour_dtime_height = '420px';

g_help[7] = '<p><strong>Enter date and time using AM and PM</strong></p>\n\
<ol><li>Click on this icon to bring up the date/time control using AM and PM: <img src="images2/cal_am.gif" alt="Enter time of event using 12-hr clock" title="Use 12-hr clock" onclick="javascript:pickDateTime(12)" style="cursor:pointer"/></li>\n\
<li>Select year from the dropdown list shown <br/>at the top of the calendar control</li>\n\
<li>Select month from the dropdown list at the top <br/>of the calendar</li>\n\
<li>Select day of month by clicking on the day number <br/>shown on the calendar</li>\n\
<li>Enter the hours and minutes of the time to <br/>calculate chart for. <br/>Use local time for the location of the event</li>\n\
<li>Select AM or PM from the dropdown next to the minutes</li>\n\
<li>Click OK to complete entry of date/time. You should see the date and time in a format like YYYYMMDD and HHMM</li></ol>\n\
<p>Next step: <a href="javascript:HelpTopic(\'location\')">Selecting location</a></p>\n\
';
g_helpObj.dtime_am = g_help[7];
g_helpObj.dtime_am_left = '100px';
g_helpObj.dtime_am_top = '100px';
g_helpObj.dtime_am_height = '420px';

g_help[8] = '<p><strong>Enter date and time using 24-hour clock</strong></p>\n\
<ol><li>Click on this icon to bring up the date/time control using 24-hour time: <img src="images2/cal_24.gif" alt="Enter time of event using 24-hr clock" title="Use 24-hr clock" onclick="javascript:pickDateTime(24)" style="cursor:pointer"/></li>\n\
<li>Select year from the dropdown list shown <br/>at the top of the calendar control</li>\n\
<li>Select month from the dropdown list at the top <br/>of the calendar</li>\n\
<li>Select day of month by clicking on the day number <br/>shown on the calendar</li>\n\
<li>Enter the hours and minutes of the time to <br/>calculate chart for either by filling in the numbers <br/>OR using the up and down arrows (spinners). <br/>Use local time for the location of the event</li>\n\
<li>Click OK to complete entry of date/time. You should see the date and time in a format like YYYYMMDD and HHMM</li></ol>\n\
<p>Next step: <a href="javascript:HelpTopic(\'location\')">Selecting location</a></p>\n\
';
g_helpObj.dtime_24 = g_help[8];
g_helpObj.dtime_24_left = '100px';
g_helpObj.dtime_24_top = '100px';
g_helpObj.dtime_24_height = '420px';

g_help[9] = '<p><strong>Recalculate chart</strong></p>\n\
<p>You have now entered all the data (time, date, place) needed to calculate the jyotish chart. Simply click the "Recalculate" button shown below \
the timezone selection list, and you should see the updated information displayed in the Java applet at left.</p>\n\
<p>It\'s important to note that the chart will <b>not</b> be recalculated unless you\'ve loaded the page from a link or, if you\'ve changed any of the \
date, time and location information, clicked the Recalculate button.</p>\n\
<p>You can also save the details used to calculate the chart. <a href="javascript:HelpTopic(\'saving\')">Read this help section</a> for details on how to save \
chart information using browser cookies.</p>\n\
<p><a href="javascript:HelpTopic(\'tour\')">Restart guided tour</a> <br/><a href="javascript:HelpTopic(\'chart\')">Understanding the chart display</a> <br/><a href="javascript:HelpClose()">Close help</a></p>\n\
';
g_helpObj.recalc = g_help[9];
g_helpObj.recalc_left = '745px';
g_helpObj.recalc_top = '10px';
g_helpObj.recalc_applet_visible = true;

g_help[10] = '<p><strong>Understanding the chart display</strong></p>\n\
<p>Help for this section is still being written. You can navigate through various sections by clicking on buttons which appear at the top \
of the chart applet (displayed at left)</p>\n\
';
g_helpObj.chart = g_help[10];
g_helpObj.chart_left = '680px';
g_helpObj.chart_top = '10px';
g_helpObj.chart_applet_visible = true;

g_help[11] = '<p><strong>Known problems and limitations</strong></p>\n\
<p>Currently known problems and limitations:</p>\n\
<ul>\
<li>There is no way to resize the chart</li>\n\
<li>There is no way to resize the map control</li>\n\
<li>The chart doesn\'t provide any interpretation of positions</li>\n\
<li>There is no "Loading..." progress indication, and this can take a long time, especially when loading initially</li>\n\
<li>Output / Print feature doesn\'t work</li>\n\
</ul>\
<p>This is free software and will be continually improved as time permits. Our main focus is to help our students who are trying to \
learn jyotish in the context of Ayurveda and provide them with tools to easily calculate the chart.</p>\n\
';
g_helpObj.bugs = g_help[11];
g_helpObj.bugs_left = '10px';
g_helpObj.bugs_top = '10px';

g_help[12] = '<p><strong>Saving and sharing chart data</strong></p>\n\
<p>The process of entering birth information can be tedious. If you\'d like to save the chart details so you can come back to them later, there are a few options.</p>\n\
<p>You can save details in a "cookie" in your browser, which is stored locally on your computer. To use this feature you need to read the privacy disclaimer and check the \
"Enable saving" checkbox, then click the "I agree" button. This is important because we want you to understand that your birth details will be stored in browser cookies, \
and although they are saved locally there are ways that cookies can be accessed by other programs running on your computer.</p>\n\
<p>Once saving has been enabled, you can save the details for the currently displayed chart by checking the "Add to saved charts" checkbox and clicking the Update button. \
Charts you\'ve saved this way appear as links with the date and time of birth on the line just below the Update button at the bottom of the chart. You do not need \
to recalculate before saving. Calculation will be done automatically when you load the page from a saved link.</p>\n\
<p>If you use one of your saved chart links to reload chart details, you can make changes and click the Update button. If you did not load from a saved chart link, \
you can still save the current details as a new chart, and the "Add to saved charts" checkbox will be checked by default.</p>\n\
<p>As soon as you\'ve updated details (if changing the name, you need to navigate away from the name field) a "Redisplay" link should appear just below the \n\
timezone list. You can bookmark this link in your browser or copy the link location and save it in a document. Anyone can use this link to view chart information. \n\
If you want to share chart details without the name, you can use the "anonymous" link next to "Redisplay", which is identical except that it does not specify \
the name.</p>\n\
';
g_helpObj.saving = g_help[12];
g_helpObj.saving_left = '10px';
g_helpObj.saving_top = '2px';
g_helpObj.saving_width = '720px';
g_helpObj.saving_height = '496px';

g_helpObj.diagnose = '<p><strong>Troubleshooting</strong></p>\n\
<p>Some common problems are listed here. This section will be expanded as we get feedback from more users.</p>\n\
<p><strong>It takes a long time to load the first time</strong><br/>\n\
We use about 1.3mb of ephemeris data, besides the 600k of program files loaded for the Java applet. Your browser should cache both the ephemeris data as \
well as the program files, so this should not continue to be a problem.</p>\n\
<p><strong>There is a grey box with a red X in it</strong><br/>\n\
It\'s possible you don\'t have Java enabled. You may need to download the free Java runtime from <a href="http://java.sun.com">java.sun.com</a></p>\n\
<p><strong>When I select date and time and click OK, the calendar doesn\'t go away</strong><br/>\n\
This Internet Explorer problem should have been resolved. When you click OK after selecting year, month, day, and time, everything should update but the \
calendar remains visible. Click the Cancel button to dismiss the calendar. If you still see this problem, please contact us for assistance.</p>\n\
<p><strong>The chart applet remains grey or gets stuck on initializing</strong><br/>\n\
Use one of the links to reload and you should see the chart displayed normally. If the problem persists, try restarting your browser. If that doesn\'t work, \
contact us for assistance.\n\
';
g_helpObj.diagnose_top = '10px';
g_helpObj.diagnose_left = '10px';
g_helpObj.diagnose_width = '720px';
g_helpObj.diagnose_height = '600px';

g_helpObj.share = '<p><strong>Share this chart for discussion and interpretation</strong></p>\n\
<p>If you\'d like to share your name, contact information and birth info for discussion amongst staff and students of San Diego College of Ayurveda, \
you can do so here:<br/>\n\
<form method="post" id="share_form" onsubmit="ShareSubmitHandler()">\n\
<input type="checkbox" name="i_agree_to_share" value="yes" title="Check this to indicate that you agree to sharing your personal information"> I agree that by submitting this, I am sending \
my personal information by email and it will be viewed and discussed. Although we will not knowingly give out your information, you understand there is always a risk in sending personal \
information via email.<br/>\n\
My email address (required): <input type="text" name="contact_email"><br/>\n\
Share this chart as: <input type="text" name="txtName2"><br/>\n\
<input type="submit" value="Share my chart" name="share_requested"><br/>\n\
<input type="hidden" name="txtDate2">\
<input type="hidden" name="txtTime2">\
<input type="hidden" name="txtLat2">\
<input type="hidden" name="txtLong2">\
<input type="hidden" name="txtTZ2">\
<input type="hidden" name="tzName2">\
<input type="hidden" name="chkDST2" value="">\
</p>\n\
</form>\n\
';
g_helpObj.share_left = '680px';
g_helpObj.share_top = '10px';
g_helpObj.share_applet_visible = true;
g_helpObj.share_onload = function(){ShareLoadHandler();};

// Show help topic by index at specified position
function Help(x,y,index)
{
    var helpDiv = document.getElementById("help");
    helpDiv.style.left = '' + x + 'px';
    helpDiv.style.top = '' + y + 'px';
    if (index<0 || index>=g_help.length)
    {
		helpDiv.innerHTML = '<p>Invalid index ' + index + '</p>' + g_help_footer;
	}
	else
	{
		helpDiv.innerHTML = g_help_header + g_help[index] + g_help_footer;
	}
	ShowHelp(helpDiv,1);
}

// Show help topic by name using its default size and position
function HelpTopic(name)
{
    var helpDiv = document.getElementById("help");
    if (g_helpObj[name] == undefined)
    {
		helpDiv.innerHTML = g_help_header + '<p>Invalid topic ' + name + '</p>' + g_help_footer;
	}
	else
	{
		// Default to hiding applet unless appletVisible == true
		// FIXME we may not need to do this for browsers other than IE but need to verify...
		if (g_helpObj[name+'_applet_visible'] == true)
		{
			document.achart_main.style.visibility = 'visible';
		}
		else // hide by default
		{
			//var appletContainer = document.getElementById("applet_container");
			//appletContainer.style.visbility = 'hidden';
			document.achart_main.style.visibility = 'hidden';
		}
		// Additional diagnostic info is displayed for 'diagnose'
		var add = '';
		if (name=='diagnose')
		{
			add = '<p>Information about your browser<br/>If you want to report any problems with this software, please copy \
the following information listed below and paste it into your email:<br/>';
			add += 'Window: ';
			add += window.innerHeight;
			add += ' x ';
			add += window.innerWidth;
			add += '<br/>';
			add += 'Screen: ';
			add += window.top.screen.height;
			add += ' x ';
			add += window.top.screen.width;
			add += '<br/>';
			add += 'Browser: ';
			add += window.navigator.appName;
			add += ' Version: ';
			add += window.navigator.appVersion;
			add += ' Cookies? ';
			add += window.navigator.cookieEnabled;
			add += ' Platform: ';
			add += window.navigator.platform;
			add += ' Java: ';
			add += window.navigator.javaEnabled();
			add += '</p>\n';
		}
		helpDiv.innerHTML = g_help_header + g_helpObj[name] + add + g_help_footer;
	}
	// Always apply / reset defaults
	if (g_helpObj[name+'_left'] != undefined) helpDiv.style.left = g_helpObj[name+'_left'];
	else helpDiv.style.left = g_helpDefaultLeft;
	if (g_helpObj[name+'_top'] != undefined) helpDiv.style.top = g_helpObj[name+'_top'];
	else helpDiv.style.top = g_helpDefaultTop;
	if (g_helpObj[name+'_width'] != undefined) helpDiv.style.width = g_helpObj[name+'_width'];
	else helpDiv.style.width = g_helpDefaultWidth;
	if (g_helpObj[name+'_height'] != undefined) helpDiv.style.height = g_helpObj[name+'_height'];
	else helpDiv.style.height = g_helpDefaultHeight;
	// Invoke load handler if defined
	if (g_helpObj[name+'_onload'] != undefined) g_helpObj[name+'_onload']();
	ShowHelp(helpDiv,1);
}

function HelpShow(op)
{
	ShowHelp(document.getElementById("help"),op);
}

function HelpClose()
{
	ShowHelp(document.getElementById("help"),0);
	// Show applet
	//var appletContainer = document.getElementById("applet_container");
	//appletContainer.style.visbility = 'visible';
	document.achart_main.style.visibility = 'visible';
}

// Set help to visible (1) hidden (0) or toggle (-1)
function ShowHelp(e,vis)
{
	if (vis>0) e.style.visibility = 'visible';
	else if (vis==0) e.style.visibility = 'hidden';
	else
	{
		// toggle visbility of help div
		if (e.style.visibility=='hidden') e.style.visibility='visible';
		else e.style.visibility='hidden';
	}
}

// Convert <n>px to number
function ParsePixelValue(s)
{
  var px = s.indexOf('px');
  if (px <= 0) return s.valueOf();
  return s.substr(0,px).valueOf();
}

var g_isPaneMoving = 0;
var g_paneMoveStart;
var g_paneWidgetOffset_x; // Offset of event widget from help pane's left
var g_paneWidgetOffset_y;

// Help pane move and resize
function StartPaneMove(e)
{
  if (!e) e = window.event;
  var helpDiv = document.getElementById("help");
  g_isPaneMoving = 1;
  g_paneMoveStart = e;
  var sLeft = helpDiv.style.left;
  var sTop = helpDiv.style.top;
  g_paneWidgetOffset_x = e.screenX - ParsePixelValue(sLeft);
  g_paneWidgetOffset_y = e.screenY - ParsePixelValue(sTop);
  //dbgOut( '<p>move start: ' + e.clientX + ',' + e.clientY + '; ' + e.screenX + ',' + e.screenY + ' @' + g_paneWidgetOffset_x + ',' + g_paneWidgetOffset_y + ';' + sLeft + ':' + sTop + ' ' );
}

function PaneMove(e)
{
  if (!g_isPaneMoving) return;
  if (!e) e = window.event;
  var helpDiv = document.getElementById("help");
  var newX = e.screenX - g_paneWidgetOffset_x;
  var newY = e.screenY - g_paneWidgetOffset_y;
  if (newX > 0 && newY > 0)
  {
    helpDiv.style.left = newX + 'px';
    helpDiv.style.top = newY + 'px';
  }
  //dbgOut( ' move:' + e.screenX + ',' + e.screenY + ' ' );
}

function EndPaneMove(e)
{
  if (!g_isPaneMoving) return;
  //if (!e) e = window.event;
  //dbgOut( 'move end:' + e.screenX + ',' + e.screenY + '</p>\n' );
  g_isPaneMoving = 0;
}

var g_isPaneResizing = 0;
var g_paneResizeStart;

function StartPaneResize(e)
{
  if (!e) e = window.event;
  g_isPaneResizing = 1;
  g_paneResizeStart = e;
  //dbgOut( 'resize start:' + e.toString() );
}

function PaneResize(e)
{
  if (!g_isPaneResizing) return;
  if (!e) e = window.event;
  //dbgOut( 'resize:' + e.toString() );
}

function EndPaneResize(e)
{
  if (!g_isPaneResizing) return;
  //if (!e) e = window.event;
  //dbgOut( 'resize end:' + e.toString() );
  g_isPaneResizing = 0;
}

// Load handler for share request
function ShareLoadHandler()
{
  var f1 = document.getElementById("share_form");
  f1.txtName2.value = document.forms[0].txtName.value;
}

// onsubmit handler for share request
function ShareSubmitHandler()
{
  var f1 = document.getElementById("share_form");
  f1.txtLat2.value = document.forms[0].txtLat.value;
  f1.txtLong2.value = document.forms[0].txtLong.value;
  f1.txtDate2.value = document.forms[0].txtDate.value;
  f1.txtTime2.value = document.forms[0].txtTime.value;
  f1.txtTZ2.value = document.forms[0].txtTZ.value;
  if (document.forms[0].chkDST.checked) f1.chkDST2.value = '1';
  var idx = document.forms[0].tzName.selectedIndex;
  if (idx>=0)
  {
    f1.tzName2.value = document.forms[0].tzName.options[idx].value; // + ' ' + idx + '/' + document.forms[0].tzName.length;
  }
  else
  {
    f1.tzName2.value = 'unknown';
  }
  return true;
}
