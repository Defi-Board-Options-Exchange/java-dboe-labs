#! /bin/sh

cd ~/prod/IBJts/
date=`date +20%y%m%d`
LOG=~/prod/logs/tws_$date.txt
PID=~/prod/.tws.pid

java -cp jts.jar:total.2013.jar -Dsun.java2d.noddraw=true -Xmx512M ibgateway.GWClient . >> $LOG &

echo $! > $PID
chmod 666 $PID
