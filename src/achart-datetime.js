// Javascript date/time support functions

var js_datetime_ver = 16;

// Date/time changed
function handleDTChange()
{
  // We could defer this but it only applies when the field loses focus
  UpdateLink();
  onSelectionChanged(document.forms[0].tzName);
  return true;
} // handleDTChange()

var g_calInputHours = 12;
// Hidden dtime changed as a result of date/time picker
function handledtimeChange()
{
	// Parse date
	if (document.forms[0].dtime.value=='') return;
	document.forms[0].txtDate.value = document.forms[0].dtime.value.substr(0,4) + document.forms[0].dtime.value.substr(5,2) + document.forms[0].dtime.value.substr(8,2);
	var hours = 1 * document.forms[0].dtime.value.substr(11,2);
	if (g_calInputHours == 12)
	{
		if (document.forms[0].dtime.value.substr(16,2)=='PM') hours += 12;
	}
	if (g_dbg&0x10)
	{
		dbgOut('<p>got dtime=' + document.forms[0].dtime.value + ' hours=' + hours + '</p>');
	}
	var hStr = '0' + hours;
	document.forms[0].txtTime.value = hStr.substr(hStr.length-2) + document.forms[0].dtime.value.substr(14,2);

	// For IE: txtDate and txtTime were temporarily set writable, set back to readonly now
	document.forms[0].txtDate.readonly = true;
	document.forms[0].txtTime.readonly = true;

	handleDTChange();

	document.forms[0].txtLat.focus();
	document.forms[0].dtime.onchange = null;
	// This triggers an error in IE unless focus is set somewhere else
	document.forms[0].dtime.style.visibility = 'hidden';

	return true;
}

// Put up the date/time picker in either 12 or 24-hour format
function pickDateTime(hours)
{
	var curDate = document.forms[0].txtDate.value;
	var curTime = document.forms[0].txtTime.value;
	var fmtDT = '';
	g_calInputHours = hours;
	// Get current date/time and format yyyy-mm-dd hh:mm using 24-hr time or yyyy-mm-dd hh:mm{AM|PM}
	fmtDT = curDate.substr(0,4) + '-' + curDate.substr(4,2) + '-' + curDate.substr(6,2) + ' ';
	if (hours==12)
	{
		var curHours = 1 * curTime.substr(0,2);
		var ampm = "AM";
		if (curHours > 12)
		{
			ampm = "PM";
			curHours -= 12;
		}
		else if (curHours == 12)
		{
			ampm = "PM";
		}
		var hStr = '0' + curHours;
		fmtDT = fmtDT + hStr.substr(hStr.length-2) + ':' + curTime.substr(2,2) + ampm;
	}
	else
	{
		fmtDT = fmtDT + curTime.substr(0,2) + ':' + curTime.substr(2,2);
	}
	document.forms[0].dtime.value = fmtDT;
	document.forms[0].dtime.style.visibility = 'visible';
	document.forms[0].dtime.onchange = function(){handledtimeChange();};
	// For IE: txtDate and txtTime need to not be readonly
	document.forms[0].txtDate.readonly = false;
	document.forms[0].txtTime.readonly = false;
	if (g_dbg&0x10)
	{
		dbgOut('<p>pickDateTime('+hours+') fmtDT=['+fmtDT+']</p>');
	}
	NewCssCal('dtime','yyyyMMdd','dropdown',true,hours);
}

