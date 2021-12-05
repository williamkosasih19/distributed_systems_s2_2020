#!/bin/bash

echo Simple test with 9 paxos nodes and no failures
CURRENTDIR=$(pwd)
cd "$(dirname $(pwd)/$0)/../.."

./compile.sh

rm M*.log

java -cp "./out" Voting M1 &
pid_0=$!
sleep 1
java -cp "./out" Voting M2 &
pid_1=$!
sleep 1
java -cp "./out" Voting M3 &
pid_2=$!
sleep 1
java -cp "./out" Voting M4 &
pid_3=$!
sleep 1
java -cp "./out" Voting M1 &
pid_4=$!
sleep 1
java -cp "./out" Voting M1 &
pid_5=$!
sleep 1
java -cp "./out" Voting M2 &
pid_6=$!
sleep 1
java -cp "./out" Voting M3 &
pid_7=$!
sleep 1
java -cp "./out" Voting M1 start &
pid_8=$!

echo Let the processes run for 60 seconds, then kill all of them
sleep 60
kill $pid_0
kill $pid_1
kill $pid_2
kill $pid_3
kill $pid_4
kill $pid_5
kill $pid_6
kill $pid_7
kill $pid_8
killall java

echo Diff each logs - result are expected to be the same for all
DIFF1=$(diff M0.log M1.log)
DIFF2=$(diff M0.log M2.log)
DIFF3=$(diff M0.log M3.log)
DIFF4=$(diff M0.log M4.log)
DIFF5=$(diff M0.log M5.log)
DIFF6=$(diff M0.log M6.log)
DIFF7=$(diff M0.log M7.log)
DIFF8=$(diff M0.log M8.log)
DIFF_FINAL="$DIFF1$DIFF2$DIFF3$DIFF4$DIFF5$DIFF6$DIFF7$DIFF8"
echo "DIFF FINAL = " $DIFF_FINAL
echo ...
echo ...

if [ "$DIFF_FINAL" == "" ]
then
  echo "TEST SUCCESFULL!!! - NO DIFFERENCE BETWEEN CONSENSUS IN EACH NODES"
else
  echo "TEST FAILED - DIFF = " $DIFF_FINAL
fi

cd $CURRENTDIR