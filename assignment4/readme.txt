Assignment 4
William Kosasih - A1755770

ASSIGNMENT4_TIME_AND_RATIO_SCRIPT
- Run the time and the consensus vs proposer test. 
- Needs 3 parameters:
  + 1. The number of nodes
  + 2. Test length (in seconds)
  + 3. Test number
- The output files include:
  - time_result_try_<test number> : the time it takes for each learner from being initiated until a consensus is reached. One line = one reading - Used to generate the average time and time histogram
  - ratio_result_try_<test number> : Output is a single line <number of consensus>:<number of proposals> - used to calculate the average ratio

ASSIGNMENT4_RATIO_OVERTIME.sh:
- Run the time and the consensus vs proposer test with 9 nodes and 4 failures
- Needs (3) parameters:
  + 1. UNUSED
  + 2. Test length (in seconds)
  + 3. Test number
- The output files include:
  - time_result_failure_try_<test number> : the time it takes for each learner from being initiated until a consensus is reached. One line = one reading - Used to generate the average time and time histogram
  - ratio_result_failure_try_<test number> : Output is a single line <number of consensus>:<number of proposals> - used to calculate the average ratio
  
ASSIGNMENT4_RATIO_OVERTIME.sh
- Run tests with 9 nodes and 4 failures/
- Generates ratio every 20 seconds
- Used to generate the line diagram for consensus:proposals every 20 seconds