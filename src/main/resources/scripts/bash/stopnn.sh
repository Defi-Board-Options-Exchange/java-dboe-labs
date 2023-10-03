#! /bin/sh

cd ~/prod/
PIDF=.nn.pid

if [ -e $PIDF ]; then

	PID=`cat $PIDF`
	kill -9 $PID
	rm $PIDF

	echo "Stop NN PID: $PID"
else 
	echo 'No NN PID found'
fi
