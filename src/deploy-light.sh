#!/bin/sh
# Deploy to an existing directory on a web server using rsync
# Syntax: deploy-light.sh <rsync-dest>
# Example: ./deploy-light.sh joni@jonimitchell.com:public_html/astro
# Unlike deploy-rsync.sh this only copies the non-Java components

RSYNC_SPEC=$1
RSYNC2_SPEC=$2
[ "${RSYNC_SPEC}" ] || { echo "Syntax: $0 rsync-destination [runchart-destination]"; exit 1; }

# Change to directory
PROJECT_DIR=$(dirname $0)
cd ${PROJECT_DIR}

# package to temp dir
TEMPDIR=tmp.deploy.$$
[ -d ${TEMPDIR} ] && rm -rf ${TEMPDIR}
mkdir ${TEMPDIR} || { echo "Failed to create ${TEMPDIR}"; exit 1; }

MAKE=$(which make 2>/dev/null)
if [ "${MAKE}" ]
then
  make DEST=${TEMPDIR} || { echo "Build failed"; exit 1; }
else
  echo "Skipping make - not found"
fi
cd ${TEMPDIR}
if [ "${RSYNC2_SPEC}" ]
then
  echo "Syncing jar, sh and ephe data to ${RSYNC2_SPEC}"
  rsync -a --verbose --checksum *.jar *.sh achart-parashara.inc.php ephe ${RSYNC2_SPEC} || { echo "Second rsync failed "; exit 1; }
fi
rm -rf ephe *.jar *~ amath_* images images2
rsync -a --verbose --checksum * ${RSYNC_SPEC} || { echo "rsync failed - files are in ${TEMPDIR}"; exit 1; }
cd ..
echo "Successfully deployed"
rm -rf ${TEMPDIR}

