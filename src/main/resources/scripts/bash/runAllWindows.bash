#!/bin/bash

cd C:\Users\TruongVinh\Dropbox\QuantVu86\prod

java -Dapplication=com.ngontro86.server.ServerApp -DappConfigs=db-config.cfg,server-config.cfg,snapshot-config.cfg -jar server-fat.jar  > logs\server_log.txt 2>&1 
 sleep 30s
java -Dapplication=com.ngontro86.IB.main.IbApp -DappConfigs=db-config.cfg,ib-config.cfg -jar ib-fat.jar  > logs\ib_log.txt 2>&1 
sleep 5s
java -Dapplication=com.ngontro86.execution.main.ExecutionApp -DappConfigs=execution-config.cfg -jar execution-fat.jar > logs\execution_log.txt 2>&1
sleep 5s
java -Dapplication=com.ngontro86.verifier.main.VerifierApp -DappConfigs=verifier-config.cfg -jar verifier-fat.jar > logs\verifier_log.txt 2>&1
sleep 15s
java -Dapplication=com.ngontro86.algo.main.AlgoApp -DappConfigs=db-config.cfg,algo-config.cfg -jar algo-fat.jar > logs\algo_log.txt 2>&1
