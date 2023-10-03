#! /bin/sh

cd ~/prod/
date=`date +20%y%m%d`
LOG=logs/load_dailyprice_$date.txt

java -classpath ops:libs/*:common -Dlog4j.configuration=file:common/resources/log4j.xml com.ngontro86.crawler.vnstocks.LoadDailyPriceApp localhost ngontro86 root root >> $LOG &
	

