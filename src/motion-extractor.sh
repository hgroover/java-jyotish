#!/bin/sh
# motion-extractor.sh - Extract raw motion values for
#  sun - 	index 0
#  moon -	index 1
#  mercury -	index 3
#  venus -	index 4
#  mars -	index 5
#  jupiter -	index 6
#  saturn -	index 7

OUTPUT=$1
START_DATE=$2
END_DATE=$3
MINUTES=$4

[ "${MINUTES}" ] || MINUTES=15
[ "${END_DATE}" ] || { echo "Syntax: $0 output-basename start-date end-date [minute-increment]"; exit 1; }

START_S=$(TZ=UTC date --date="${START_DATE}" +'%s')
END_S=$(TZ=UTC date --date="${END_DATE}" +'%s')
if [ ${END_S} -le ${START_S} ]
then
  echo "Start date ${START_DATE} epoch ${START_S} is after"
  echo "end date   ${END_DATE} epoch ${END_S}"
  exit 1
fi

END_S=$(expr ${END_S} + 86400)

echo "Time,Sun,Moon,Mercury,Venus,Mars,Jupiter,Saturn,Sun L,Moon L" | tee ${OUTPUT}.csv
OUTPUT=$(readlink -e ${OUTPUT}.csv)
OUTPUT=$(basename ${OUTPUT} .csv)

ACHART_VER=153
MYDIR=$(dirname $0)
#CP="-cp AChart-${ACHART_VER}.jar:${MYDIR}/src:${MYDIR}/src/SwissEph-2011.jar:/usr/share/icedtea-web/plugin.jar"
CP="-cp $(readlink -e ${MYDIR}/${JAR_DIR}AChart-${ACHART_VER}.jar):$(readlink -e ${MYDIR}):$(readlink -e ${MYDIR}/${JAR_DIR}SwissEph-2011.jar)"

# Calculate all times in UTC
latitude=38N50
longitude=77W04
timezone=-0500
dst=0

TIME_S=${START_S}
while [ ${TIME_S} -lt ${END_S} ]
do
  DATE=$(TZ=UTC date --date=@${TIME_S} +'%Y%m%d')
  TIME=$(TZ=UTC date --date=@${TIME_S} +'%H%M')
  java -Djava.awt.headless=true ${CP} AChartRun "Description=__SUBJECT_NAME__" time=${TIME} date=${DATE} latitude=${latitude} longitude=${longitude} DST=${dst} TZ=${timezone} style=North HtmlDivs=,1, TextOnly > ${OUTPUT}.tmp 2> ${OUTPUT}.tmperr
  eval $(awk '/^index 0 / {printf "SUN_L=%s; SUN_RA=%s; SUN_M=%s; ", $6, $8, $10;}
/^index 1 / {printf "MOON_L=%s; MOON_RA=%s; MOON_M=%s; ", $6, $8, $10;}
/^index 3 / {printf "MERCURY_L=%s; MERCURY_RA=%s; MERCURY_M=%s; ", $6, $8, $10;}
/^index 4 / {printf "VENUS_L=%s; VENUS_RA=%s; VENUS_M=%s; ", $6, $8, $10;}
/^index 5 / {printf "MARS_L=%s; MARS_RA=%s; MARS_M=%s; ", $6, $8, $10;}
/^index 6 / {printf "JUPITER_L=%s; JUPITER_RA=%s; JUPITER_M=%s; ", $6, $8, $10;}
/^index 7 / {printf "SATURN_L=%s; SATURN_RA=%s; SATURN_M=%s; ", $6, $8, $10;}
END {print "\n";}' ${OUTPUT}.tmp)
  echo "${DATE} ${TIME},${SUN_M},${MOON_M},${MERCURY_M},${VENUS_M},${MARS_M},${JUPITER_M},${SATURN_M},${SUN_L},${MOON_L}" | tee -a ${OUTPUT}.csv
  TIME_S=$(expr ${TIME_S} + \( ${MINUTES} \* 60 \) )
done

