#!/bin/bash

pid=`ps aux | grep ordering-service | awk '{print $2}'`
kill -9 $pid