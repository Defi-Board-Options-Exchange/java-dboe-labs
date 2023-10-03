#! /bin/sh

cd ~/prod/
date=`date +20%y%m%d`
LOG=logs/sub_$date.txt
PID=.sub.pid

if [ -e $PID ]; then
	echo 'SubDB already running'
else
	java -classpath dbpub:libs/*:common -Dlog4j.configuration=file:common/resources/log4j.xml com.ngontro86.dbpub.main.DBPubApp localhost ngontro86 root root PROD >> $LOG &
	echo $! > $PID
	chmod 666 $PID
fi
