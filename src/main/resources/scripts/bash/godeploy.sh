#! /bin/sh

HOME=$1

SRC=$HOME/trading-lab
DEST=$HOME/releases/prod/

cd $DEST

echo 'Deleting old files'
rm -rf common
rm -rf server
rm -rf dbpub
rm -rf execution
rm -rf verifier
rm -rf ib
rm -rf research
rm -rf algo
rm -rf ops
rm -rf crawler

echo 'Copying over files'

echo 'copying common...'
scp -r $SRC/common/bin ./common/
scp -r $SRC/common/resources ./common/
scp -r $SRC/common/libs .

echo 'copying server...'
scp -r $SRC/server/bin ./server/
scp -r $SRC/server/resources ./server/
scp -r $SRC/server/lib ./server/

echo 'copying dbpub...'
scp -r $SRC/dbpub/bin ./dbpub/

echo 'copying execution...'
scp -r $SRC/execution/bin ./execution/

echo 'copying verifier...'
scp -r $SRC/verifier/bin ./verifier/

echo 'copying ib...'
scp -r $SRC/ib/bin ./ib/

echo 'copying research...'
scp -r $SRC/research/bin ./research/

echo 'copying crawler...'
scp -r $SRC/crawler/bin ./crawler/

echo 'copying algo...'
scp -r $SRC/algo/bin ./algo/
scp -r $SRC/algo/resources ./algo/

echo 'copying ops...'
scp -r $SRC/ops/bin ./ops/

echo 'copying nn...'
scp -r $SRC/nn/bin ./nn/

echo 'Done'




