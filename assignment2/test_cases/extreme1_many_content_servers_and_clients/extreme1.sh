#!/bin/bash
echo "### EXTREME1 TEST ###"
echo This test runs the aggregate server and many clients and content servers concurently
echo This test is meant to check whether the aggreagate server is able to handle many concurent requests without crashing
echo This test also tests the server\'s ability to recover from a failure, while handling multiple incoming requests
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
CONTENT_SERVER_PID_1=$!
sleep 1
xterm ./run_content_server.sh&
sleep 1
xterm ./run_content_server.sh&

xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
CONTENT_SERVER_PID_2=$!
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
CONTENT_SERVER_PID_3=$!
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
CONTENT_SERVER_PID_4=$!
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
kill $CONTENT_SERVER_PID_3
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
CONTENT_SERVER_PID_5=$!
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
kill $CONTENT_SERVER_PID_1
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
kill $CONTENT_SERVER_PID_2
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&

sleep 2
./run_client.sh | tee -a test_cases/extreme1_many_content_servers_and_clients/client.log
sleep 2

xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
kill $CONTENT_SERVER_PID_4
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&

sleep 1
./run_client.sh | tee -a test_cases/extreme1_many_content_servers_and_clients/client.log
sleep 1

echo killing the aggregate server...
sleep 3
kill $AGGREGATE_SERVER_PID
kill $CONTENT_SERVER_PID_5
sleep 2
echo restarting the aggregate server
sleep 2
xterm ./run_aggregate_server.sh&
sleep 2

xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_client.sh&
xterm ./run_content_server.sh&
xterm ./run_content_server.sh&
xterm ./run_client.sh&

echo sleeping for 20 seconds to make sure the aggregate server
echo keeps its connection to the content servers

sleep 20
./run_client.sh | tee -a test_cases/extreme1_many_content_servers_and_clients/client.log
killall java
cd test_cases/extreme1_many_content_servers_and_clients
echo "############## DIFF WITH EXPECTED CLIENT LOG ##############"
echo "Clock is not expected to be exact as there may be timing difference for each runs"
sleep 3
diff client.log client_expected.log