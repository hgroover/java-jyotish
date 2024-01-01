#!/bin/sh
# Use awk scripts to process template source into java code

SOURCE=$1
[ "${SOURCE}" ] || { echo "No source specified"; exit 1; }

# Motion delta values are set in template-parse2.awk
gawk -f template-parse1.awk ${SOURCE} | sort -n | gawk -F'\t' -f template-parse2.awk

