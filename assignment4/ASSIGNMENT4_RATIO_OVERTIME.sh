#!/bin/bash

./compile.sh

rm M*.log

./run_M1.sh &
pid_0=$!
sleep 1
./run_M1.sh &
pid_1=$!
sleep 1
./run_M1.sh &
pid_2=$!
sleep 1
./run_M1.sh &
pid_3=$!
sleep 1
./run_M1.sh &
pid_4=$!
sleep 1
./run_M1.sh &
pid_5=$!
sleep 1
./run_M1.sh &
pid_6=$!
sleep 1
./run_M1.sh &
pid_7=$!
sleep 1
./run_M1_start.sh &
pid_8=$!

kill $pid_1
kill $pid_3
kill $pid_5
kill $pid_7

rm ratio_overtime_failure.txt

while [ 1 == 1 ]
do
  sleep 20
  PROPOSALS_SENT=$(cat M*_PROPOSALS.log | wc -l)
  PORPOSALS_ACCEPTED=$(cat M0_TIME.log | wc -l)
  echo $PROPOSALS_SENT:$PORPOSALS_ACCEPTED >> ratio_overtime_failure.txt
done
