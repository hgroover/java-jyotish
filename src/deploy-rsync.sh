#!/bin/sh
# Deploy to an existing directory on a web server using rsync
# Syntax: deploy-rsync.sh <rsync-dest>
# Example: ./deploy-rsync.sh joni@jonimitchell.com:public_html/astro

RSYNC_SPEC=$1
[ "${RSYNC_SPEC}" ] || { echo "Syntax: $0 rsync-destination"; exit 1; }

# Change to directory
PROJECT_DIR=$(dirname $0)
cd ${PROJECT_DIR}

# package to temp dir
TEMPDIR=tmp.deploy.$$
[ -d ${TEMPDIR} ] && rm -rf ${TEMPDIR}
mkdir ${TEMPDIR} || { echo "Failed to create ${TEMPDIR}"; exit 1; }

make DEST=${TEMPDIR} || { echo "Build failed"; exit 1; }
cd ${TEMPDIR}
rsync -a --verbose * ${RSYNC_SPEC} || { echo "rsync failed - files are in ${TEMPDIR}"; exit 1; }

cd ..
echo "Successfully deployed"
rm -rf ${TEMPDIR}

