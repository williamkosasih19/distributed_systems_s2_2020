#!/bin/bash

if [ $1 == "DEBUG" ]
then
  echo "true" > ../../debug.conf
else
  echo "false" > ../../debug.conf
fi

echo Simple test with 9 paxos nodes and 4 failures
cd ../..

./compile.sh

rm M*.log

xterm ./run_M1.sh &
pid_0=$!
sleep 1
xterm ./run_M2.sh &
pid_1=$!
sleep 1
xterm ./run_M3.sh &
pid_2=$!
sleep 1
xterm ./run_M4.sh &
pid_3=$!
sleep 1
xterm ./run_M1.sh &
pid_4=$!
sleep 1
xterm ./run_M1.sh &
pid_5=$!
sleep 1
xterm ./run_M2.sh &
pid_6=$!
sleep 1
xterm ./run_M3.sh &
pid_7=$!
sleep 1
xterm ./run_M1_start.sh &
pid_8=$!

echo Let the processes run for 20 seconds, then kill 4 of them
sleep 20
kill $pid_0
kill $pid_3
kill $pid_5
kill $pid_8
echo _________________
echi 4 nodes killed
echo ********diff*******

echo Diff each logs - result are expected to be the same for all of these 4 processes
diff M0.log M3.log
diff M0.log M5.log
diff M0.log M8.log

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
kill $pid_6
kill $pid_7
echo Diff each logs - result are expected to be the same for all of these 5 processes
diff M1.log M2.log
diff M1.log M4.log
diff M1.log M6.log
diff M1.log M7.log

echo ...
echo ...
echo test success
cd test_cases/intermediate1_9_nodes_4_failures
