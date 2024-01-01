#!/usr/bin/env python
# Process shapefile against grid:
# Bands are:
# 0: -90 - -66
# 1: -66 - -22
# 2: -22 - +22
# 3: +22 - +66
# 4: +66 - +90
# Segments are numbered starting from the 15 degree lune centered on 165w (0) to the
# 15 degree line centered on 180e (23)
# Syntax: python shapegrid.py <shapefile-base>

import sys
import os
import os.path
import re
import shapefile

my_version = "1.06"

# Define all functions
def InBounds(lat,lng,minlat,maxlat,minlong,maxlong):
  if minlat<maxlat:
    if lat<minlat or lat>maxlat:
      return False
  else:
    if lat>maxlat and lat<minlat:
      return False
  if minlong<maxlong:
    return (lng>=minlong and lng<maxlong)
  else:
    return (lng<maxlong or lng>=minlong)

# End function definitions

# Begin main body of execution
#print 'shapegrid.py version ' + my_version

if len(sys.argv) < 2:
 print 'Syntax: ' + sys.argv[0] + ' <shapefile-base>'
 sys.exit(2)

r = shapefile.Reader(sys.argv[1])
shapes = r.shapes()
records = r.records()
print '// shapegrid ' + my_version + '; read ' + str(len(shapes)) + ' from ' + sys.argv[1]
print 'var __uninhabited = ["America/Los_Angeles","America/Chicago","America/Mexico_City","America/New_York","America/Sao_Paulo","Europe/London","Europe/Paris","Europe/Rome","Asia/Calcutta","Asia/Tokyo"];'
#print 'shapes[0].points=' + str(shapes[0].points)
#print 'records[0]=' + str(records[0])
bandMargin = 0.5
print 'var __bandmargin = ' + str(bandMargin) + ';'
# Define start of band boundaries, which will overlap by __bandmargin
bands = [-90, -66, -48, -42, -36, -30, -22, -12, 0, 12, 22, 30, 36, 42, 48, 66, 90 ]
#           24    16   6    6    6   8     10  12  ,,,
bandcount = len(bands)-1
print 'var __bandcount = ' + str(bandcount) + ';'
print 'var __bands = [',
for band in range(bandcount):
  print '[' + str(bands[band]) + ',' + str(bands[band+1]) + ']',
  if band < bandcount-1:
  	print ',',
  else:
	print '];'

print 'var g_tz = new Array();'
print ''
sys.stdout.flush()

maxzoneband = -1
maxzonebandlune = -1
maxzonebandlunecount = 0
for band in range(bandcount):
  minlat=bands[band] - bandMargin
  maxlat=bands[band+1] + bandMargin
  maxzonecount = 0
  maxzonelune = -1
  print 'g_tz[' + str(band) + '] = new Array();'
  for lune in range(24):
    minlong=15 * lune + 7.5 - 180
    maxlong=15 * (lune + 1) + 7.5 - 180
    if minlong<-180:
      minlong = minlong + 360.0
    if maxlong<-180:
      maxlong = maxlong + 360.0
    if minlong>180:
      minlong = minlong - 360.0
    if maxlong>180:
      maxlong = maxlong - 360.0

    print '// ' + str(band) + ',' + str(lune) + ' [' + str(minlat) + ',' + str(maxlat) + '; ' + str(minlong) + ',' + str(maxlong) + ']',

    lastzone = ''
    zonelist = []
    zonecount = 0
    for n in range(len(shapes)):
      found = 0
      for np in range(len(shapes[n].points)):
        if InBounds(shapes[n].points[np][1], shapes[n].points[np][0], minlat, maxlat, minlong, maxlong):
          found = found + 1
          break
      if found>0 and lastzone!=records[n][0] and records[n][0]!='uninhabited':
        zonelist.append(records[n][0])
        lastzone = records[n][0]
        # We don't have any of these
        if len(records[n])>1:
          print ' *** len=' + str(len(records[n])) + ': ' + str(records[n])
    if len(zonelist)==0:
      print ''
      print 'g_tz[' + str(band) + '][' + str(lune) + ']= __uninhabited;'
    else:
      zonelist.sort()
      lastzone = ''
      datastr = ''
      for np in range(len(zonelist)):
        if np==0:
          datastr = '"' + zonelist[np] + '"'
        elif zonelist[np]==lastzone:
          continue
        else:
          datastr = datastr + ', "' + zonelist[np] + '"'
        lastzone = zonelist[np]
        zonecount = zonecount + 1
      print ' count=' + str(zonecount)
      print 'g_tz[' + str(band) + '][' + str(lune) + ']=[' + datastr + '];'
      if zonecount>maxzonecount:
	    maxzonecount = zonecount
	    maxzonelune = lune
      if lune % 6 == 0:
      	sys.stdout.flush()

  print '// band ' + str(band) + ' max zones was ' + str(maxzonecount) + ' in lune ' + str(maxzonelune)
  print ''
  sys.stdout.flush()
  if maxzonecount>maxzonebandlunecount:
    maxzonebandlunecount = maxzonecount
    maxzoneband = band
    maxzonebandlune = maxzonelune

print '// band ' + str(maxzoneband) + ' had the maximum zones in lune ' + str(maxzonebandlune) + ' (' + str(maxzonebandlunecount) + ')'
