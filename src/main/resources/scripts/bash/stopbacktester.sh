#! /bin/sh
cd ~/prod/
PIDF=.backtester.pid
if [ -e $PIDF ]; then
	PID=`cat $PIDF`
	kill -9 $PID
	rm $PIDF

	echo "Stop Backtester PID: $PID"
else 
	echo 'No Backtester PID Found'
fi
