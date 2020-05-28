#!/bin/bash   
pid=$(ps -aux|grep './eureka-0.0.1-SNAPSHOT.jar --spring.profiles.active=peer1'| grep -v grep | awk '{print $2}')
if [ $pid ]
then
     echo peer1 $pid
fi

pid=$(ps -aux|grep './eureka-0.0.1-SNAPSHOT.jar --spring.profiles.active=peer2'| grep -v grep | awk '{print $2}')
if [ $pid ]
then
     echo peer2 $pid
fi

pid=$(ps -aux|grep './eureka-0.0.1-SNAPSHOT.jar --spring.profiles.active=peer3'| grep -v grep | awk '{print $2}')
if [ $pid ]
then
     echo peer3 $pid
fi

pid=$(ps -aux|grep './eureka-0.0.1-SNAPSHOT.jar --server.port=1801 --eureka.instance.hostname=eureka'| grep -v grep | awk '{print $2}')
if [ $pid ]
then
     echo eureka $pid
fi
