#!/bin/sh
curl -i http://localhost:8080//orders?fromQuote=quote-1 -X POST
echo ""
echo ""

curl -i http://localhost:8080/orders?dealer=BigJoe
echo ""
echo ""




