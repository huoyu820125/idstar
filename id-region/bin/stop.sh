#!/bin/bash
   pid=$(ps -aux|grep './id-region-0.0.1-SNAPSHOT.jar'| grep -v grep | awk '{print $2}')
   if [ $pid ]
   then
   	echo stop id-region $pid
	kill $pid
   fi
