<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<title>Jyotish links</title>
<script language="javascript">
function setWVis(state)
{
  var w = document.getElementById("w");
  if (state=='toggle')
  {
	if (w.style.visibility=='visible') state='hidden';
	else state='visible';
  }
  w.style.visibility = state;
}
</script>
</head>
<body bgcolor="#ffffff">
<div name="main">
<h3>Jyotish links</h3>
<p><a href="achart.php">Free basic chart calculation</a></p>
<p><a href="http://sandiegocollegeofayurveda.net">San Diego College of Ayurveda home page</a></p>
<!-- <p><a href="index.php">Refresh</a></p> -->
<p>Northern chart html display test: <a href="javascript:setWVis('visible')">Show</a> | <a href="javascript:setWVis('hidden')">Hide</a> | <a href="javascript:setWVis('toggle')" title="Switch between hidden and visible">Toggle</a></p>
</div>
<!-- Try issuing style element within body -->
<!-- Using background image does not print:
	background: url(images/northern_bg.png) no-repeat;
-->
<style type="text/css">
div.chart {
	position:relative;
	width:805px;
	height:805px;
	padding: 5px;
	z-index:1;
}
div.slot {
	position:absolute;
	font-size:10px;
	background:#e3f14f;
	z-index:100;
	border:1px solid gray;
}
</style>
<div id="w" class="chart" style="visibility:hidden;">
<img src="images/northern_bg.png"/>
<?php
// Issue text at specified position with align one of left, right, center
// Coordinates are interpreted according to alignment (top, middle, bottom)
function issueText( $text, $x, $y, $align="left", $valign="top", $boxwidth=80, $boxheight=20 )
{
  // Interpret negative as offset from left/bottom edge
  if ($x<0) $x += 800;
  if ($y<0) $y += 800;
  // Adjust position based on selected alignment
  if ($align=="center") $x -= ($boxwidth/2);
  else if ($align=="right") $x -= $boxwidth;
  if ($valign=="middle") $y -= ($boxheight/2);
  else if ($valign=="bottom") $y -= $boxheight;
  printf( "<div class=\"slot\" style=\"left:%dpx;top:%dpx;width:%dpx;height:%dpx;text-align:%s;vertical-align:%s;\">%s</div>\n",
	$x, $y, $boxwidth, $boxheight, $align, $valign, $text );
}

// Issue text echoing position
function issue( $x, $y, $pref, $align="left", $valign="top" )
{
  issueText( sprintf( "%s@%d,%d", $pref, (int)$x, (int)$y ), $x, $y, $align, $valign );
}

// Slope text from start point to end point with specified alignment
function slopeText( $xStart, $yStart, $xEnd, $yEnd, $pref, $align, $valign )
{
  $x = $xStart;
  $y = $yStart;
  $xinc = 20;
  $yinc = 20;
  if ($xEnd < $xStart) $xinc = -$xinc;
  else if ($xEnd == $xStart) $xinc = 0;
  if ($yEnd < $yStart) $yinc = -$yinc;
  else if ($yEnd == $yStart) $yinc = 0;
  while (  ( ($xinc < 0 && $x > $xEnd) || ($xinc > 0 && $x < $xEnd) || $xinc == 0 )
		&& ( ($yinc < 0 && $y > $yEnd) || ($yinc > 0 && $y < $yEnd) || $yinc == 0 ))
  {
	issue( $x, $y, $pref, $align, $valign );
	$x += $xinc;
	$y += $yinc;
  }
}

// Slope text from start point with specified x and y increments and alignment
function slopeVector( $xStart, $yStart, $xinc, $yinc, $count, $pref, $align, $valign )
{
  $x = $xStart;
  $y = $yStart;
  for ($n = 0; $n < $count; $n++)
  {
	issue( $x, $y, $pref, $align, $valign );
	$x += $xinc;
	$y += $yinc;
  }
}

/********
issueText( "@5,5L", 5, 5 );
issueText( "@5,20L", 5, 20 );
issueText( "@795,5R", -5, 5, "right" );
issueText( "@400,400C", 400, 400, "center", "middle" );
issueText( "@795,795R", -5, -5, "right", "bottom" );
issueText( "@5,795L", 5, -5, "left", "bottom" );
*********/

/**********
____________
|\ 2 /^\12/ |
|3 \/ 1 \/11|y=800/4
| / \  /  \ |
|/4   X 10 \|
|\   / \   /|
| \ /7  \ /9|
|5/ \  /  \ |
|/_6_\/__8_\|
***********/
// h1
//slopeText( 200+15, 200-15, 200+15+140, 200-15-140, "h1", "left", "bottom" );
//slopeText( 200+10, 200+10, 200+10+160, 200+10+160, "h1", "left", "top" );
slopeVector( 200+37, 200, 20, -20, 4, "h1b", "left", "bottom" );
slopeVector( 200+22, 200, 20, 20, 4, "h1t", "left", "top" );
slopeVector( 600-30, 200, -20, -20, 4, "h1br", "right", "bottom" );
slopeVector( 600-30, 200, -20, 20, 4, "h1tr", "right", "top" );
slopeVector( 400, 200-40, 0, 20, 5, "h1c", "center", "top" );
// h2
//slopeText( 200, 5, 200, 5+160, "h2", "center", "top" );
slopeVector( 200, 15, 0, 20, 7, "h2", "center", "top" );
slopeVector( 100, 15, 0, 20, 1, "h2l", "center", "top" );
slopeVector( 300, 15, 0, 20, 1, "h2r", "center", "top" );
// h3
//slopeText( 15, 200, 15, 200-140, "h3b", "left", "bottom" );
//slopeText( 15, 200, 15, 200+140, "h3t", "left", "top" );
slopeVector( 15, 200, 0, -20, 5, "h3b", "left", "bottom" );
slopeVector( 15, 200, 0, 20, 5, "h3t", "left", "top" );
// h4
slopeVector( 30, 405, 20, -20, 4, "h4b", "left", "bottom" );
slopeVector( 30, 405, 20, 20, 4, "h4t", "left", "top" );
slopeVector( 400-30, 405, -20, -20, 4, "h4br", "right", "bottom" );
slopeVector( 400-30, 405, -20, 20, 4, "h4tr", "right", "top" );
slopeVector( 200, 400-40, 0, 20, 5, "h4c", "center", "top" );
// h5
slopeVector( 15, 600, 0, -20, 5, "h5b", "left", "bottom" );
slopeVector( 15, 600, 0, 20, 5, "h5t", "left", "top" );
// h6
slopeVector( 200, 795, 0, -20, 7, "h6", "center", "bottom" );
slopeVector( 100, 795, 0, -20, 1, "h6l", "center", "bottom" );
slopeVector( 300, 795, 0, -20, 1, "h6r", "center", "bottom" );
// h7
slopeVector( 200+37, 600-4, 20, -20, 4, "h7b", "left", "bottom" );
slopeVector( 200+22, 600-4, 20, 20, 4, "h7t", "left", "top" );
slopeVector( 600-30, 600-4, -20, -20, 4, "h7br", "right", "bottom" );
slopeVector( 600-30, 600-4, -20, 20, 4, "h7tr", "right", "top" );
slopeVector( 400, 600-40, 0, 20, 5, "h7c", "center", "top" );
// h8
slopeVector( 500, 795, 0, -20, 1, "h8l", "center", "bottom" );
slopeVector( 600, 795, 0, -20, 7, "h8", "center", "bottom" );
slopeVector( 700, 795, 0, -20, 1, "h8r", "center", "bottom" );
// h9
slopeVector( 795, 600, 0, -20, 5, "h9b", "right", "bottom" );
slopeVector( 795, 600, 0, 20, 5, "h9t", "right", "top" );
// h10
slopeVector( 755, 405, -20, -20, 4, "h10b", "right", "bottom" );
slopeVector( 755, 405, -20, 20, 4, "h10t", "right", "top" );
slopeVector( 400+30, 405, 20, -20, 4, "h10bl", "left", "bottom" );
slopeVector( 400+30, 405, 20, 20, 4, "h10tl", "left", "top" );
slopeVector( 600, 400-40, 0, 20, 5, "h10c", "center", "top" );
// h11
slopeVector( 795, 200, 0, -20, 5, "h11b", "right", "bottom" );
slopeVector( 795, 200, 0, 20, 5, "h11t", "right", "top" );
// h12
slopeVector( 500, 15, 0, 20, 1, "h12l", "center", "top" );
slopeVector( 600, 15, 0, 20, 7, "h12", "center", "top" );
slopeVector( 700, 15, 0, 20, 1, "h12r", "center", "top" );
/**********
// Slope down from top right to bottom left at 45 degrees
slopeText( 795, 5, 5, 795, "left", "top" );
// Slope down from top left to bottom right (45d)
slopeText( 5, 5, 795, 795, "left", "top" );
// Slope down from left mid to bottom mid
slopeText( 5, 400, 400, 795, "left", "bottom" );
// Slope up from bottom mid to right mid
slopeText( 400, 795, 795, 400, "left", "bottom" );
// Slope up from right mid to top mid
slopeText( 795, 400, 400, 5, "left", "top" );
// Slope down from top mid to left mid
slopeText( 400, 5, 5, 400, "left", "top" );
**************/
?>
</div>
<!-- <p>Floating at the bottom</p> -->
<!-- <img src="images/northern_bg.png"/> -->
</body>
</html>
