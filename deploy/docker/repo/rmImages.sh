#!/bin/sh
docker images | awk '{print $3}' | xargs --no-run-if-empty docker rmi
