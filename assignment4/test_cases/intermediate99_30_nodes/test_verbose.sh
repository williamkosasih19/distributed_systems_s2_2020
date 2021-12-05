#!/bin/bash

echo "true" > ../../debug.conf

echo Simple test with 9 paxos nodes and no failures
cd ../..

rm M*.log

xterm ./run_M1.sh &
sleep 1
xterm ./run_M2.sh &
sleep 1
xterm ./run_M3.sh &
sleep 1
xterm ./run_M4.sh &
sleep 1
xterm ./run_M1.sh &
sleep 1
xterm ./run_M1.sh &
sleep 1
xterm ./run_M1.sh &
sleep 1
xterm ./run_M1.sh &
sleep 1
xterm ./run_M4.sh &
sleep 1
xterm ./run_M1.sh &
sleep 1
xterm ./run_M2.sh &
sleep 1
xterm ./run_M1.sh &
sleep 1
xterm ./run_M1.sh &
sleep 1
xterm ./run_M3.sh &
sleep 1
xterm ./run_M1.sh &
sleep 1
xterm ./run_M1.sh &
sleep 1
xterm ./run_M1.sh &
sleep 1
xterm ./run_M1.sh &
sleep 1
xterm ./run_M1.sh &
sleep 1
xterm ./run_M1.sh &
sleep 1
xterm ./run_M1.sh &
sleep 1
xterm ./run_M1.sh &
sleep 1
xterm ./run_M1.sh &
sleep 1
xterm ./run_M1.sh &
sleep 1
xterm ./run_M2.sh &
sleep 1
xterm ./run_M2.sh &
sleep 1
xterm ./run_M1.sh &
sleep 1
xterm ./run_M1.sh &
sleep 1
xterm ./run_M3.sh &
sleep 1
xterm ./run_M1_start.sh &
sleep 1


echo Let the processes run for 20 seconds, then kill all of them
sleep 20
killall java

echo Diff each logs - result are expected to be the same for all
diff M0.log M1.log
diff M0.log M2.log
diff M0.log M3.log
diff M0.log M4.log
diff M0.log M5.log
diff M0.log M6.log
diff M0.log M7.log
diff M0.log M8.log
diff M0.log M9.log
diff M0.log M10.log
diff M0.log M11.log
diff M0.log M12.log
diff M0.log M13.log
diff M0.log M14.log
diff M0.log M15.log
diff M0.log M16.log
diff M0.log M17.log
diff M0.log M18.log
diff M0.log M19.log
diff M0.log M20.log
diff M0.log M21.log
diff M0.log M22.log
diff M0.log M23.log
diff M0.log M24.log
diff M0.log M25.log
diff M0.log M26.log
diff M0.log M27.log
diff M0.log M28.log
diff M0.log M29.log
echo ...
echo ...
echo test success
cd test_cases/simple1_no_failures
