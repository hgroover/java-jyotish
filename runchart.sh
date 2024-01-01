#!/bin/sh
# Wrapper to run a chart headless

# Log all command invocations
MYDIR=$(dirname $0)
LOGPATH=${MYDIR}/log
LOGFILE=${LOGPATH}/rc-$(date +'%Y%m').log

syntax()
{
  echo "$*"
  echo "Syntax: [SUBJECT_NAME=name] [CACHE={0|1}] runchart.sh output YYYY MM DD HHMM latitude longitude timezone dst"
  echo "output is basename of .html, .code, .tmp (raw output) and .tmperr (stderr output); auto to generate from timestamp"
  echo "Latitude format is ddNmm or ddSmm"
  echo "Longitude is dddWmm or dddEmm"
  echo "Time zone is -hhmm for west of GMT, hhmm for east"
  echo "DST is 0 or 1"
  exit 1
}

[ -d ${LOGPATH} ] || { mkdir -p ${LOGPATH}; echo "Begin log with ${LOGFILE}" >> ${LOGFILE}; }
echo "$0 $*" >> ${LOGFILE}

OUTPUT=$1
YEAR=$2
MONTH=$3
DAY=$4
TIME=$5
latitude=$6
longitude=$7
timezone=$8
dst=$9
[ "${dst}" ] || syntax "Not enough arguments: you specified OUTPUT=${OUTPUT} y-m-d=${YEAR}-${MONTH}-${DAY} time=${TIME} lat/lon=${latitude}/${longitude} timezone=${timezone} dst=${dst}"

case ${OUTPUT} in
 auto|Auto|AUTO)
	OUTPUT=.out/$(date +'%Y%m%d/%H%M%S.%N')
	[ -d $(dirname ${OUTPUT}) ] || mkdir -p $(dirname ${OUTPUT})
	;;
esac

# Options
DIVS=1,
CACHEDIR=.rc-cache
[ "${CACHE}" ] || CACHE=1
# Directory must exist for readlink -e to work
[ -d ${CACHEDIR} ] || mkdir -p ${CACHEDIR} || { echo "Failed to create cache dir ${CACHEDIR}"; exit 1; }
CACHEDIR=$(readlink -e ${CACHEDIR})
CACHEFILEBASE=d${dst}_${timezone}_${latitude}_${longitude}
CACHEFILEDIR=${YEAR}/${MONTH}/${DAY}/${TIME}
CACHEPATH=${CACHEDIR}/${CACHEFILEDIR}/${CACHEFILEBASE}
[ -d ${CACHEDIR}/${CACHEFILEDIR} ] || mkdir -p ${CACHEDIR}/${CACHEFILEDIR} || { echo "Failed to create ${CACHEDIR}/${CACHEFILEDIR}"; exit 1; }
INCACHE=0
[ -s ${CACHEPATH}.raw ] && INCACHE=1
[ ${CACHE} = 0 -a ${INCACHE} = 1 ] && { echo "CACHE set to ${CACHE} - forcing re-creation of cached entry"; INCACHE=0; }

# Optional values in environment
SUBJECT_NAME_SED="$(echo "${SUBJECT_NAME}" | sed 's:/:\\/:g')"

# Do output as a single block to support using named pipes
{
# ${OUTPUT} must exist, even empty, for readlink -e to work
[ -e ${OUTPUT} ] || touch ${OUTPUT}
# Note that realpath is not on CentOS - use readlink -e for same functionality
OUTPUT=$(readlink -e ${OUTPUT})
echo "Output path ${OUTPUT}"
echo "Output path ${OUTPUT}" >> ${LOGFILE}

  # If ${OUTPUT} was created empty solely to get full path, delete it
  [ -s ${OUTPUT} ] || rm ${OUTPUT}
  PHP_DIR=.
  [ -r src/achart-parashara.inc.php ] && PHP_DIR=src
  if [ ${INCACHE} = 0 ]
  then
#	#echo "Using cached copy from ${CACHEPATH}.*"
#	#ls -l ${CACHEPATH}.*
#  else
	#echo "Creating cache entry ${CACHEPATH}"
# Subdir of current dir where AChart-<version>.jar resides
JAR_DIR=
[ -d src ] && JAR_DIR=src/

ACHART_VER=154
MYDIR=$(dirname $0)
#CP="-cp AChart-${ACHART_VER}.jar:${MYDIR}/src:${MYDIR}/src/SwissEph-2011.jar:/usr/share/icedtea-web/plugin.jar"
CP="-cp $(readlink -e ${MYDIR}/${JAR_DIR}AChart-${ACHART_VER}.jar):$(readlink -e ${MYDIR}):$(readlink -e ${MYDIR}/${JAR_DIR}SwissEph-2011.jar)"
#echo "Lat: ${latitude} Longitude: ${longitude} TZ: ${timezone} DST: ${dst}"

# Swiss ephemeris wants ephe in current dir. We should have every path in absolute form now
[ "${JAR_DIR}" ] && cd ${JAR_DIR}

  DATE=$(echo ${YEAR} ${MONTH} ${DAY} | awk '{printf "%04d%02d%02d\n", $1, $2, $3;}')
  #echo "${DATE} ${TIME} cwd=$(pwd)"
  #java ${CP} AChart "Description=Test chart" time=${TIME} date=${DATE} latitude=38N50 longitude=77W00 DST=0 TZ=-0500 style=North TextOnly >> ${OUTPUT}
  java -Djava.awt.headless=true ${CP} AChartRun "Description=__SUBJECT_NAME__" time=${TIME} date=${DATE} latitude=${latitude} longitude=${longitude} DST=${dst} TZ=${timezone} style=North HtmlDivs=${DIVS} TextOnly > ${CACHEPATH}.raw 2> ${CACHEPATH}.err
  fi
  #	DATA="$(gawk -f ../rasi-points.awk < ${OUTPUT}.tmp)"
  #	echo -e "${DATE}\t${TIME}\t${DATA}" >> ${OUTPUT}
  awk '/^--- browserOut/ {p=1; next;} /^=== end browserOut/ {p=0;} p;' ${CACHEPATH}.raw > ${OUTPUT}.html
  sed -i 's:</body></html>::' ${OUTPUT}.html
  awk '/^--- code/ {p=1; next;} /^---/ {p=0;} p;' ${CACHEPATH}.raw > ${OUTPUT}.code
  awk '/^--- sadbala/ {p=1; next;} /^---/ {p=0;} p;' ${CACHEPATH}.raw > ${OUTPUT}.sadbala
  awk '/^--- dashas/ {p=1; next;} /^---/ {p=0;} /^</ {if (p==1) p++;} {if (p==2) print;}' ${CACHEPATH}.raw > ${OUTPUT}.dashas
  # Recombine
  [ -s ${OUTPUT}.code ] && echo "<p style=\"font-size: 8pt;\">Coded output: $(cat ${OUTPUT}.code)</p>" >> ${OUTPUT}.html
  [ -s ${OUTPUT}.sadbala ] && cat ${OUTPUT}.sadbala >> ${OUTPUT}.html
  [ -s ${OUTPUT}.dashas ] && cat ${OUTPUT}.dashas >> ${OUTPUT}.html
  [ "${SUBJECT_NAME}" ] && sed -i "s/__SUBJECT_NAME__/${SUBJECT_NAME_SED}/g" ${OUTPUT}.html
  if [ -r ${PHP_DIR}/achart-parashara.inc.php -a -s ${OUTPUT}.code ]
  then
	cp ${PHP_DIR}/achart-parashara.inc.php ${OUTPUT}.php
	echo "\$a = EvaluateParashara(\"$(cat ${OUTPUT}.code)\"); foreach (\$a as \$s) { printf(\"<p>%s</p>\n\", Translit(\$s,1,1)); }" >> ${OUTPUT}.php
        php ${OUTPUT}.php >> ${OUTPUT}.html 2>> ${LOGFILE}
  else
        echo "<!-- no parashara.inc in ${PHP_DIR}, pwd = $(pwd) -->" >> ${OUTPUT}.html
  fi
  echo "</body></html>" >> ${OUTPUT}.html
}

