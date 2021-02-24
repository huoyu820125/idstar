#!/bin/bash
   pid=$(ps -aux|grep './id-star-service-0.0.1-SNAPSHOT.jar --server.port=8227'| grep -v grep | awk '{print $2}')
   if [ $pid ]
   then
   	echo stop id-region $pid
	kill $pid
   fi
