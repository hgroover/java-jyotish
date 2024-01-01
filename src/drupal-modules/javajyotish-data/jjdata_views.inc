<?php

// Raw dump of saved chart data
function view_chart_data( $uid = 0 )
{
  if (!$uid) $myuid = $GLOBALS['user']->uid;
  else $myuid = $uid->uid;
	$content = "<p>Chart data for user {$myuid}:</p>\n";
	// Note use of Drupal 6 API here...
	$result = db_query('SELECT uid, name, date, time, lat, lon, tz, dst, tzname FROM jjdata_charts WHERE uid = %d ORDER BY cid', array($myuid));
	$count = 0;
	while ($record = db_fetch_array($result))
	{
		$content .= sprintf( "<p>%s %s %s %s %s</p>\n",
			htmlentities($record->name), $record->date, $record->time, $record->lat, $record->lon
		);
		//$content .= print_r($record,TRUE);
		$count++;
	}
	if ($count == 0)
	{
		$content .= "<p>No saved chart data</p>";
	}
	return $content;
}

// Closing tag omitted
