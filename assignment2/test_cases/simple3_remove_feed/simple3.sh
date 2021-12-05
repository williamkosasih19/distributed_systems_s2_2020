#!/bin/bash
echo "### SIMPLE3 TEST ###"
echo This test runs the aggregate server and 1 content server, 
echo kills the contentserver, and launch the client program after 15 seconds
echo The aggregate server is expected return no feed
sleep 3
rm client.log
cd ../..
rm backup.log
xterm ./run_aggregate_server.sh&
sleep 1
xterm ./run_content_server.sh&
CONTENT_SERVER_PID=$!
sleep 2
./run_client.sh | tee -a test_cases/simple3_remove_feed/client.log
sleep 2
echo killing the content server...
kill $CONTENT_SERVER_PID
echo sleeping for 15 seconds
sleep 15
./run_client.sh | tee -a test_cases/simple3_remove_feed/client.log
killall java
cd test_cases/simple3_remove_feed
echo "############## DIFF WITH EXPECTED CLIENT LOG ##############"
echo "Clock is not expected to be exact as there may be timing difference for each runs"
sleep 3
diff client.log client_expected.log