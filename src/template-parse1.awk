BEGIN {inpath=0;id="";}
/<path/ {inpath=1;d="";}
/<polygon/ {inpath=0;}
/<line/ {inpath=0;}
/<text/ {inpath=0;}
/<g/ {inpath=0;}
/^[ \t]*id="h([0-9]+)-([0-9]+):.*:.*:.*:.*"/ {if (inpath && match($1,/id="h([0-9]+)-([0-9]+):(.):(.):([^:]+):([^"]+)"/,a)) { printf "%02d\t%02d\t%s\t%s\t%d\t%d\t%s\n", a[1], a[2], a[3], a[4], a[5], a[6], d; d=""; } }
/^[ \t]*d=".+"/ {d=""; if (inpath && match($0,/d="([^"]+)"/,a)) d=a[1];}
