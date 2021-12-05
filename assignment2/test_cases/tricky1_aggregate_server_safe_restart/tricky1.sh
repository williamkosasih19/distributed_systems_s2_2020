#!/bin/bash
echo "### TRICKY1 TEST ###"
echo This test runs the aggregate server and 10 content servers, 
echo runs the client once, then prematurely kill the aggregate server
echo then expect the aggregate server to restore its state before being killed
echo and connect succesfully with the content servers again
sleep 3
rm client.log
cd ../..
rm backup.log
xterm ./run_aggregate_server.sh&
AGGREGATE_SERVER_PID=$!

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

sleep 2
./run_client.sh | tee -a test_cases/tricky1_aggregate_server_safe_restart/client.log
sleep 2

echo killing the aggregate server...
sleep 3
kill $AGGREGATE_SERVER_PID
sleep 2
echo restarting the aggregate server
sleep 2
xterm ./run_aggregate_server.sh&
sleep 2

echo sleeping for 20 seconds to make sure the aggregate server
echo keeps its connection to the content servers

sleep 20
./run_client.sh | tee -a test_cases/tricky1_aggregate_server_safe_restart/client.log
killall java
cd test_cases/tricky1_aggregate_server_safe_restart
echo "############## DIFF WITH EXPECTED CLIENT LOG ##############"
echo "Clock is not expected to be exact as there may be timing difference for each runs"
sleep 3
diff client.log client_expected.log