#!/bin/sh
/usr/bin/mongoimport -d ordering -c catalog < /tmp/mongodb.catalog.json
/usr/bin/mongoimport -d ordering -c dealers < /tmp/mongodb.dealers.json
/usr/bin/mongoimport -d ordering -c quotes < /tmp/mongodb.quotes.json
