<?php
// menu.inc.php - menu definition for achart.php
// Customize this to add your own links or put them in local_content/menu_links.dat and/or
// local_content/menu_links_pre.dat using the format
// <alignment><tab><url><tab><title><tab><link_body>

// Read optional entries from tab-delimited file and add to menu array. Return number added
function LoadOptionalMenu( &$aMenu, $file )
{
	$aLocal = @file( $file );
	if ($aLocal !== FALSE && sizeof($aLocal)>0)
	{
		$count = 0;
		for ($n = 0; $n < sizeof($aLocal); $n++)
		{
			// Ignore blank lines and comments
			if ($aLocal[$n] == "" || $aLocal[$n][0] == "#") continue;
			$a = explode("\t", $aLocal[$n]);
			if (sizeof($a)<3) continue;
			$aMenu[] = $a;
		}
		return $count;
	}
    return 0;
}

// Initialize menu entries
function InitMenu()
{
	global $menu_entries;

	// Get base URL for SDCOA
	$baseURL = curBaseURL();
	$baseURLPrefix = "";

	//printf( "<!-- baseURL=%s -->\n", $baseURL );

    $own[] = "sandiegocollegeofayurveda.net";
    $own[] = "sandiegocollegeofayurveda.com";
    $own[] = "ayurveda-california.org";

    $isOwn = 0;
    for ($n = 0; $n < sizeof($own); $n++)
    {
		if (strcasecmp( $_SERVER["SERVER_NAME"], $own[$n] ) == 0)
		{
			//printf( "<!-- match: %s -->\n", $own[$n] );
			$isOwn = 1;
			break;
		}
	}
	if (!$isOwn)
	{
		$baseURL = "http://sandiegocollegeofayurveda.net";
		$baseURLPrefix = $baseURL;
		//printf( "<!-- server=%s base=%s -->\n", $_SERVER["SERVER_NAME"], $baseURL );
	}
	/********
	else
	{
		printf( "<!-- server_name=%s n=%d -->\n", $_SERVER["SERVER_NAME"], $n );
	}
	*******/

    // Add optional entries from local_content/menu_links_pre.dat
    LoadOptionalMenu( $menu_entries, "menu_links_pre.dat" );

	// group alignment (l, r, c, or empty), link url, link title, body of link
	$menu_entries[] = array("l", $baseURL, "San Diego College of Ayurveda home", "SDCOA");
	$menu_entries[] = array("l", "{$baseURLPrefix}/courses", "Back to virtual learning", "Courses");
	$menu_entries[] = array("l", curURI(), "Discard all changes and reload this page with defaults", "Reload defaults");
	$menu_entries[] = array("l", "javascript:HelpTopic('share')", "Share this chart for discussion and interpretation", "Share");
	//$menu_entries[] = array("c", "javascript:HelpShow(-1)", "Toggle help", "Show help");
	$menu_entries[] = array("c", "javascript:HelpTopic('index')", "Display interactive help", "Help");
	$menu_entries[] = array("c", "javascript:HelpTopic('tour')", "Start interactive guided tour", "Guided tour");
	$menu_entries[] = array("c", "javascript:HelpTopic('diagnose')", "Diagnose problems you may be experiencing with this application", "Troubleshooting");

	// Add optional entries from local_content/menu_links.dat
	LoadOptionalMenu( $menu_entries, "menu_links.dat" );

	if (isset( $_REQUEST['back_link'] ))
	{
		$back_text = "Back";
		$back_title = "Go back";
		if (isset( $_REQUEST['back_text'] )) $back_text = $_REQUEST['back_text'];
		if (isset( $_REQUEST['back_title'] )) $back_title = $_REQUEST['back_title'];
		$menu_entries[] = array("r", $_REQUEST['back_link'], $back_title, $back_text );
	}

}

function TopMenu()
{
  global $menu_entries;
  // Was 780px with 300px, 280px, 200px
  $s = "<div id=\"top_menu_group\" style=\"width:99%; height:14pt; position:relative; float:left; top:0px; left:0px;\">";
  $group_left = "";
  $group_right = "";
  $group_center = "";
  for ($n = 0; $n < sizeof($menu_entries); $n++)
  {
	$al = $menu_entries[$n][0];
    $entry = sprintf( " <a href=\"%s\" title=\"%s\">%s</a>",
		$menu_entries[$n][1], $menu_entries[$n][2], $menu_entries[$n][3] );
	if ($al == "l") AddToGroup( $group_left, "top_menu_left", $entry, "left", "36%" );
	else if ($al == "c") AddToGroup( $group_center, "top_menu_center", $entry, "center", "33%" );
	else if ($al == "r") AddToGroup( $group_right, "top_menu_right", $entry, "right", "30%" );
	else $s .= $entry;
  }
  if ($group_left) $group_left .= "</div>";
  if ($group_center) $group_center .= "</div>";
  if ($group_right) $group_right .= "</div>";
  return $s . $group_left . $group_center . $group_right . "</div>";
}

// Add entry to group div
function AddToGroup( &$group, $groupname, $entry, $align, $width )
{
	if ($group=="")
	{
		$group = sprintf( "<div id=\"%s\" style=\"text-align:%s; float:left; top:2px; width:%s;\">", $groupname, $align, $width );
	}
	else
	{
		$group .= " | ";
	}
	$group .= $entry;
}

// Trailing close php tag intentionally omitted
