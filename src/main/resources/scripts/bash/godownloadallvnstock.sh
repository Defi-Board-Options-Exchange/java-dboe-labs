#! /bin/sh

cd ~/prod/
date=`date +20%y%m%d`
LOG=logs/loadprice_$date.txt

java -classpath ops:libs/*:common -Dlog4j.configuration=file:common/resources/log4j.xml com.ngontro86.crawler.vnstocks.LoadPriceApp localhost ngontro86 root root >> $LOG &
	

