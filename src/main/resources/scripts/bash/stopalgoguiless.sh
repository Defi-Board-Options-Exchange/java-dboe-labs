#! /bin/sh

cd ~/trading/current/
PIDF=.algoguiless.pid

if [ -e $PIDF ]; then

	PID=`cat $PIDF`
	kill -9 $PID
	rm $PIDF

	echo "Stop AlgoGUILess PID: $PID"
else 
	echo 'No AlgoGUILess PID found'
fi
