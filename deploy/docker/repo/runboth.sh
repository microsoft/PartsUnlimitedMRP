#!/bin/bash

docker run -d -p 27017:27017 --name mongodb -v /data/db:/data/db scicoria/mongoseed:0.1
docker run -d -p 8080:8080 --link mongodb:mongo scicoria/orderservice:0.1
