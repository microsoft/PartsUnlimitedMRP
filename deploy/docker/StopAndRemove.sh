#!/bin/bash

#Stop your docker container and remove it manually on the server
docker rm db --force
docker rm order --force
docker rm web --force

#Remove the images manually on the server
docker rmi mypartsunlimitedmrp/db
docker rmi mypartsunlimitedmrp/order
docker rmi mypartsunlimitedmrp/web