FROM java:8-jre
MAINTAINER Shawn Cicoria shawn.cicoria@microsoft.com

RUN mkdir -p /usr/local/app

WORKDIR /usr/local/app

ADD startService.sh /usr/local/app/
ADD ordering-service-0.1.0.jar /usr/local/app/

EXPOSE 8080
#CMD ["java", "-jar", "ordering-service-0.1.0.jar"]
CMD ["./startService.sh"]
