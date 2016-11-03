#!/bin/bash

#Build your docker images manually on the server
docker build -t mypartsunlimitedmrp/db ./Database
docker build -t mypartsunlimitedmrp/order ./Order
docker build -t mypartsunlimitedmrp/web ./Clients

#Run your docker images manually on the server
docker run -it -d --name db -p 27017:27017 -p 28017:28017 mypartsunlimitedmrp/db 
docker run -it -d --name order -p 8080:8080 --link db:mongo mypartsunlimitedmrp/order
docker run -it -d --name web -p 80:8080 mypartsunlimitedmrp/web

#Feed the database
docker exec db mongo ordering /tmp/MongoRecords.js