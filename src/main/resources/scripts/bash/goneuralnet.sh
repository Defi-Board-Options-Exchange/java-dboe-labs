#! /bin/sh

cd ~/prod/
date=`date +20%y%m%d`
LOG=logs/neuralnet_$date

java -classpath research:libs/*:common:algo:server:server/lib/* -Xmx2048m -XX:-UseGCOverheadLimit -Dlog4j.configuration=file:common/resources/log4j.xml com.ngontro86.research.backtest.NeuralNetResearcher localhost ngontro86 root root $1 $2 $3 >> $LOG &


