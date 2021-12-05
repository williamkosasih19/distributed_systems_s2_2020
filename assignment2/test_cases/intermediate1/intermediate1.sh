#!/bin/bash
echo "### INTERMEDIATE1 TEST ###"
echo This test runs the aggregate server and 10 content servers, 
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
xterm ./run_content_server.sh&
sleep 1
xterm ./run_content_server.sh&
sleep 1
xterm ./run_content_server.sh&
sleep 1
xterm ./run_content_server.sh&
sleep 1
xterm ./run_content_server.sh&
sleep 1
xterm ./run_content_server.sh&
sleep 1
xterm ./run_content_server.sh&
sleep 1
xterm ./run_content_server.sh&
sleep 1
xterm ./run_content_server.sh&
sleep 1
for i in $(seq 1 10)
do
  ./run_client.sh | tee -a test_cases/intermediate1/client.log
  sleep 1
done
killall java
cd test_cases/intermediate1
echo "############## DIFF WITH EXPECTED CLIENT LOG ##############"
echo "Clock is not expected to be exact as there may be timing difference for each runs"
sleep 3
diff client.log client_expected.log