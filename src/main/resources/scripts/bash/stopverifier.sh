#! /bin/sh

cd ~/trading/current/

PIDF=.verifier.pid

if [ -e $PIDF ]; then
	PID=`cat $PIDF`
	kill -9 $PID
	rm $PIDF
	echo "Stop Verifier PID: $PID"
else
	echo 'No Verifier Found'
fi
