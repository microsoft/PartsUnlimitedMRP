#!/bin/bash

while ! curl http://mongo:27017/
do
  echo "$(date) - still trying"
  sleep 1
done
echo "$(date) - connected successfully"

java -jar usr/local/app/ordering-service-0.1.0.jar -spring.config.location=application.properties
