#! /bin/sh
cd ~/trading/current/
PIDF=.serv.pid
if [ -e $PIDF ]; then
	PID=`cat $PIDF`
	kill -9 $PID
	rm $PIDF

	echo "Stop SERVER PID: $PID"
else 
	echo 'No SERVER PID Found'
fi



