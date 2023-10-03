#! /bin/sh
cd ~/prod/

java -classpath crawler:libs/*:common -Xmx2048m -XX:-UseGCOverheadLimit -Dlog4j.configuration=file:common/resources/log4j.xml com.ngontro86.crawler.main.MyWebCrawler $1 &	


