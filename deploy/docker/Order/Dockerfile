FROM openjdk:8-jre

MAINTAINER juliens@microsoft.com

RUN mkdir -p /usr/local/app

WORKDIR /usr/local/app

COPY drop/* /usr/local/app/

EXPOSE 8080

ENTRYPOINT sh run.sh
