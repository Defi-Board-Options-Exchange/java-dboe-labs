#! /bin/sh

cd ~/prod/tws/
date=`date +20%y%m%d`
LOG=~/prod/logs/tws_gui_$date
PID=~/prod/.tws_gui.pid

java -cp jts.jar:hsqldb.jar:jcommon-1.0.12.jar:jfreechart-1.0.9.jar:jhall.jar:other.jar:rss.jar -Xmx512M -XX:MaxPermSize=128M jclient.LoginFrame . >> $LOG &

echo $! > $PID
chmod 666 $PID
