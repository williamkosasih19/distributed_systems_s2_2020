Assignment 3
William Kosasih - A1755770

In this assignment, I have written a program that implements a Paxos voting protocol 
for Suburbs Council President that is fault tolerant and resilient 
to various failure types, with all communication happens strictly via sockets. 

Assesment checklist:

1. Paxos implementation works when two councillors send voting proposals at the same time
  Status : Working
  Description: Implementation works for multiple proposers proposing at the same time for an arbitrary number of nodes nodes
               where n >= 3
               
2. Paxos implementation works in the case where all M1-M9 have immediate responses to voting queries
  Status : Working
  Description : Implementation works for all 9 members respond to voting queries immdeiately. 
                This can be simulated by launching 9 instances of the voting program with parameter M1
                
3. Paxos implementation works when M1 – M9 have responses to voting queries suggested by the profiles above, 
   including when M2 or M3 propose and then go offline
  Status : Working
  Description : Implementation works in the case of any x number of instances go down where x < n / 2 of the total nodes
  
4. Testing harness for the above scenarios + evidence that they work (in the form of printouts)
  Status : Working
  Description : Automatic testing scripts are included, and the result of the consensus is stored for each clients
  in M<x>.log where x is the instance number of each applications launched.
  Each test scrips also does diff at the end of the execution, to show that all programs indeed reached a consistent consensus
  
Bonus Assesment checklist:
1. Paxos implementation works with a number ‘n’ of councilors with four profiles of response times: immediate;  medium; late; never
  Status : Working
  Description : Implementation works for arbitrary number of voters n where n >= 3
                Each councilor also has different reponse time, determined by their couciler type (e.g M1, M2, M3, M4)
                This is defined in Constants.java:GetAcceptorResponseTime

General information:
+ A node acts as a proposer, acceptor and a learner.
+ A node may decide to be a proposer at anytime. The probability of a node to become
+ a proposer for every period (defined by Constants.PROPOSER_TURNAROUND_TIME)miliseconds is retrieved from the function: double Constants.GetProposerProbability(String memberType)

* The consensus is stored by each node in Mx.log (M0.log, M1.log, M2.log, etc)
* This log file is used for testing.

Compilation:
  Running the ./compile.sh script in the main directory should compile the program, and output the compilation result in the out directory
Testing:
  1. Manually:
    a. compile the program by running ./compile.sh
    b. Run any of the run_M<x>.sh to start an instance of the voting program. Repeat this step n-1 times if you wish to run n instances 
    b1. It might be a good idea to run the program in the background to allow multiple instances to run in case of only a single terminal available.
    c. For the last instance of the program, run run_M1_start.sh. This script lets the other instances know that its the time for all instances to start.
  2. Automatic:
    Run any of the test scripts inside test_cases/<subdir>
    ## use test_no_xterm*.sh if no xterm.
Output:
  1. The learners of each node stores the result of the consensus algorithm to M<x>.log where x is the id of the node.
     These logs are the proof that the algorithm works. Checking this is as easy as running diff through each of them.
     Expect exact same result for each of the log file (as long as the process is not killed prematurely)
     
######################## IMPORTANT ########################
1. Sometimes ports don't close propperly after each test runs. If the application does not run as expected,
   please allow a few minutes for the close to be properly closed. This is dues to the application relying on the OS
   to tell which ports are open in order to determine the correct offset for each sockets.
2. The issue above might cause inconsistencies in the result as well.
