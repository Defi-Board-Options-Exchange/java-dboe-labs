#! /bin/sh

cd ~/prod/
date=`date +20%y%m%d`
LOG=logs/algo_$date.txt

java -classpath algo:algo/resources/*:libs/*:common -Dlog4j.configuration=file:common/resources/log4j.xml com.ngontro86.algo.main.AlgoApp localhost ngontro86 root root >> $LOG &
	
