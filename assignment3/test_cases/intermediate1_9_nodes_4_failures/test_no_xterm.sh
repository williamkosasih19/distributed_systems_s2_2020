#!/bin/bash

echo Intermediate test with 9 nodes and 4 failures

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

echo Let the processes run for 70 seconds, then kill 4 of them
sleep 70
kill $pid_0
kill $pid_3
kill $pid_6
kill $pid_8
echo _________________
echo 4 nodes killed
echo ********diff*******

echo Diff each logs - result are expected to be the same for all of these 4 processes
DIFF1=$(diff M0.log M3.log)
DIFF2=$(diff M0.log M6.log)
DIFF3=$(diff M0.log M8.log)

echo _________________

echo Let the other processes run for another 60 seconds then kill all
echo **Note: Sometimes a proposer needs quite a bit to catch up to the proposal number
echo to be as large as the ones that were proposed by a killed node, so it might take some time to see any updates
echo if we compile with the verbose flag on, then we can see that there are still things going on in the background
echo just that all the proposal are rejected due to lowwer number than prviously seen
sleep 60
kill $pid_1
kill $pid_2
kill $pid_4
kill $pid_5
kill $pid_7
echo Diff each logs - result are expected to be the same for all of these 5 processes
DIFF4=$(diff M1.log M2.log)
DIFF5=$(diff M1.log M4.log)
DIFF6=$(diff M1.log M5.log)
DIFF7=$(diff M1.log M7.log)

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
