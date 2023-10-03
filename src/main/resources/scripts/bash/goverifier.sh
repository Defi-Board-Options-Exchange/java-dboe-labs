#! /bin/sh

cd ~/trading/current/
date=`date +20%y%m%d`
LOG=~/logs/verifier_$date.txt
PID=.verifier.pid

if [ -e $PID ]; then
	echo 'Verifier already running'
else
	~/jdk/bin/java -Dapplication=com.ngontro86.verifier.main.VerifierApp -DappConfigs=verifier-config.cfg -jar ib-fat.jar  > $LOG 2>&1
	echo $! > $PID
	chmod 666 $PID
fi
