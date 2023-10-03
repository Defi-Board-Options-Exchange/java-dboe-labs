#! /bin/sh

cd ~/trading/current
date=`date +20%y%m%d`
LOG=~/logs/adminServer_$date.txt
PID=.adminServ.pid

if [ -e $PID ]; then
	echo 'Server already running'
else
	~/jdk/bin/java -Dapplication=com.ngontro86.server.ServerApp -DappConfigs=db-config.cfg,server-config.cfg,snapshot-config.cfg -jar server-fat.jar  > $LOG 2>&1
	echo $! > $PID
	chmod 666 $PID
fi

