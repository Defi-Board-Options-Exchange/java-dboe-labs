#! /bin/sh

cd ~/prod/
date=`date +20%y%m%d`
LOG=logs/ops_$date.txt

java -classpath ops:libs/*:common -Dlog4j.configuration=file:common/resources/log4j.xml com.ngontro86.ops.main.PL localhost ngontro86 root root >> $LOG &
	

