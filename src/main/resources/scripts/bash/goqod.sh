#! /bin/sh

cd ~/prod/
date=`date +20%y%m%d`
LOG=logs/qod_$date

java -classpath research:libs/*:common -Dlog4j.configuration=file:common/resources/log4j.xml com.ngontro86.research.backtest.DailyFortuneQuote >> $LOG &
	
