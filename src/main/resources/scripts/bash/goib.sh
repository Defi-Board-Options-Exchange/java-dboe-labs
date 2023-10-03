#! /bin/sh

cd ~/trading/current/
date=`date +20%y%m%d`
LOG=~/logs/ib_$date.txt
PID=.ib.pid

if [ -e $PID ]; then
	echo 'IB already running'
else
	~/jdk/bin/java -Dapplication=com.ngontro86.IB.main.IbApp -DappConfigs=db-config.cfg,ib-config.cfg -jar ib-fat.jar  > $LOG 2>&1
	echo $! > $PID
	chmod 666 $PID
fi
