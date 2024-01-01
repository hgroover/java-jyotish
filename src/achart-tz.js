// Timezone support functions and data

var js_tz_ver = 104;

// Translate digital term to degrees{sign}minutes
function DegreesMinutes(term,positiveSign,negativeSign,decimals)
{
  var a = Math.abs(term);
  var sD = '' + Math.floor(a);
  if (sD.length < 2) sD = '0' + sD;
  var dM = (a - Math.floor(a)) * 60.0;
  var dMult = 1.0;
  var minuteStrLength = 2;
  // Add one for the decimal point
  if (decimals > 0)
  {
	minuteStrLength++;
	minuteStrLength += decimals;
	while (decimals--) dMult *= 10.0;
  }
  // We need to multiply before rounding to ensure that we don't
  // mindlessly truncate 3.1999 to 3.1
  var sM = '0' + (Math.round(dM * dMult) / dMult);
  //alert('term='+term+' a='+a+' sD='+sD +' dM='+dM + ' dMult='+dMult+' mslen=' + minuteStrLength+' sM='+sM);
  // Get starting point, 2 places to left of decimal. Force presence of trailing zeroes to
  // right of decimal point even if we don't use them
  var start = 0;
  var point = sM.indexOf('.');
  if (point < 0) sM = sM + '.000000'
  point = sM.indexOf('.');
  if (point>=2)
  {
	start = point-2;
  }
  sM = sM.substr(start, minuteStrLength);
  if (term < 0.0)
  {
    return sD + negativeSign + sM;
  }
  else
  {
    return sD + positiveSign + sM;
  }
}

// Translate degrees{sign}minutes back to digital form
function DigitalDegrees(term)
{
  var s = term.toLowerCase();
  var sOff = s.indexOf('e');
  if (sOff < 0) sOff = s.indexOf('w');
  if (sOff < 0) sOff = s.indexOf('n');
  if (sOff < 0) sOff = s.indexOf('s');
  if (sOff < 0)
  {
	alert('Invalid geo term ' + term);
	return 0.0;
  }
  //alert('d=' + s.substr(0,sOff) + ' m=' + s.substr(sOff+1));
  var d = 1.0 * s.substr(0,sOff) + s.substr(sOff+1) / 60.0;
  var signValue = s.substr(sOff,1);
  if (signValue=='w' || signValue=='s') d = -d;
  return d;
}

// Translate signed digital lat/long to timezone cluster index
// Timezone clusters are numbered in bands using n = <signed lat> + 90
// Bands are defined in shapegrid.py and reflected into this source file
// along with data.
// Bands are divided into 24 segments ("lunes") using m = Math.floor((<signed longitude> + 180 - 7.5) / 15.0) % 24
// (corresponding to the basic timezone definition centered on 15 degree intervals starting with the
// 165 degrees west)
// Thus Greenwich (51.478105262843165, 0.0000000) is at the center of [3][11]
// and Bath (51.38035254077819, -2.355194091796875) is also at [3][11]
// Indices of other longitudes:
// -7.5 = 11; -7.51 = 10; -172.5 = 0; -172.51 = 23; 172.5 = 23, 172.49 = 22

function LatToBandIndex( latitude )
{
	var band;
	for (band=0; band<__bandcount; band++)
	{
		// Assume bands output by shapegrid.py are sorted from low to high
		// This allows us to check only the upper edge
		if (latitude < __bands[band][1])
		{
		  return band;
		}
	}
	// Fallthrough to Antarctica
	return 0;
}

function LonToSegmentIndex( longitude )
{
	// Handle negative wrap on far west / dateline longitudes
	return (24 + Math.floor( (longitude + 180 - 7.5) / 15.0 )) % 24;
}

// Raw data for relevant timezones now lives in achart-tzdata.js
// Produced using shapegrid.py by analyzing polygons for timezone, and if any point infringes on a
// bounded segment expanded by 0.5 degrees latitude on the north and south ends
// and expanded by 1 degree longitude on the east and west sides, include time zone

// Segments in each band start from the one from 172w30 - 157w30 (centered 155w00)
// ending with 172e30 - 172w30 (centered 180e00)

