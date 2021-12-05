#!/bin/bash

rm *.log

./compile.sh


i=0

./run_M1.sh &
sleep 1
./run_M1.sh &
pid1=$!
sleep 1
./run_M1.sh &
sleep 1
./run_M1.sh &
pid2=$!
sleep 1
./run_M1.sh &
sleep 1
./run_M1.sh &
pid3=$!
sleep 1
./run_M1.sh &
pid4=$!
sleep 1
./run_M1.sh &
sleep 1
./run_M1_start.sh &

kill $pid1
kill $pid2
kill $pid3
kill $pid4

sleep $2

killall java
cat *TIME.log > time_result_failure_try_$3
ENUM=$(cat M0_TIME.log | wc -l) 
DENOM=$(cat *PROPOSALS.log | wc -l)
echo $ENUM:$DENOM > ratio_result_failure_try_$3
