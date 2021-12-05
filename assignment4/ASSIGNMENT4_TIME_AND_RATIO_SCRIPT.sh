#!/bin/bash

rm *.log

./compile.sh


i=0
let target=$1-1

while [ $i -lt $target ]
do
  ./run_M1.sh &
  sleep 1
  let i=$i+1
done
./run_M1_start.sh &

sleep $2

killall java
cat *TIME.log > time_result_try_$3
ENUM=$(cat M0_TIME.log | wc -l) 
DENOM=$(cat *PROPOSALS.log | wc -l)
echo $ENUM:$DENOM > ratio_result_try_$3
