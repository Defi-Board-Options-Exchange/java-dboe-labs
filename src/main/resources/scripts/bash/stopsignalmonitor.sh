#! /bin/sh

cd ~/prod/
PIDF=.signalmonitor.pid

if [ -e $PIDF ]; then

	PID=`cat $PIDF`
	kill -9 $PID
	rm $PIDF

	echo "Stop SignalMonitor PID: $PID"
else 
	echo 'No SignalMonitor PID found'
fi
