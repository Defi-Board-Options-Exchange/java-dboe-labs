#! /bin/sh

cd ~/trading/current/
PIDF=.ib.pid
if [ -e $PIDF ]; then
	PID=`cat $PIDF`
	kill -9 $PID
	rm $PIDF

	echo "Stop IB PID: $PID"
else 
	echo 'No IB PID Found'
fi
