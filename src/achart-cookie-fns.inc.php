<?php
// Cookie functions shared by achart.php and achart-ajax.php

class ACookie
{
	public $add_update = "";
	public $new_data = "";
	public $saved = 0;
	public $save_enabled = 0;
	public $debugInfo = "<p>empty</p>";
	public $debugShort = "";

	function __construct( $cookie_add_update = "", $cookie_new_data = "", $cookie_saved = 0, $cookie_save_enabled = 0 )
	{
		$this->add_update = $cookie_add_update;
		$this->new_data = $cookie_new_data;
		$this->saved = $cookie_saved;
		$this->save_enabled = $cookie_save_enabled;
		$this->debugInfo = "";
	}

	// Perform HTTP initialization (which may include setcookie)
	function Init( &$collection, $url_with_name )
	{
        global $dbgx;
        global $plugins;

		if (isset( $collection['save_enabled'] ))
		{
			$this->save_enabled = $collection['save_enabled'];
			setcookie( "save_enabled", $this->save_enabled, time() + 86400*365*5 );
			$this->debugInfo .= sprintf( "<p>Set save_enabled from args to %d</p>\n", $this->save_enabled );
			$this->debugShort = sprintf( "set-save_enabled=%d,req-save_enabled=[%s]", $this->save_enabled, $collection['save_enabled'] );
		}
		else if (isset( $collection['save_disabled'] ))
		{
			$this->save_enabled = $collection['save_disabled'] ? 0 : 1;
			setcookie( "save_enabled", $this->save_enabled, time() + 86400*365*5 );
			$this->debugInfo .= sprintf( "<p>Got save_disabled in args, new ena=%d</p>\n", $this->save_enabled );
			$this->debugShort = sprintf( "set-save_disabled=%d,enabled=%d", $collection['save_disabled'], $this->save_enabled );
		}
		else if (!isset( $_COOKIE['save_enabled'] ))
		{
			// Default to save not enabled with expiry 30 days from now
			setcookie( "save_enabled", "0", time() + 86400*30 );
			$this->save_enabled = 0;
			$this->debugInfo .= sprintf( "<p>No save_enabled in cookies, default to 0</p>\n" );
			$this->debugShort = "set-save_enabled-default=0";
		}
		else
		{
			$this->save_enabled = $_COOKIE['save_enabled'];
			$this->debugInfo .= sprintf( "<p>Got save_enabled=%d from cookies</p>\n", $this->save_enabled );
			$this->debugShort = sprintf( "set-save_enabled-cookie=%d", $this->save_enabled );
		}

        if ($dbgx & 0x20)
        {
            $this->debugInfo .= "<pre>\nCollection dump:\n";
            $this->debugInfo .= print_r( $collection, true );
            $this->debugInfo .= "\nCookie dump:\n";
            $this->debugInfo .= print_r( $_COOKIE, true );
            if (isset( $_SESSION ))
            {
				$this->debugInfo .= "\nSession dump:\n";
				$this->debugInfo .= print_r( $_SESSION, true );
			}
			else
			{
				$this->debugInfo .= "\n(session_init not called)\n";
			}
            if (isset( $plugins['drupal'] ))
            {
				global $user;
				if (isset( $user ))
				{
					$this->debugInfo .= "\nDrupal user:\n";
					$this->debugInfo .= print_r( $user, true );
				}
				else
				{
					$this->debugInfo .= "Drupal plugin present but no user info\n";
				}
            }
            else
            {
				$this->debugInfo .= "Drupal plugin not present\n";
            }
            $this->debugInfo .= "\n</pre>\n";
        }

		if ($this->save_enabled)
		{
		  $deleteCookie = 0;
		  if (isset( $collection['save_new'] ) && $collection['save_new'])
		  {
			$this->saved = time();
		  }
		  else if (isset( $_REQUEST['save_id'] ))
		  {
			$this->saved = $_REQUEST['save_id'];
		  }
		  else if (isset( $_REQUEST['delete_id'] ))
		  {
			$this->saved = $_REQUEST['delete_id'];
			$deleteCookie = 1;
		  }
		  if ($this->saved)
		  {
			  // Delete by setting cookie to blank data with expiration an hour ago
			  if ($deleteCookie)
			  {
				setcookie( "save_data[{$this->saved}]", "", time() - 3600 );
			  }
			  else
			  {
				  // Construct data to save as query
				  $this->add_update = "$this->saved";
				  $org_data = $url_with_name;
				  if ($org_data) $org_data .= "&";
				  $this->new_data = "{$org_data}save_id={$this->saved}";
				// When updating or adding, set expiration to 5 years from now
				setcookie( "save_data[{$this->saved}]", $this->new_data, time() + 86400*365*5 );
			  } // Add or update
		  }
		}
	}

	// Perform Javascript initialization
	function IssueJSInit()
	{
        global $dbgx;
		$s = sprintf( "  setSaveEnabled(%d);\n", $this->save_enabled );
		if (isset( $_COOKIE['save_data'] ))
		{
            if ($dbgx & 0x20)
            {
                print( "/******\n" );
                print_r( $_COOKIE );
                print( "\n********/\n" );
            }
			foreach ($_COOKIE['save_data'] as $key => $value)
			{
				// Supersede with newer data (from update)
				if ($this->add_update != "" && $this->add_update == $key)
				{
					$value = $this->new_data;
					$this->add_update = "";
				}

				$s .= sprintf( "  addSavedItem(%d,'%s');\n", $key, str_replace('\'', '\\\'', urldecode($value)) );
			}
		}
		else if ($dbgx & 0x20)
		{
			print( " // save_data not defined in cookies\n/*************\n" );
			print_r( $_COOKIE );
			print( "\n**********/\n" );
		}
        // If we've added a new save, add that as well
        if ($this->add_update != "") $s .= sprintf( "  addSavedItem(%d, '%s');\t// New item\n", $this->add_update, str_replace('\'', '\\\'', $this->new_data) );
		return $s;
	}

}

// Trailing tag intentionally ommitted
