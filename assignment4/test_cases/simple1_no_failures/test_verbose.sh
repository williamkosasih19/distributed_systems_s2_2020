#!/bin/bash

echo "true" > ../../debug.conf

echo Simple test with 9 paxos nodes and no failures
cd ../..

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

echo Let the processes run for 20 seconds, then kill all of them
sleep 20
kill $pid_0
kill $pid_1
kill $pid_2
kill $pid_3
kill $pid_4
kill $pid_5
kill $pid_6
kill $pid_7
kill $pid_8

echo Diff each logs - result are expected to be the same for all
diff M0.log M1.log
diff M0.log M2.log
diff M0.log M3.log
diff M0.log M4.log
diff M0.log M5.log
diff M0.log M6.log
diff M0.log M7.log
diff M0.log M8.log

echo ...
echo ...
echo test success
cd test_cases/simple1_no_failures
