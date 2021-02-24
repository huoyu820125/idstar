#!/bin/bash
    pid=$(ps -aux|grep './id-star-service-0.0.1-SNAPSHOT.jar --server.port=8225'| grep -v grep | awk '{print $2}')
if [ $pid ]
then
     echo node1 $pid
fi

pid=$(ps -aux|grep './id-star-service-0.0.1-SNAPSHOT.jar --server.port=8226'| grep -v grep | awk '{print $2}')
if [ $pid ]
then
     echo node2 $pid
fi

pid=$(ps -aux|grep './id-star-service-0.0.1-SNAPSHOT.jar --server.port=82257'| grep -v grep | awk '{print $2}')
if [ $pid ]
then
     echo node3 $pid
fi

pid=$(ps -aux|grep './id-star-service-0.0.1-SNAPSHOT.jar --server.port=1801 --id-star-service.instance.hostname=id-star-service'| grep -v grep | awk '{print $2}')
if [ $pid ]
then
     echo id-star-service $pid
fi
