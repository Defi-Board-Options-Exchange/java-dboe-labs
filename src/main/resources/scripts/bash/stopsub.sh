#! /bin/sh

cd ~/prod/
PIDF=.sub.pid

if [ -e $PIDF ]; then

	PID=`cat $PIDF`
	kill -9 $PID
	rm $PIDF

	echo "Stop SUBDB PID: $PID"
else 
	echo 'No SUBDB PID found'
fi
