#! /bin/sh

cd ~/trading/current/
date=`date +20%y%m%d`
LOG=~/logs/execution_$date.txt
PID=.exec.pid

if [ -e $PID ]; then
	echo 'Execution already running'
else
	~/jdk/bin/java -Dapplication=com.ngontro86.execution.main.ExecutionApp -DappConfigs=execution-config.cfg -jar execution-fat.jar  > $LOG 2>&1
	echo $! > $PID
	chmod 666 $PID
fi
