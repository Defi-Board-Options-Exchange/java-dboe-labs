#!/bin/sh

cd ~/prod/
PIDF=.karaoke.pid

if [ -e $PIDF ]; then

	PID=`cat $PIDF`
	kill -9 $PID
	rm $PIDF

	echo "Stop Karaoke PID: $PID"
else 
	echo 'No Karaoke PID found'
fi
