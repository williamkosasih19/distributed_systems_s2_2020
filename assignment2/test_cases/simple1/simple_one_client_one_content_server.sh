#!/bin/bash
echo "### SIMPLE1 TEST ###"
echo This test runs the aggregate server and 1 content server, 
echo also with one client communicating to the aggregate server every second 
echo for 10 times
sleep 3
rm client.log
cd ../..
rm backup.log
xterm ./run_aggregate_server.sh&
sleep 1
xterm ./run_content_server.sh&
sleep 1
for i in $(seq 1 10)
do
  ./run_client.sh | tee -a test_cases/simple1/client.log
  sleep 1
done
killall java
cd test_cases/simple1
echo "############## DIFF WITH EXPECTED CLIENT LOG ##############"
DIFF=$(diff client.log client_expected.log)
if [ -z $DIFF ]
then
  echo "OUTPUT MATCHED!!!"
else
  echo "OUTPUT DOESN'T MATCH EXPECTED VALUE!!!"
fi