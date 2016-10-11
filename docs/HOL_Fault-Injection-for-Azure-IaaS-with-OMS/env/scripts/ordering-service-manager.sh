#!/bin/bash

case $1 in
    start)
        /bin/bash /var/lib/partsunlimited/ordering-service-start.sh
    ;;
    stop)
        /bin/bash /var/lib/partsunlimited/ordering-service-stop.sh
    ;;
    restart)
        /bin/bash /var/lib/partsunlimited/ordering-service-stop.sh
        /bin/bash /var/lib/partsunlimited/ordering-service-start.sh
    ;;
esac
exit 0