#! /bin/sh

cd ~/prod/
date=`date +20%y%m%d`
LOG=logs/signal_monitor_$date
PID=.signalmonitor.pid

if [ -e $PID ]; then
	echo 'SignalMonitor already running'
else
	java -classpath ops:libs/*:common -Dlog4j.configuration=file:common/resources/log4j.xml com.ngontro86.ops.main.SignalMonitor >> $LOG &
	echo $! > $PID
	chmod 666 $PID
fi
	

