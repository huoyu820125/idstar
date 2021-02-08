#!/bin/bash
   pid=$(ps -aux|grep './id-region-0.0.1-SNAPSHOT.jar --server.port=8226'| grep -v grep | awk '{print $2}')
   if [ $pid ]
   then
   	echo stop id-region $pid
	kill $pid
   fi
