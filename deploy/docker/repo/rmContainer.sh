#!/bin/sh
docker ps -a | awk '{print $1}' | xargs --no-run-if-empty docker rm
