# Dockerizing MongoDB: Dockerfile for building MongoDB images
# Based on ubuntu:latest, installs MongoDB following the instructions from:
# http://docs.mongodb.org/manual/tutorial/install-mongodb-on-ubuntu/

FROM       ubuntu:latest
MAINTAINER Shawn Cicoria shawn.cicoria@microsoft.com

ADD mongorun.sh .
# Installation:
# Import MongoDB public GPG key AND create a MongoDB list file
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 7F0CEB10
RUN echo 'deb http://downloads-distro.mongodb.org/repo/ubuntu-upstart dist 10gen' | tee /etc/apt/sources.list.d/10gen.list

# Update apt-get sources AND install MongoDB
RUN apt-get update && apt-get install -y mongodb-org

# Create the MongoDB data directory
RUN mkdir -p -m 777 /data/db

RUN mkdir -p -m 777 /tmp

COPY mongodb.catalog.json /tmp/mongodb.catalog.json
COPY mongodb.dealers.json /tmp/mongodb.dealers.json
COPY mongodb.quotes.json /tmp/mongodb.quotes.json
COPY mongorun.sh /tmp/mongorun.sh

WORKDIR /tmp

RUN chmod +x ./mongorun.sh

# Expose port #27017 from the container to the host
EXPOSE 27017

VOLUME /data/db

ENTRYPOINT ["./mongorun.sh"]

