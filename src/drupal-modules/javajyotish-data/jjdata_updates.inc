<?php

/******* old stuff *******/
function jjdata_upload( $uid )
{
  $content = "";
  if (isset( $_POST['key'] ) && $_POST['key'] != "")
  {
	// Get hash from ebook_user_key
	$content .= sprintf( "<p>Validate hash %s</p>\n", $_POST['key'] );
	// Catenate with '.' and username
	$source_rec = db_query('SELECT hash, name FROM ebook_user_key LEFT JOIN users USING (uid) WHERE uid = :uid ORDER BY when_generated DESC LIMIT 1', array(':uid' => $uid->uid))->fetchAssoc();
	if (!$source_rec) return $content . "<p>Invalid user reference</p>\n";
	$source = $source_rec['hash'] . '.' . $source_rec['name'];
	// Get md5 checksum and compare with key
	if (md5($source) != $_POST['key']) return $content . "<p>Access denied</p>\n";
	if ($_POST['data'] == "") return $content . "<p>No data</p>\n";
	$alines = explode( "\n", $_POST['data'] );
	if (sizeof($alines) < 2) return $content . "</p>Data empty</p>\n";
	$addcount = 0;
	for ($n = 0; $n < sizeof($alines); $n++)
	{
		// Strip any trailing CR/LF but preserve HT (tab)
		$s = rtrim( $alines[$n], "\n\r" );
		// First line must contain headers
		if ($n == 0)
		{
			$hdrs = explode( "\t", $s );
			continue;
		}
		// Quietly ignore empty lines
		if ($s == "") continue;
		$fields = explode( "\t", $s );
		// Create associative array
		$af = array();
		for ($nf = 0; $nf < sizeof($fields); $nf++)
		{
			$af[$hdrs[$nf]] = $fields[$nf];
		}
		// Check for required values
		if (!isset( $af['asin'] ) || !isset( $af['title'] ) || !isset( $af['author'] ))
		{
			$content .= "<p>Ignoring record (missing essentials): ";
			$content .= print_r( $af, TRUE );
			$content .= "</p>";
			continue;
		}
		// Launder ISBN
		if (isset( $af['isbn'] ))
		{
			$af['isbn'] = substr( str_replace( array('-','_',' ','.',':'), '', $af['isbn'] ), 0, 16 );
		}
		$ures1 = db_insert('ebook_title')
			->fields(array('uid', 'title', 'author', 'subject', 'pubdate', 'isbn', 'tts'))
			->values(array('uid' => $uid->uid, 'title' => $af['title'], 'author' => $af['author'], 'subject' => $af['subject'], 'pubdate' => $af['pubdate'], 'isbn' => $af['isbn'], 'tts' => $af['tts'] ))
			->execute();
		/********
		$content .= "<p>Inserted: ";
		$content .= print_r($ures1,TRUE);
		$content .= "</p>\n";
		*********/
		$ures2 = db_insert('ebook_asin')
			->fields(array('tid','asin'))
			->values(array('tid' => $ures1, 'asin' => $af['asin']))
			->execute();
		/*********
		$content .= "<p>Inserted: ";
		$content .= print_r($ures2,TRUE);
		$content .= "</p>\n";
		*********/
		if (isset( $af['desc'] ) && $af['desc'] != "")
		{
			$ures3 = db_insert('ebook_synopsis')
				->fields(array('tid','synopsis'))
				->values(array('tid' => $ures1, 'synopsis' => $af['desc']))
				->execute();
			/********
			$content .= "<p>Inserted tid {$ures1}: ";
			$content .= print_r($ures3,TRUE);
			//$content .= $af['desc'];
			$content .= "</p>\n";
			******/
		}
		// Dump asin -> tid mapping for app
		$content .= sprintf( "<p asin=\"%s\" tid=\"%d\" uid=\"%d\">%s %d</p>\n", $af['asin'], $af['tid'], $uid->uid );
		$addcount++;
	}
	$content .= sprintf( "<p>Added %d records</p>\n", $addcount );
  }
  else
  {
	$key_value = "";
	// If user is logged in, calculate correct key
	if ($GLOBALS['user'] && $GLOBALS['user']->uid)
	{
		$source_rec = db_query('SELECT hash, name FROM ebook_user_key LEFT JOIN users USING (uid) WHERE uid = :uid ORDER BY when_generated DESC LIMIT 1', array(':uid' => $GLOBALS['user']->uid))->fetchAssoc();
		if ($source_rec) $key_value = md5($source_rec['hash'] . '.' . $source_rec['name']);
	}
	$content .= "<p>Manual data upload requires E-book upload key. Paste in tab-delimited output from Share My Kindle application</p>\n";
	$content .= "<form method=\"POST\"><p>E-book upload key: <input type=\"text\" name=\"key\" value=\"{$key_value}\"></p>\n";
	$content .= "<p>Tab-delimited data (must be entered using copy and paste):<br/>\n";
	$content .= "<textarea name=\"data\" rows=\"8\" cols=\"80\" style=\"font-size:8pt;\"></textarea></p>\n";
	$content .= "<p><input type=\"submit\" value=\"Upload\"></p></form>\n";
  }
  return $content;
}

function jjdata_delete( $uid )
{
	/******
	// We should have a posted list of space-delimited tid values
	if (!isset( $_POST['tid'] ))
	{
		return "<p>No tid specified</p>";
	}
	*****/
	$content = "";
	$delcount = 0;
	$content .= "<p>Tid list: ";
	foreach ($_POST as $key => $value)
	{
		if (substr($key,0,3) == "tid")
		{
			$content .= sprintf( " %s=%d", $key, $value );
			$delcount++;
			$dlist[] = $value;
		}
	}
	//$content .= print_r( $_POST['tid'], TRUE );
	if ($delcount == 0)
	{
		return "<p>Nothing to delete</p>\n";
	}
	$num_deleted = db_delete('ebook_title')
		->condition('tid', $dlist, 'IN')
		->execute();
	$num_deleted_asin = db_delete('ebook_asin')
		->condition('tid', $dlist, 'IN')
		->execute();
	$num_deleted_syn = db_delete('ebook_synopsis')
		->condition('tid', $dlist, 'IN')
		->execute();
	// FIXME also delete requests
	// Discard
	$content = "<p>{$delcount} titles to be deleted: {$num_deleted} from title list, {$num_deleted_asin} from ASIN list, {$num_deleted_syn} synopses</p>\n";
	return $content;
}


// Closing tag omitted deliberately
