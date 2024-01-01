<?php
// Script for java output
//phpinfo();
$do_logging = 0;
if (isset( $_REQUEST["do_logging"] )) $do_logging = $_REQUEST["do_logging"];
if ($do_logging)
{
	$log = fopen( "logs/post.log", "a" );
	fprintf( $log, "POST:\n%s\n", print_r($_POST,TRUE) );
	fprintf( $log, "SERVER:\n%s\n", print_r($_SERVER,TRUE) );
	//fprintf( $log, "REQUEST:\n%s\n", print_r($_REQUEST,TRUE) );
}
// Default action is to read raw post data
// Also allow posting fields
if (isset( $_REQUEST["data"] ))
{
	$raw = $_REQUEST["data"];
	$via = "post";
}
else
{
	$raw = file_get_contents("php://input");
	$via = "input";
}
if ($do_logging)
{
	fprintf( $log, "INPUT LENGTH: %d via %s\n", strlen($raw), $via );
	//fprintf( $log, "INPUT:\n%s\n", $raw );
	fclose( $log );
}

print $raw;

// Closing tag intentionally omitted
