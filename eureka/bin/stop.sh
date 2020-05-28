#!/bin/bash
if [ $1 = 'cluster' ]
then
   echo stop cluster
   pid=$(ps -aux|grep './eureka-0.0.1-SNAPSHOT.jar --spring.profiles.active=peer1'| grep -v grep | awk '{print $2}')
   if [ $pid ] 
   then
   	echo stop peer1 $pid
	kill $pid
   fi
   pid=$(ps -aux|grep './eureka-0.0.1-SNAPSHOT.jar --spring.profiles.active=peer2'| grep -v grep | awk '{print $2}')
   if [ $pid ]
   then
   	echo stop peer2 $pid
	kill $pid
   fi
   pid=$(ps -aux|grep './eureka-0.0.1-SNAPSHOT.jar --spring.profiles.active=peer3'| grep -v grep | awk '{print $2}')
   if [ $pid ]
   then
   	echo stop peer3 $pid
        kill $pid
   fi
else
   pid=$(ps -aux|grep './eureka-0.0.1-SNAPSHOT.jar --server.port=1801 --eureka.instance.hostname=eureka'| grep -v grep | awk '{print $2}')
   if [ $pid ]
   then
   	echo stop eureka $pid
	kill $pid
   fi
fi

