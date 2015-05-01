#!/bin/sh
curl -i http://localhost:8080/dealers -X POST -H "Content-Type: application/json" -d @dealer1.json
echo ""
echo ""
curl -i http://localhost:8080/catalog -X POST -H "Content-Type: application/json" -d @cat001.json
echo ""
echo ""
curl -i http://localhost:8080/catalog -X POST -H "Content-Type: application/json" -d @cat002.json
echo ""
echo ""
curl -i http://localhost:8080/catalog -X POST -H "Content-Type: application/json" -d @cat003.json
echo ""
echo ""
curl -i http://localhost:8080/catalog -X POST -H "Content-Type: application/json" -d @cat004.json
echo ""
echo ""

curl -i http://localhost:8080/dealers
echo ""
echo ""
curl -i http://localhost:8080/catalog
echo ""
echo ""

curl -i http://localhost:8080/quotes -X POST -H "Content-Type: application/json" -d @quote1.json
echo ""
echo ""

