#!/bin/bash

echo Intermediate test with 15 paxos nodes and 7 failures

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
java -cp "./out" Voting M1 &
pid_7=$!
sleep 1
java -cp "./out" Voting M1 &
pid_8=$!
sleep 1
java -cp "./out" Voting M1 &
pid_9=$!
sleep 1
java -cp "./out" Voting M1 &
pid_10=$!
sleep 1
java -cp "./out" Voting M1 &
pid_11=$!
sleep 1
java -cp "./out" Voting M1 &
pid_12=$!
sleep 1
java -cp "./out" Voting M1 &
pid_13=$!
sleep 1
java -cp "./out" Voting M1 start &
pid_14=$!


echo Let the processes run for 70 seconds, then kill 4 of them
sleep 70
kill $pid_0
kill $pid_3
kill $pid_6
kill $pid_8
kill $pid_2
kill $pid_12
kill $pid_4
echo _________________
echo 4 nodes killed
echo ********diff*******

echo Diff each logs - result are expected to be the same for all of these 4 processes
DIFF1=$(diff M0.log M3.log)
DIFF2=$(diff M0.log M6.log)
DIFF3=$(diff M0.log M8.log)
DIFF4=$(diff M0.log M2.log)
DIFF5=$(diff M0.log M12.log)
DIFF6=$(diff M0.log M4.log)

echo _________________

echo Let the other processes run for another 60 seconds then kill all
echo **Note: Sometimes a proposer needs quite a bit to catch up to the proposal number
echo to be as large as the ones that were proposed by a killed node, so it might take some time to see any updates
echo if we compile with the verbose flag on, then we can see that there are still things going on in the background
echo just that all the proposal are rejected due to lowwer number than prviously seen
sleep 60
kill $pid_1
kill $pid_5
kill $pid_7
kill $pid_9
kill $pid_10
kill $pid_11
kill $pid_13
kill $pid_14
echo Diff each logs - result are expected to be the same for all of these 5 processes
DIFF7=$(diff M1.log M5.log)
DIFF8=$(diff M1.log M7.log)
DIFF9=$(diff M1.log M9.log)
DIFF10=$(diff M1.log M10.log)
DIFF11=$(diff M1.log M11.log)
DIFF12=$(diff M1.log M13.log)
DIFF13=$(diff M1.log M14.log)

DIFF_FINAL="$DIFF1$DIFF2$DIFF3$DIFF4$DIFF5$DIFF6$DIFF7$DIFF8$DIFF9$DIFF10$DIFF1$DIFF12$DIFF13$DIFF14"
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
