#!/bin/sh
curl -i http://localhost:8080/quotes/quote-1 -X PUT -H "Content-Type: application/json" -d @quote2.json
echo ""
echo ""



