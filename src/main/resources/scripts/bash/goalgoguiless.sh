#! /bin/sh

cd ~/trading/current
date=`date +20%y%m%d`
LOG=~/logs/algo_guiless_$date.txt
PID=.algoguiless.pid

if [ -e $PID ]; then
	echo 'AlgoGUILess already running'
else

	~/jdk/bin/java -Dapplication=com.ngontro86.algo.AlgoGUILessApp -DappConfigs=algo-config.cfg,db-config.cfg  > $LOG 2>&1
	echo $! > $PID
	chmod 666 $PID
fi
