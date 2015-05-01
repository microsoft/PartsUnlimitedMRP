#!/bin/sh
cd mongoseed
docker build -t scicoria/mongoseed:0.1 .
cd ../orderService
docker build -t scicoria/orderservice:0.1 .
cd ../
