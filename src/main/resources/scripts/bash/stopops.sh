#! /bin/sh

cd ~/prod/
PIDF=.ops.pid

if [ -e $PIDF ]; then

	PID=`cat $PIDF`
	kill -9 $PID
	rm $PIDF

	echo "Stop OPS PID: $PID"
else 
	echo 'No OPS PID found'
fi
