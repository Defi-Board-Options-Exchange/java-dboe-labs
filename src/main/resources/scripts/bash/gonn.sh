#! /bin/sh

cd ~/prod/
date=`date +20%y%m%d`
LOG=logs/nn_$date
PID=.nn.pid

if [ -e $PID ]; then
	echo 'NN already running'
else
	java -classpath nn:libs/*:common -Dlog4j.configuration=file:common/resources/log4j.xml com.ngontro86.nn.main.NN localhost ngontro86 root root >> $LOG &
	echo $! > $PID
	chmod 666 $PID
fi
