#! /bin/sh

cd ~/trading/current/

date=`date +20%y%m%d`
LOG=~/logs/cancelorders_$date.txt
~/jdk/bin/java -Dapplication=com.ngontro86.algo.main.CancelAllOrders -DappConfigs=algo-config.cfg,db-config.cfg  > $LOG 2>&1
