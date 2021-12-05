#!/bin/bash

if [ $1 == "DEBUG" ]
then
  echo "true" > ../../debug.conf
else
  echo "false" > ../../debug.conf
fi

echo Simple test with 9 paxos nodes and no failures
cd ../..

./compile.sh

rm M*.log

xterm ./run_M1.sh &
pid_0=$!
sleep 1
xterm ./run_M1.sh &
pid_1=$!
sleep 1
xterm ./run_M1.sh &
pid_2=$!
sleep 1
xterm ./run_M1.sh &
pid_3=$!
sleep 1
xterm ./run_M1.sh &
pid_4=$!
sleep 1
xterm ./run_M1.sh &
pid_5=$!
sleep 1
xterm ./run_M1.sh &
pid_6=$!
sleep 1
xterm ./run_M1.sh &
pid_7=$!
sleep 1
xterm ./run_M1_start.sh &
pid_8=$!


while [ 1 == 1 ]
do
  sleep 20
  PROPOSALS_SENT=$(cat M*_PROPOSALS.log | wc -l)
  PORPOSALS_ACCEPTED=$(cat M1_TIME.log | wc -l)
  echo $PROPOSALS_SENT:$PORPOSALS_ACCEPTED >> ratio.txt
done

echo ...
echo ...
echo test success
cd test_cases/simple1_no_failures
