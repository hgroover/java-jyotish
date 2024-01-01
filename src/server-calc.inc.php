<?php
// server-calc.inc.php - encapsulation of server calc using runchart.sh

function VarNotEmpty($varname)
{
  if (isset( $_REQUEST[$varname] ) && $_REQUEST[$varname] != "")
  {
    return true;
  }
  return false;
}

function HasServerCalcVars()
{
  // Because of checkbox submit value semantics, we may not have dst so default it to "0"
  if (VarNotEmpty( "date" )
    || (VarNotEmpty( "year" ) && VarNotEmpty( "month" ) && VarNotEmpty("day")) )
        return VarNotEmpty("time")
        && VarNotEmpty("lat")
        && VarNotEmpty("lon")
        && VarNotEmpty("tz")
        // && VarNotEmpty("dst")
        ;
  return false;
}

function ServerCalc( $name, $year, $month, $day, $time, $lat, $lon, $tz, $dst = "0" )
{
    global $content_id;
    global $keep_content;
    if ($name == "") $name = "(anonymous)";
    if ($year == "" || $month == "" || $day == "" || $time == "")
    {
        return "<h5>Error: date/time not specified</h5>";
    }
    if ($lat == "" || $lon == "" || $tz == "" || $dst == "")
    {
        return "<h5>Error: lat/lon/tz/dst ({$lat}/{$lon}/{$tz}/{$dst}) not specified</h5>";
    }

    $cmd = "SUBJECT_NAME=" . escapeshellarg($name) . " /home/hgweb/jj/runchart.sh auto";
    $cmd .= sprintf( " %04d", $year );
    $cmd .= sprintf( " %02d", $month );
    $cmd .= sprintf( " %02d", $day );
    $cmd .= sprintf( " %04d", $time );
    $cmd .= (" " . escapeshellarg($lat));
    $cmd .= (" " . escapeshellarg($lon));
    $cmd .= (" " . escapeshellarg($tz));
    $cmd .= sprintf( " %d", $dst );
    $output = shell_exec( $cmd );

    // We should have Output path ...
    $content_id = "";
    if (!strncmp( $output, "Output path", 11 ))
    {
        $a = explode( " ", $output );
        if (sizeof($a) >= 3)
        {
            $content_path = trim($a[2]);
            if (preg_match( '/\.out\/([0-9]+\/[0-9]+\.[0-9]+)$/', $content_path, $acm ))
                    $content_id = str_replace( "/", "-", $acm[1] );
        }
    }

    if ($content_id == "") return "<h5>Failed to get output path from {$output}</h5>";

    $s = "";

    // Return only contents of <body>...</body>
    //readfile( trim($a[2]) . ".html" );
    $ac = @file( "{$content_path}.html" );
    // Perform some editing
    $jj_ver = "";
    $inbody = 0;
    for ($n = 0; $n < sizeof($ac); $n++)
    {
        if (!$inbody)
        {
            if (strstr( $ac[$n], "<body>" ))
            {
                $inbody = 1;
            }
            continue;
        }
        if (preg_match( '/^<h1>(Java J.+)<\/h1>$/', $ac[$n], $acm ))
        {
                $jj_ver = $acm[1];
                continue;
        }
        if (preg_match( '/^<h3>(.+)<\/h3>$/', $ac[$n], $acm ) && $jj_ver != "")
        {
                $s .= sprintf( "<h1>%s</h1>\n", $acm[1] );
                $s .= sprintf( "<h5>%s</h5>\n", $jj_ver );
                continue;
        }
        if (preg_match( '/^<\/body><\/html>$/', $ac[$n] ))
        {
                // End of body
                return $s;
        }
        $s .= $ac[$n];
    } // for all lines

    // Did not find body
    if (!$inbody) return "<h5>Contents empty</h5>" . $s;

    // Did not get expected end
    $s .= "<!-- end of body not found -->";

    return $s;
} // ServerCalc()

function ServerCalcDate( $name, $date, $time, $lat, $lon, $tz, $dst = "0" )
{
  return ServerCalc( $name, substr($date,0,4), substr($date,4,2), substr($date,6,2), $time, $lat, $lon, $tz, $dst );
} // ServerCalcDate()

