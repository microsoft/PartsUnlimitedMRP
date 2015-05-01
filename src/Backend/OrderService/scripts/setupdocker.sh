#!/bin/sh
curl -i http://192.168.59.103:8080/ordering-0.1.0/dealers -X POST -H "Content-Type: application/json" -d @dealer1.json
echo ""
echo ""
curl -i http://192.168.59.103:8080/ordering-0.1.0/catalog -X POST -H "Content-Type: application/json" -d @cat001.json
echo ""
echo ""
curl -i http://192.168.59.103:8080/ordering-0.1.0/catalog -X POST -H "Content-Type: application/json" -d @cat002.json
echo ""
echo ""
curl -i http://192.168.59.103:8080/ordering-0.1.0/catalog -X POST -H "Content-Type: application/json" -d @cat003.json
echo ""
echo ""
curl -i http://192.168.59.103:8080/ordering-0.1.0/catalog -X POST -H "Content-Type: application/json" -d @cat004.json
echo ""
echo ""

curl -i http://192.168.59.103:8080/ordering-0.1.0/dealers
echo ""
echo ""
curl -i http://192.168.59.103:8080/ordering-0.1.0/catalog
echo ""
echo ""

curl -i http://192.168.59.103:8080/ordering-0.1.0/quotes -X POST -H "Content-Type: application/json" -d @quote1.json
echo ""
echo ""

