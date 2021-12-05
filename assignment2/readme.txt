Assignment 2 - News Aggregation System
William Kosasih - A1755770

In this assignment, I implemented aggregation system described below, 
including a failure management system.  This includes client, server and network failure.

1. Multiple clients may attempt to GET simultaneously and are required to GET
   the aggregated feed that is correct for the Lamport clock adjusted time if 
   interleaved with any PUTs. Hence, if A PUT, a GET, and another PUT arrive in 
   that sequence then the first PUT must be applied and the content server advised, 
   then the GET returns the updated feed to the client then the next PUT is applied. In each case, the participants will be guaranteed that this order is maintained if they are using Lamport clocks.
2. Multiple content servers may attempt to simultaneously PUT. 
   This must be serialised and the order maintained by Lamport clock timestamp.
3. My aggregation server will expire and remove any content from a content 
   server that it has not communicated within the last 12 seconds.
4. All elements in my assignment must be capable of implementing Lamport clocks,
   for synchronization and coordination purposes.
   
An external library is used in my implementation, namely: Apache-commons-lang3.
Some extra parameters are needed in order to compile and run the code.
Those can be referenced in the build script I added, thos includes:
1. compile.sh: Build all and link all components, and output the to the directory: output
2. run_aggregate_server.sh <portnumber> : Run the aggregate server
3. run_content_server.sh <feed.txt path> <aggregate server address> <aggregate server port> : Run the content server
4. run_client.sh <aggregate server address> <aggregate server port>: run the client.

I have also created a directory containing test cases, this can be found in the directory
./test_cases.
There are multiple sub-directories in the test_cases directory, each is labelled with the difficulty level of the test cases
ranging from simple to extreme.
In each of the subdirectories, there also exists a bash script that will effectively run the test.

For example for the test simple2 under ./test_cases/simple2
we can run simple2.sh that will do the test.

Each test contains information of what it does, and shows the difference between the expected output and the output got
from running the tests.

Each test case subdirectories typically contains these files:
1. <test name>.sh: The test script for the test
2. client_expected.log: The expected value for the test
3. (Output)client.log: The output from the client after running the test.

the extreme1 test case is meant to demonstrate the server's ability to handle multiple
concurent request from different clients, content servers, and ping to the content servers concurently without crashing.

The code should be able to handle corrupt feed.txt and/or corrupt XML both in the content server, aggregate server part, and client part.
