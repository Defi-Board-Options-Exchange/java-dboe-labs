#! /bin/sh

cd ~/prod/

echo 'Cancel all orders'
./gocancelorders.sh
sleep 10

echo 'Stop Verifier'
./stopverifier.sh
sleep 5

echo 'Stop Execution'
./stopexecution.sh
sleep 5

echo 'Stop Algo'
./stopalgoguiless.sh
sleep 5

echo 'Stop IB'
./stopib.sh
sleep 5

echo 'Stop Server'
./stopserv.sh

echo 'Done'

