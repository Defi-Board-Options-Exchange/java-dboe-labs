#! /bin/sh

cd ~/trading/scripts

echo 'Start SERVER'
./goserv.sh
sleep 20

echo 'Start Execution'
./goexecution.sh
#sleep 10

echo 'Start IB'
./goib.sh
sleep 10

echo 'Start Algo'
./goalgoguiless.sh
sleep 10

echo 'Start Verifier'
./goverifier.sh
sleep 10

echo 'DONE'


