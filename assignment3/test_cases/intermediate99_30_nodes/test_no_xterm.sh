#!/bin/bash

echo Intermediate test with 30 nodes and no failures

CURRENTDIR=$(pwd)
cd "$(dirname $(pwd)/$0)/../.."

./compile.sh

rm M*.log

java -cp "./out" Voting M1 &
first=$!
sleep 1
java -cp "./out" Voting M2 &
sleep 1
java -cp "./out" Voting M3 &
sleep 1
java -cp "./out" Voting M4 &
sleep 1
java -cp "./out" Voting M1 &
sleep 1
java -cp "./out" Voting M1 &
sleep 1
java -cp "./out" Voting M1 &
sleep 1
java -cp "./out" Voting M1 &
sleep 1
java -cp "./out" Voting M4 &
sleep 1
java -cp "./out" Voting M1 &
sleep 1
java -cp "./out" Voting M2 &
sleep 1
java -cp "./out" Voting M1 &
sleep 1
java -cp "./out" Voting M1 &
sleep 1
java -cp "./out" Voting M3 &
sleep 1
java -cp "./out" Voting M1 &
sleep 1
java -cp "./out" Voting M1 &
sleep 1
java -cp "./out" Voting M1 &
sleep 1
java -cp "./out" Voting M1 &
sleep 1
java -cp "./out" Voting M1 &
sleep 1
java -cp "./out" Voting M1 &
sleep 1
java -cp "./out" Voting M1 &
sleep 1
java -cp "./out" Voting M1 &
sleep 1
java -cp "./out" Voting M1 &
sleep 1
java -cp "./out" Voting M1 &
sleep 1
java -cp "./out" Voting M2 &
sleep 1
java -cp "./out" Voting M2 &
sleep 1
java -cp "./out" Voting M1 &
sleep 1
java -cp "./out" Voting M1 &
sleep 1
java -cp "./out" Voting M3 &
sleep 1
java -cp "./out" Voting M1 start &
sleep 1


echo Let the processes run for 150 seconds, then kill all of them
sleep 150
kill $first
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
DIFF9=$(diff M0.log M9.log)
DIFF10=$(diff M0.log M10.log)
DIFF11=$(diff M0.log M11.log)
DIFF12=$(diff M0.log M12.log)
DIFF13=$(diff M0.log M13.log)
DIFF14=$(diff M0.log M14.log)
DIFF15=$(diff M0.log M15.log)
DIFF16=$(diff M0.log M16.log)
DIFF17=$(diff M0.log M17.log)
DIFF18=$(diff M0.log M18.log)
DIFF19=$(diff M0.log M19.log)
DIFF20=$(diff M0.log M20.log)
DIFF21=$(diff M0.log M21.log)
DIFF22=$(diff M0.log M22.log)
DIFF23=$(diff M0.log M23.log)
DIFF24=$(diff M0.log M24.log)
DIFF25=$(diff M0.log M25.log)
DIFF26=$(diff M0.log M26.log)
DIFF27=$(diff M0.log M27.log)
DIFF28=$(diff M0.log M28.log)
DIFF29=$(diff M0.log M29.log)

DIFF_FINAL="$DIFF1$DIFF2$DIFF3$DIFF4$DIFF5$DIFF6$DIFF7$DIFF8$DIFF9$DIFF10$DIFF11$DIFF12$DIFF13$DIFF14$DIFF15$DIFF16$DIFF17$DIFF18$DIFF19$DIFF20$DIFF21$DIFF22$DIFF23$DIFF24$DIFF25$DIFF26$DIFF27$DIFF28$DIFF29"
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
