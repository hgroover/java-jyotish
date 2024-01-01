// Javascript functions callable from Java applet
// (Requires MAYSCRIPT option in applet tag)
var js_java_ver = 5;

function jsj_ResetOutput()
{
  dbgOutReset();
}

function jsj_Output(s)
{
   dbgOut('<p>'+s+'</p>');
}

function jsj_RawOutput(s)
{
	dbgOut(s);
}

// Support functions called from javascript which affect applet behavior
var g_jsDebugOutput = 0;

// Turn javascript debug output on or off
function SetJSOutput(on)
{
  if (on==g_jsDebugOutput) return;
  g_jsDebugOutput = on;
  document.achart_main.SetJSOutput(g_jsDebugOutput);
}

// Toggle state of javascript debug output
function ToggleJSOutput()
{
  var link = document.getElementById("togglejs_link");
  SetJSOutput(!g_jsDebugOutput);
  // Set link text
  if (g_jsDebugOutput)
  {
	link.innerHTML = '(dbg OFF)';
  }
  else
  {
	link.innerHTML = '(dbg on)';
  }
}
