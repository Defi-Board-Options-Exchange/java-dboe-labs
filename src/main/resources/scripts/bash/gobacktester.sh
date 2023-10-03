#! /bin/sh
cd ~/prod/
date=`date +20%y%m%d`
LOG=logs/backtester_$date.txt
PID=.backtester.pid

if [ -e $PID ]; then
	echo 'Backtester already running'
else
	java -classpath research:libs/*:common:algo:server:server/lib/* -Xmx2048m -XX:-UseGCOverheadLimit -Dlog4j.configuration=file:common/resources/log4j.xml com.ngontro86.research.backtest.Backtester localhost ngontro86 root root $1 $2 $3 >> $LOG &
	echo $! > $PID
	chmod 666 $PID

fi



