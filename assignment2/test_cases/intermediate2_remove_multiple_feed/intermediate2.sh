#!/bin/bash
echo "### INTERMEDIATE2 TEST ###"
echo This test runs the aggregate server and 5 content server, runs the client once before the contentservers are killed,
echo kills two of the contentserver, and launch the client program after 20 seconds
echo The aggregate server is expected return no feed
sleep 3
rm client.log
cd ../..
rm backup.log
xterm ./run_aggregate_server.sh&
sleep 1
xterm ./run_content_server.sh&
CONTENT_SERVER_PID1=$!
xterm ./run_content_server.sh&
sleep 1
xterm ./run_content_server.sh&
sleep 1
xterm ./run_content_server.sh&
sleep 1
xterm ./run_content_server.sh&
CONTENT_SERVER_PID2=$!
sleep 2
./run_client.sh | tee -a test_cases/intermediate2_remove_multiple_feed/client.log
sleep 2
echo killing two content servers...
kill $CONTENT_SERVER_PID1
kill $CONTENT_SERVER_PID2
echo sleeping for 20 seconds
sleep 20
./run_client.sh | tee -a test_cases/intermediate2_remove_multiple_feed/client.log
killall java
cd test_cases/intermediate2_remove_multiple_feed
echo "############## DIFF WITH EXPECTED CLIENT LOG ##############"
echo "Clock is not expected to be exact as there may be timing difference for each runs"
sleep 3
diff client.log client_expected.log