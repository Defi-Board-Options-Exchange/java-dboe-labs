#! /bin/sh

cd ~/trading/current/
date=`date +20%y%m%d`
LOG=~/logs/ibcontroller_$date
PID=.ibcontroller.pid

/opt/IBController/IBControllerGatewayStart.sh >> $LOG &

echo $! > $PID
chmod 666 $PID
