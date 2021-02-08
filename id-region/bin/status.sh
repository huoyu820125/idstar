#!/bin/bash
pid=$(ps -aux|grep './id-region-0.0.1-SNAPSHOT.jar --server.port=8225'| grep -v grep | awk '{print $2}')
if [ $pid ]
then
     echo node1 $pid
fi

pid=$(ps -aux|grep './id-region-0.0.1-SNAPSHOT.jar --server.port=8226'| grep -v grep | awk '{print $2}')
if [ $pid ]
then
     echo node2 $pid
fi

pid=$(ps -aux|grep './id-region-0.0.1-SNAPSHOT.jar --server.port=82257'| grep -v grep | awk '{print $2}')
if [ $pid ]
then
     echo node3 $pid
fi

pid=$(ps -aux|grep './id-region-0.0.1-SNAPSHOT.jar --server.port=1801 --id-region.instance.hostname=id-region'| grep -v grep | awk '{print $2}')
if [ $pid ]
then
     echo id-region $pid
fi
