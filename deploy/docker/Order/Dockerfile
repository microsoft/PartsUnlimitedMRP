FROM openjdk:8-jre

MAINTAINER pablo.bermejo@uclm.es

RUN mkdir -p /usr/local/app

# WORKDIR /usr/local/app

# copiar el .jar del servicio a la imagen
# No se indica el nombre porque las versiones pueden cambiar
COPY drop/*.jar usr/local/app


COPY drop/run.sh usr/local/app/run.sh

COPY drop/application.properties application.properties


EXPOSE 8082

ENTRYPOINT P=8082 ;sed "s/8080/$P/g" < conf/server.xml > /tmp/srv.xml && catalina.sh run -config /tmp/srv.xml

