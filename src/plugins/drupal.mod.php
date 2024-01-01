<?php
// Drupal integration plugin module for achart
// This is a plugin for achart; if you're looking for the javajyotish-data module
// for Drupal 6, that's in ../drupal-modules

// Initialization for Drupal 6
if (isset( $drupal_root ))
{

if (!isset( $drupal_base ))
{
        $drupal_base = $_SERVER['DOCUMENT_ROOT'];
        if ($drupal_root!='/') $drupal_base .= $drupal_root;
}

$drupal_includes = $drupal_base . '/includes';
@set_include_path( get_include_path() . PATH_SEPARATOR . $drupal_includes );
$pre_drupal_cwd = getcwd();
chdir( $drupal_base );

/*********** This doesn't seem to be needed on either of the two installations I've tested on ***********
// Copied from drupal config
ini_set('session.cache_expire',     200000);
ini_set('session.cache_limiter',    'none');
ini_set('session.cookie_lifetime',  2000000);
ini_set('session.gc_maxlifetime',   200000);
ini_set('session.save_handler',     'user');
ini_set('session.use_only_cookies', 1);
ini_set('session.use_trans_sid',    0);
********************************************************************************************************/

/*********** jjdata_charts in mysql ************
+--------+------------------+------+-----+---------+----------------+
| Field  | Type             | Null | Key | Default | Extra          |
+--------+------------------+------+-----+---------+----------------+
| uid    | int(10) unsigned | NO   | PRI | NULL    |                |
| cid    | int(10) unsigned | NO   | PRI | NULL    | auto_increment |
| ckey   | int(10) unsigned | NO   |     | 0       |                |
| name   | varchar(128)     | YES  |     | NULL    |                |
| date   | varchar(8)       | YES  |     | NULL    |                |
| time   | varchar(6)       | YES  |     | NULL    |                |
| lat    | varchar(12)      | YES  |     | NULL    |                |
| lon    | varchar(12)      | YES  |     | NULL    |                |
| tz     | varchar(6)       | YES  |     | NULL    |                |
| tzname | varchar(64)      | YES  |     | NULL    |                |
| dst    | int(11)          | YES  |     | 0       |                |
+--------+------------------+------+-----+---------+----------------+

*************************************************/

$base_url = 'http://' . $_SERVER['HTTP_HOST'];
if ($drupal_root!='/') $base_url .= $drupal_root;
include_once 'bootstrap.inc';
include_once 'common.inc';
chdir( $pre_drupal_cwd );

$plugins['drupal'] = 1;

} // $drupal_root defined

// Required initialization entry - cannot issue text, returns any debug output
function drupal_plugin_init()
{
	global $drupal_base;
	global $user;
	$previousDir = getcwd();
	chdir( $drupal_base );
	//$ssret = session_start();
	drupal_bootstrap(DRUPAL_BOOTSTRAP_FULL);
	chdir( $previousDir );
	$s = "drupal plugin initialized in {$drupal_base}, restored {$previousDir}, base_url={$base_url}";
	//$s .= ", session_start() returned {$ssret}";
	if (isset( $user ))
	{
		$s .= ", user->name={$user->name}, uid={$user->uid}, mail={$user->mail}, timezone={$user->timezone}";
		$s .= ", roles=";
		$s .= print_r( $user->roles, true );
		//$s .= ", user dump:";
		//$s .= print_r( $user, true );
	}
	else
	{
		$s .= ", user not set";
	}
	return "<p>{$s}</p>\n";
}

// Closing tag intentionally ommitted
