#! /bin/sh

cd ~/trading/current/
PIDF=.exec.pid
if [ -e $PIDF ]; then
	PID=`cat $PIDF`
	kill -9 $PID
	rm $PIDF
	echo "Stop Execution PID: $PID"
else
	echo 'No Execution PID Found'
fi
