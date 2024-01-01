// Data management functions for achart.php
// Due to applet reload problems in Firefox/IcedTea on some platforms,
// we avoid reloading the page and try to do everything via AJAX.
// Data management functions handled here include:
// - building and displaying saved chart data
// - managing operations (add, update, delete) on saved chart data
// - managing agreement results which update cookies
// - building and displaying share form
// - managing share requests

var js_data_ver = 52;

// Posts are handled by achart-ajax.php (which must be in the same directory and for which
// we use the full filename.ext)
var g_saveData = new Array();
var g_saveDataCount = 0;
var g_saveEnabled = 0; // Reflected from ACookie::$save_enabled
var g_currentDataIndex = -1; // Index into g_saveData of currently loaded data
var g_dataManagementShown = 0;

// Visible state of main block, to manage show / hide link
var g_mainBlockVisible = true;
var g_mainBlockLastHeight = 0;
var g_mainBlockLastWidth = 0;

// Manage number of saved and default charts shown
var g_showAllSaved = 0;
var g_showSavedLimit = 5;
var g_showAllDefault = 0;
var g_showDefaultLimit = 5;

// Define list of charts to show when there's nothing saved or saving not enabled
// These are parsed directly and are not used as URLs - no escaping needed for spaces
var g_defaultSaveList = new Array();
var g_defaultListCount = 0;
g_defaultSaveList[g_defaultListCount++] = 'name=Barack Obama&date=19610804&time=1924&lat=21N17.4&lon=157W48.6&tz=-1000&tzname=Pacific/Honolulu';
g_defaultSaveList[g_defaultListCount++] = 'name=Al Gore&date=19480331&time=1253&lat=38N54.2&lon=77W03.1&tz=-0500&tzname=America/New_York';
g_defaultSaveList[g_defaultListCount++] = 'name=Cesar Chavez&date=19270331&time=1500&lat=32N43&lon=114W37&tz=-0800&tzname=America/Los_Angeles';
g_defaultSaveList[g_defaultListCount++] = 'name=Martin Luther King&date=19290115&time=1300&lat=33N43.3&lon=84W22.3&tz=-0500&tzname=America/New_York';
g_defaultSaveList[g_defaultListCount++] = 'name=James Earl Ray&date=19280310&time=1500&lat=38n53&lon=90w10&tz=-0600&tzname=America/Chicago';
g_defaultSaveList[g_defaultListCount++] = 'name=Srinivas Ramanujan&date=18871222&time=1820&lat=11n21&lon=77e44&tz=0553&tzname=Asia/Kolkata';
g_defaultSaveList[g_defaultListCount++] = 'name=Paula Abdul&date=19620619&time=1432&lat=34n03&lon=118w14&tz=-0800&dst=1&tzname=America/Los_Angeles';
g_defaultSaveList[g_defaultListCount++] = 'name=Sylvester Stallone&date=19460706&time=1920&lat=40n42&lon=74w00&tz=-0500&dst=1&tzname=America/New_York';
g_defaultSaveList[g_defaultListCount++] = 'name=Christopher Reeve&date=19520925&time=0312&lat=40n46&lon=73w59&tz=-0500&dst=1&tzname=America/New_York';

// Set save enabled without update of html
function setSaveEnabled(ena)
{
	g_saveEnabled = ena;
}

// Very rough equivalent of php htmlentities (for debug display)
function roughHtmlentities(s)
{
	return s.replace('&','&amp;').replace('<','&lt;').replace('>','&gt;');
}

// Opposite of roughHtmlentities(), also decodes hex-escaped values
function html_entity_decode(s)
{
	return unescape(s.replace('&gt;','>').replace('&lt;','<').replace('&amp;','&'));
}

// Add saved item to local data
function addSavedItem(key,value)
{
    var idx = g_saveDataCount++;
    g_saveData[idx] = new Object();
    g_saveData[idx].key = key; // Serial value returned by time()
    g_saveData[idx].value = decodeURIComponent(value); // url-encoded list of values including name, save_id, etc.
    if (g_dbg&0x20) dbgOut('<p>addsaved(' + key + ',' + roughHtmlentities(value) + ')=' + idx + ', src=' + roughHtmlentities(g_saveData[idx].value) + '</p>\n');
    return idx;
}

// Brute force search by key value. Return index or -1 if not found
function findSavedItem(key)
{
    var n;
    for (n = 0; n < g_saveDataCount; n++)
    {
        if (g_saveData[n].key==key) return n;
    }
    // Not found
    return -1;
}

// Set save enabled and if changed, rewrite data block
function updateSaveEnabled(ena)
{
	if (ena != g_saveEnabled)
	{
		if (g_dbg&0x20) dbgOut('<p>ena=' + ena + ' was ' + g_saveEnabled + '</p>\n');
		setSaveEnabled(ena);
		rewriteDataBlock();
	}
	else if (g_dbg&0x20) dbgOut('<p>ena=' + ena + ' no change</p>\n');

}

// Update save enabled if specified checkbox is checked
function checkUpdateSaveEnabled(chk)
{
  if (chk.checked)
  {
	postCookieData('ck_enable','save_enabled=1');
  }
  else
  {
	dbgOut( '<p>Checkbox not selected- not enabling save</p>\n' );
  }
}

// Construct get url args from current form
function constructGetFromForm(withName,fieldSet)
{
    var url = '';
    var sep = '';
    if (!fieldSet || fieldSet.indexOf('lat')>=0)
    {
		url = url + sep + 'lat=' + document.forms[0].txtLat.value;
		sep = '&';
    }
    url += '&lon=';
    url += document.forms[0].txtLong.value;
    url += '&date=';	url += document.forms[0].txtDate.value;
    url += '&time=';	url += document.forms[0].txtTime.value;
    url += '&tz=';		url += document.forms[0].txtTZ.value;
	var idx = document.forms[0].tzName.selectedIndex;
	if (idx >= 0)
	{
		url += '&tzname=';	url += document.forms[0].tzName.options[idx].value;
	}
	if (document.forms[0].chkDST.checked)
	{
		url += '&dst=1';
	}
	if (withName)
	{
		if (document.forms[0].txtName.value!='')
		{
            // Escape or not?
			url += '&name=' + document.forms[0].txtName.value;
		}
	}
	return url;
}

// Load raw data from a url (from g_saveData or from g_defaultSaveList)
function loadSavedURL(urlData,needsRecalc)
{
    var a = urlData.split('&');
    var n;
    var dLat = 0;
    var dLon = 0;
    var tzname = "";
    for (n = 0; n < a.length; n++)
    {
        var a2 = a[n].split('=');
        if (a2.length < 2) continue;
        if (a2[0] == 'name')
        {
            document.forms[0].txtName.value = a2[1];
        }
        else if (a2[0] == 'lat')
        {
            document.forms[0].txtLat.value = a2[1];
            dLat = DigitalDegrees(a2[1]);
        }
        else if (a2[0] == 'lon')
        {
            document.forms[0].txtLong.value = a2[1];
            dLon = DigitalDegrees(a2[1]);
        }
        else if (a2[0] == 'date')
        {
            document.forms[0].txtDate.value = a2[1];
        }
        else if (a2[0] == 'time')
        {
            document.forms[0].txtTime.value = a2[1];
        }
        else if (a2[0] == 'tz')
        {
            document.forms[0].txtTZ.value = a2[1];
        }
        else if (a2[0] == 'dst')
        {
            document.forms[0].chkDST.checked = (a2[1] == '1');
        }
        else if (a2[0] == 'tzname')
        {
            // Find in document.forms[0].tzName
            // We were not finding values when clicking on the button to load something not already in the list
            // This was because of not specifying tzname in the call to navTo()
            tzname = a2[1];
            var newIndex = assertOptionListValue(document.forms[0].tzName, a2[1]);
            if (g_dbg & 0x20) dbgOut( '<p>tzname value ' + tzname + '; new index = ' + newIndex + '</p>' );
            document.forms[0].tzName.selectedIndex = newIndex;
        }
        else if (a2[0] != 'save_id')
        {
            dbgOut( '<p>Warning: unknown field name ' + a2[0] + '; disregarding value ' + a2[1] + '</p>\n' );
        }
    }
	// Set map position
	if (dLat!=0 && dLon!=0)
    try
    {
		var band = LatToBandIndex(dLat);
		var lune = LonToSegmentIndex(dLon);
		// Assert zoom level >= 10
		var curZoom = g_map.getZoom();
		if (curZoom < 10) curZoom = 10;
		navTo( curZoom, dLat, dLon, band, lune, tzname );
	}
	catch (e)
	{
		dbgOut( '<p>Failed to set map position: ' + e.toString() + '</p>\n' );
	}
    else
        // Set tzname for tz and dst
        // This should have been done in navTo()
        if (tzname!="") assertTimezoneSelection(tzname);
    // Start recalc after 750ms
	if (needsRecalc)
	{
        setTimeout("SubmitChart()", 750);
	}
}

// Load indexed item from g_saveData
function loadSavedItem(idx)
{
    if (idx < 0 || idx >= g_saveDataCount) return;
    // Get arg=value pairs
	if (g_dbg&0x20) dbgOut('<p>lsa[' + idx + ']=' + roughHtmlentities(g_saveData[idx].value) + '</p>\n');
    var needsRecalc = (g_currentDataIndex != idx);
    loadSavedURL(g_saveData[idx].value, needsRecalc);
    // Set currently selected item
    if (needsRecalc)
    {
		g_currentDataIndex = idx;
		rewriteDataBlock();
	}
}

// Load default item (celeb data) from g_defaultSaveList
function loadDefaultItem(idx)
{
	if (idx < 0 || idx >= g_defaultSaveList.length) return;
	loadSavedURL(g_defaultSaveList[idx], true);
}

// Construct loader from URL data
function constructLoaderFromURL(defaultLabel,urlData,isSelected,onClickExpr)
{
    var styleName = 'databtn';
    var btnPrefix = '';
    var btnSuffix = '';
    if (isSelected)
    {
		styleName = 'databtn_sel';
		btnPrefix = ' [';
		btnSuffix = '] ';
	}
    // Construct a description to display while hovering and for label
    var a = urlData.split('&');
    var label = defaultLabel;
    var desc = '' + defaultLabel;
    var n;
    var sNotfound = '';
    for (n = 0; n < a.length; n++)
    {
		var a2 = a[n].split('=');
		if (a2[0]=='name') desc = a2[1];
		else if (a2[0]=='date')
		{
			label = a2[1];
			desc += ' ';
			desc += a2[1];
		}
		else if (a2[0]=='time')
		{
            label += '\r\n';
			label += a2[1];
			desc += ' ';
			desc += a2[1];
		}
		else
		{
            sNotfound += ',';
            sNotfound += a2[0];
		}
    }
    if ((g_dbg & 0x20) != 0 && sNotfound != '') dbgOut( '<p>constructLoader() notfound=' + sNotfound + '</p>\n' );
    s = ' ' + btnPrefix;
    s += '<input type="button" class="';
    s += styleName;
    s += '" title="Load ';
    s += desc;
    s += '" value="';
    s += label;
    s += '" onclick="';
    s += onClickExpr;
    s += '" />';
    s += btnSuffix;
    return s;
}

// Construct loader button / widget from saved item
function constructLoaderButton(idx)
{
    var s = "";
    if (idx < 0 || idx >= g_saveDataCount) return s;
    if (g_dbg & 0x20) dbgOut( '<p>constructLoader(' + idx + '): val=' + roughHtmlentities(g_saveData[idx].value) + '</p>\n' );
    return constructLoaderFromURL('[' + idx + ']', g_saveData[idx].value, idx==g_currentDataIndex, 'loadSavedItem(' + idx + ')' );
}

// Construct loader button from default item (celeb data)
function constructDefaultButton(idx)
{
	if (idx < 0 || idx >= g_defaultSaveList.length) return "";
	return constructLoaderFromURL('c[' + idx + ']', g_defaultSaveList[idx], false, 'loadDefaultItem(' + idx + ')' );
}

// Update current data via AJAX
function AJAXUpdate()
{
	if (g_currentDataIndex<0)
	{
		dbgOut('<p>No current index</p>');
	}
	else
	{
		postCookieData('ck_update','save_id=' + g_saveData[g_currentDataIndex].key + '&data=' + encodeURIComponent(constructGetFromForm(true)))
	}
}

// Handle return from AJAX update
function AJAXUpdate_return(id)
{
	if (id<=0 || g_currentDataIndex<0)
	{
		dbgOut('<p>No id or invalid index</p>');
		return;
	}
	if (g_saveData[g_currentDataIndex].key != id)
	{
		dbgOut('ID mismatch:' + id + '!=' + g_saveData[g_currentDataIndex].key);
		return;
	}
	var oldData = g_saveData[g_currentDataIndex].value;
	g_saveData[g_currentDataIndex].value = constructGetFromForm(true) + '&save_id=' + id;
	if (oldData != g_saveData[g_currentDataIndex].value)
	{
        rewriteDataBlock();
	}
	if (g_dbg & 0x20) dbgOut( '<p>AJAXUpdate_return(' + id + '); new value[' + g_currentDataIndex + ']=' + roughHtmlentities(g_saveData[g_currentDataIndex].value) + '</p>\n' );
}

// Delete current data via AJAX
function AJAXDelete()
{
	if (g_currentDataIndex<0)
	{
		dbgOut('No current index');
	}
	else
	{
		postCookieData('ck_delete', 'delete_id=' + g_saveData[g_currentDataIndex].key );
	}
}

// Handle return from AJAX delete
function AJAXDelete_return(id)
{
	if (id<=0 || g_currentDataIndex<0)
	{
		dbgOut('No id or invalid index');
		return;
	}
	if (g_saveData[g_currentDataIndex].key != id)
	{
		dbgOut('ID mismatch:' + id + '!=' + g_saveData[g_currentDataIndex].key);
		return;
	}
	// Delete reference
	g_saveData[g_currentDataIndex] = null;
	if (g_currentDataIndex==g_saveDataCount-1)
	{
		// Deleted end value - current index moves down
		g_currentDataIndex--;
	}
	else
	{
		// Move everything else down - current index is still valid
		var n;
		for (n = g_currentDataIndex; n < g_saveDataCount-1; n++)
		{
			g_saveData[n] = g_saveData[n+1];
		}
		g_saveData[g_saveDataCount-1] = null;
	}
	g_saveDataCount--;
	rewriteDataBlock();
}

// Save current data as new entry via AJAX
function AJAXAddNew()
{
	postCookieData('ck_add','save_new=1&data='+encodeURIComponent(constructGetFromForm(true)));
}

// Handle returned new id from AJAX call
function AJAXAddNew_return(id)
{
	if (id>0)
	{
        var oldIndex = g_currentDataIndex;
		g_currentDataIndex = addSavedItem(id,constructGetFromForm(true) + '&save_id=' + id);
		if (g_dbg & 0x20) dbgOut( '<p>AJAXAddNew_return(' + id + ') - new index ' + g_currentDataIndex + ', was ' + oldIndex + '</p>\n' );
		rewriteDataBlock();
	}
	else
	{
		dbgOut('Did not get return id:' + id);
	}
}

// Set location defaults via AJAX
function AJAXSetDefaults(getQuery)
{
	postCookieData('ck_setdefs',getQuery);
}

// Handle returned new id from AJAX call
function AJAXSetDefaults_return(id)
{
	if (id>0)
	{
		if (g_dbg & 0x20) dbgOut( '<p>AJAXSetDefaults_return(' + id + ')</p>\n' );
	}
	else
	{
		dbgOut('SetDefaults failed:' + id);
	}
}

// Evaluate rules via AJAX
function AJAXEvaluateRules(symbolicData,withDump)
{
	postCookieData('ev_rules', 'with_dump=' + withDump + '&data=' + symbolicData );
}

// Handle return from AJAX ev_rules
function AJAXEvaluateRules_return(numLines,s)
{
	// First line has already been parsed and we have numLines
	var a = s.split('\n');
	var n;
	for (n = 1; n < a.length; n++) dbgOut(a[n]);
}

// Create link snippet
function toggleDisplayLink(isSaved,val,max)
{
    var s;
    s = ' <a href="javascript:SetDataButtonDisplay(';
    if (isSaved)
    {
        s += val;
        // If turning on this one, turn on other as well; if turning this one off, leave other alone
        if (val>0)
        {
            s += ',1';
        }
        else
        {
            s += ',-1';
        }
    }
    else
    {
        if (val > 0)
        {
            s += '1,';
        }
        else
        {
            s += '-1,';
        }
        s += val;
    }
    s += ')" title="Show ';
    if (val>0)
    {
        s += 'all ';
        s += max;
    }
    else
    {
        s += 'less entries';
    }
    s += '">';
    if (val>0) s += '(more)';
    else s += '(less)';
    s += '</a>';
    return s;
}

// Rewrite data block
function rewriteDataBlock()
{
  var dataBlock = document.getElementById("data_block");
  var defaultLink = document.getElementById("save_default_link");
  if (g_dbg&0x20) dbgOut('<p>rwdb sena=' + g_saveEnabled + '</p>\n');

  // Cookie operations - if save is not enabled, allow it to be turned on
  // ECMAScript gotcha: !g_saveEnabled does not work as expected when implicit eval of string may be required
  if (g_saveEnabled == 0)
  {
	html = '<p><input type="checkbox" name="save_enabled" value="1" title="Check here to enable saving to cookies in your local browser"/> Enable saving' +
		'<br/>By checking the "Enable saving" box and clicking "I agree" below, you acknowledge that personal birth details and names' +
		' will be saved to cookies in your browser. This information will be transmitted over the web whenever you visit this page' +
		' and anyone using your computer will have access to it.' +
		'<br/> <input type="button" value="I agree" onclick="checkUpdateSaveEnabled(document.forms[0].save_enabled)"/></p>';
	html += '<p>Chart data of famous people: ';
	var n;
	for (n = 0; n < g_defaultSaveList.length; n++)
	{
		html += constructDefaultButton(n);
	}
	html += '</p>';
	dataBlock.innerHTML = html;
	defaultLink.innerHTML = '';
  }
  else
  {
	var html = '<p><input type="button" value="Disable saving" title="Click here to disable saving to cookies" onclick="postCookieData(\'ck_disable\',\'save_disabled=1\')"/>';
	html += ' <input type="button" value="Manage" title="Manage saved information" onclick="ManageData()"/>';
	html += ' |';
	html += ' <input type="button" value="Update" title="Save changes to current selection" ';
	if (g_currentDataIndex<0) html += 'disabled ';
	html += 'onclick="AJAXUpdate()"/>';
	html += ' <input type="button" value="Add new" title="Add current data as a new entry" onclick="AJAXAddNew()"/>';
	html += ' <input type="button" value="Delete" title="Delete current data" ';
	if (g_currentDataIndex<0) html += 'disabled ';
	html += 'onclick="AJAXDelete()"/>';
	var groupSep = ' |';
	var totalButtons = 5;
	if (g_showAllSaved > 0) totalButtons += g_saveDataCount;
	else totalButtons += g_showSavedLimit;
	if (g_showAllDefault > 0) totalButtons += g_defaultListCount;
	else totalButtons += g_showDefaultLimit;
	var savedHeader = '';
	if (totalButtons > 15)
	{
        groupSep = '<br/>';
        savedHeader = ' My saved charts:';
	}
	var n;
	for (n = 0; n < g_saveDataCount; n++)
	{
        if (g_showAllSaved == 0 && n >= g_showSavedLimit) break;
		if (n==0)
		{
            html += groupSep;
            html += savedHeader;
		}
        html += constructLoaderButton(n);
	}
	if (n < g_saveDataCount)
	{
        html += toggleDisplayLink(true,1,g_saveDataCount);
	}
	else if (n >= g_showSavedLimit)
	{
        html += toggleDisplayLink(true,0,g_saveDataCount);
	}
	html += groupSep;
	html += ' Interesting people: ';
	for (n = 0; n < g_defaultListCount; n++)
	{
        if (g_showAllDefault == 0 && n >= g_showDefaultLimit) break;
		html += constructDefaultButton(n);
	}
	if (n < g_defaultSaveList.length)
	{
        html += toggleDisplayLink(false,1,g_defaultListCount);
	}
	else if (n >= g_showDefaultLimit)
	{
        html += toggleDisplayLink(false,0,g_defaultListCount);
	}
	/********************
	if ($acookie->saved != 0)
	{
		// Enable update
		printf( "<input type=\"hidden\" name=\"save_id\" value=\"%d\"/>", $acookie->saved );
	}
	// Enable add
	printf( "<input type=\"checkbox\" name=\"save_new\" value=\"1\" title=\"%s\"%s/> Add to saved charts\n",
		"Check here to add this set of chart data to your saved charts, then click Update",
		$acookie->saved ? "" : " checked" );

	dataBlock.innerHTML += ' <input type="button" value="Update" title="Commit cookie changes"/></p>\n';

	print( "<p>" );
	foreach ($_COOKIE['save_data'] as $key => $value)
	{
		// Supersede with newer data (from update)
		if ($acookie->add_update != "" && $acookie->add_update == $key)
		{
			$value = $acookie->new_data;
			$acookie->add_update = "";
		}

		printf( " %s", LoadURL( $key, $value ) );
	}
	// If we've added a new save, show that as well
	if ($acookie->add_update != "") printf( " %s", LoadURL( $acookie->add_update, $acookie->new_data ) );
	print( '</p>\n' );

	************/
	// Set html in one operation to avoid having tags closed for us
	html += '</p>';
	// This works
	//html += '<p>Symbolic data: ';
	//html += document.achart_main.m_CodedOutput;
	//html += '</p>';
	dataBlock.innerHTML = html;
	defaultLink.innerHTML = ' &nbsp;<a href="javascript:SaveLocationDefaults()" title="Save current location and timezone values as default">Save location as default</a>';
  }

}

// Post chart data to save in cookie
function postCookieData(request,getData)
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
  var getURL = 'achart-ajax.php?fn=' + request + '&' + getData;
  //alert('dbg:send ' + getURL);
  if (g_dbg&0x20) dbgOut('<p>send:' + roughHtmlentities(getURL) + '</p>\n');

  xmlhttp.onreadystatechange=function()
  {
  if (xmlhttp.readyState==4 && xmlhttp.status==200)
    {
    handleCookiePost(xmlhttp.responseText);
    }
  }
  xmlhttp.open("GET",getURL,true);
  xmlhttp.send();
} // postCookieData()

// Handle tab-delimited return data from AJAX call
function handleCookiePost(s) {
	// status   request data
	// status is 200 for success or something else like 4xx, 5xx
	// request is the original request (ck_enable, ck_disable, ck_add, ck_update)
	// data is typically the save id of a newly added entry, or the new enabled status
	var a = s.split('\t');
	if (g_dbg & 0x20)
	{
		var s = '<p>handleCookiePost(' + roughHtmlentities(s) + ')';
		if (a.length > 3)
		{
			s += ' x:';
			s += a[3];
		}
		dbgOut( s );
	}
	if (a.length < 3) return;
	if (a[0]!=200) return;
	if (a[1]=='ck_enable' || a[1]=='ck_disable') updateSaveEnabled(a[2]);
	else if (a[1]=='ck_add') AJAXAddNew_return(a[2]);
	else if (a[1]=='ck_update') AJAXUpdate_return(a[2]);
	else if (a[1]=='ck_delete') AJAXDelete_return(a[2]);
	else if (a[1]=='ck_setdefs') AJAXSetDefaults_return(a[2]);
	else if (a[1]=='ev_rules') AJAXEvaluateRules_return(a[2],s);
	else dbgOut( '<p>Unknown return: 1=' + a[1] + ', 2=' + a[2] + '</p>\n' );
} // handleCookiePost()

// Assert presence of value in option list and return index
function assertOptionListValue(ol,s)
{
    var n;
    for (n = 0; n < ol.options.length; n++)
    {
        if (ol.options[n].value==s)
        {
            return n;
        }
    }
    // Add and return new index
    var o = document.createElement('option');
    o.text = s;
    o.value = s;
    ol.add(o,null);
    return ol.options.length-1;
}

// Merge new contents into current data set
function MergeData()
{
    var updated = 0;
    var added = 0;
    // Go through lines
    var dataNew = document.getElementById("data_new");
    var a = dataNew.value.split('\n');
    var n;
    for (n = 0; n < a.length; n++)
    {
        var a2 = a[n].split(' ');
        if (a2.length<2) continue;
        if (a2[0]=='' || a2[0]=='0') continue;
        // Find index and add (if not found) or update
        var idx = findSavedItem(a2[0]);
        if (idx<0)
        {
            // Add via AJAX using existing id
            // FIXME strip multiple save_id values
            // Note that we must explicitly add to make this fake update work
            g_currentDataIndex = addSavedItem(a2[0], a2[1]);
            postCookieData('ck_update','save_id=' + a2[0] + '&data=' + a2[1]);
            added++;
        }
        else
        {
            // Update via AJAX
            //g_saveData[idx].value = decodeURIComponent(a2[1]);
            // FIXME strip multiple save_id values
            g_currentDataIndex = idx;
            postCookieData('ck_update','save_id=' + a2[0] + '&data=' + a2[1]);
            updated++;
        }
    }
    if (updated==0 && added==0) return;
    // Rewrite buttons
    rewriteDataBlock();
    // Rewrite data management
    WriteDataManagement(document.getElementById("data_management"));
}

// Rewrite contents of data management window
function WriteDataManagement(dataDiv)
{
    var html = '<h3>Saved data management</h3>';
    html += '<p>Current data:</p>\n';
    html += '<textarea rows="8" cols="100" id="data_current" class="data_list">\n';
    // Dump in format key<space>data
    var n;
    for (n = 0; n < g_saveDataCount; n++)
    {
        html += g_saveData[n].key;
        html += ' ';
        html += encodeURIComponent(g_saveData[n].value);
        html += '\n';
    }
    html += '</textarea>\n';
    html += '<p>Data to add or merge:</p>\n';
    html += '<textarea rows="5" cols="100" id="data_new" class="data_add_list">\n';
    html += '</textarea>\n';
    html += '<p><input type="button" value="Merge" title="Add or merge entries from above list into current data" onclick="MergeData()" />';
    html += '<br/>Select and copy lines from current list to save a copy of your entire saved set.</p>\n';
    html += '<p><input type="button" value="Close" title="Close this window" onclick="ManageData()" /></p>\n';
    dataDiv.innerHTML = html;
}

// Toggle display of data management block
function ManageData()
{
    var dataDiv = document.getElementById("data_management");
    if (g_dataManagementShown)
    {
        // Hide
        ShowHelp(dataDiv,0);
		document.achart_main.style.visibility = 'visible';
		g_dataManagementShown = 0;
    }
    else
    {
		document.achart_main.style.visibility = 'hidden';
        // Show
        dataDiv.style.left = '10px';
        dataDiv.style.top = '2px';
        dataDiv.style.width = '640px';
        dataDiv.style.height = '490px';
        WriteDataManagement(dataDiv);
        ShowHelp(dataDiv,1);
        g_dataManagementShown = 1;
    }
}

// Toggle display of saved and default charts.
// -1 means no change, 0 = off, 1 = on
function SetDataButtonDisplay( displaySaved, displayDefault )
{
    var changed = 0;
    if (displaySaved >= 0 && displaySaved != g_showAllSaved)
    {
        g_showAllSaved = displaySaved;
        changed++;
    }
    if (displayDefault >= 0 && displayDefault != g_showAllDefault)
    {
        g_showAllDefault = displayDefault;
        changed++;
    }
    if (g_dbg&0x20) dbgOut('<p>SetDataButtonDisplay('+displaySaved+','+displayDefault+') changed='+changed+'</p>\n');
    if (changed > 0)
    {
        rewriteDataBlock();
    }
}

// Save location values as defaults
function SaveLocationDefaults()
{
	// FIXME enable selective construction of get query
	AJAXSetDefaults(constructGetFromForm(false));
}

// Toggle visibility of main_body
function ToggleMainBodyVisible()
{
	ShowMainBody(!g_mainBlockVisible);
}

// Show or hide main body
function ShowMainBody( visible )
{
	var d = document.getElementById("main_body");
	if (!d) return;
	if (visible)
	{
		d.style.visibility = 'visible';
		d.style.height = g_mainBlockLastHeight;
		d.style.width = g_mainBlockLastWidth;
	}
	else
	{
		d.style.visibility = 'hidden';
		g_mainBlockLastHeight = d.style.height;
		g_mainBlockLastWidth = d.style.width;
		d.style.height = '0px';
		d.style.width = '0px';
	}
	g_mainBlockVisible = visible;
}

