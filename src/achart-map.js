// Map functions for achart.php

var js_map_ver = 9;

var myLatlng;
var g_map;
var g_marker;
var g_mapOK = 0;
var g_geoCoder;

  function initialize_map(lat,lon) {
    // Check for failure to load google maps (possibly working offline)?
    // FIXME offer some alternative location picker using local data
    try
    {
    myLatlng = new google.maps.LatLng(lat, lon);
    var myOptions = {
      zoom: 10,
      center: myLatlng,
      mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    g_map = new google.maps.Map(document.getElementById("map_canvas"),
        myOptions);
	g_marker = new google.maps.Marker({
      position: myLatlng,
      map: g_map,
      title:"Click on the map to move, \r\nclick on marker to make this \r\nthe current position"
	});
	// Drop marker and make it the current location
	google.maps.event.addListener(g_map, 'click', function(event) {
		placeMarker(event.latLng);
	});
	// Clicking on marker reasserts current location - may be useful if lat/long were
	// modified manually.
	google.maps.event.addListener(g_marker, 'click', function(event) {
		setPosition(event.latLng);
	});
        g_mapOK = 1;
        // Geocoder is optional
        g_geoCoder = new google.maps.Geocoder();
	}
	catch (e)
	{
        g_mapOK = -1;
	}
	if (g_mapOK < 0) try {
        var mapCanvas = document.getElementById("map_canvas");
        mapCanvas.innerHTML = '<p align="right">Failed to load Google Maps API. Enter latitude and longitude of position using degrees and minutes, then click <input type="button" value="Reload" onclick="reloadManually()"> to reload the timezone list below.</p>';
	}
	catch (e)
	{
	}
  }

function placeMarker(location) {
  g_marker.setPosition( location );
  if (g_dbg & 0x01)
  {
	dbgOut('<p>Drop marker:' + location.toString() + ' lat=' + newLat + ' lon=' + newLon + '</p>');
  }
  // Make this the current lat/long also
  setPosition(location);
}

function setPosition(location) {
	//alert("Selected location: " + location.toString());
	var newLat = DegreesMinutes(location.lat(), 'N', 'S', 1);
	var newLon = DegreesMinutes(location.lng(), 'E', 'W', 1);
	if (g_dbg & 0x01)
	{
		dbgOut('<p>selected:' + location.toString() + ' lat=' + newLat + ' lon=' + newLon + '</p>');
	}
	if (g_dbg&0x04)
	{
		var bandIndex = LatToBandIndex(location.lat());
		var segIndex = LonToSegmentIndex(location.lng());
		var shortName = 'Short';
		var longName = 'LongName';
		var zoomLevel = g_map.getZoom();
		dbgOut('<p>$qn[] = array("' + shortName + '", "' + longName + '", ' + location.lng() + ', ' + location.lat() + ", " + zoomLevel + ", " + bandIndex + ", " + segIndex + ', "' + g_tz[bandIndex][segIndex].toString() + '" ' + ");</p>\n");
		// Issue a geocoding request
		if (g_geoCoder != null)
		{
			var geoRequest = new Object;
			geoRequest.location = location;
			g_geoCoder.geocode( geoRequest, reverseGeocodeCallback );
		}
	}
	document.forms[0].txtLat.value = newLat;
	document.forms[0].txtLong.value = newLon;
	UpdateLink();
	UpdateZones();
}

// Callback for reverse geocoding request
function reverseGeocodeCallback(aResults, status)
{
	// aResults is an array of GeocoderResult objects
	// status is GeocoderStatus
	if (aResults.length < 1) return;
	if (status.toString() != 'OK')
	{
		dbgOut( '<p>geocode request returned ' + status.toString() + '</p>' );
		return;
	}
	var s = '';
	var n, i;
	for (n = 0; n < aResults.length; n++)
	{
		if (n > 0) s += ', ';
		s += 'types:';
		s += aResults[n].types.toString();
		s += ' # components:';
		s += aResults[n].address_components.length;
		s += ' ';
		for (i = 0; i < aResults[n].address_components.length; i++)
		{
			s += ' "';
			s += aResults[n].address_components[i].short_name;
			s += '", "';
			s += aResults[n].address_components[i].long_name;
			s += '"';
		}
	}
	dbgOut('<p>' + s + '</p>');
}

// Quick navigation support - pan map to specified location and zoom level
function navTo(zoom,lat,lon,band,lune,tzname)
{
   newCenter = new google.maps.LatLng(lat, lon);
   g_map.setCenter(newCenter);
   g_map.setZoom(zoom);
   placeMarker(newCenter);
   ApplyZoneIndices(band,lune,-1);
   assertTimezoneSelection(tzname);
}

// Assert selected timezone
function assertTimezoneSelection(tzname)
{
	var newIndex = assertOptionListValue(document.forms[0].tzName, tzname);
	document.forms[0].tzName.selectedIndex = newIndex;
	// Determine DST setting
    UpdateCurrentTimeOffsets( tzname );
}

// Reload timezone list when lat/lon have been entered manually
function reloadManually()
{
    // FIXME validate format and provide some feedback
	UpdateLink();
	UpdateZones();
}
