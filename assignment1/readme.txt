My server implements separate stack for each clients

To build the java files: ./build.sh
  - The output java classes will be in directory ./out
To start the server: ./start_server.sh
  - This will set the rmiregistry with the correct path
  - and execute the server

I added two testing bash script thzt performs various tests:
  - ./test_multi_threaded1.sh
  - ./test_multi_threaded2.sh

The SorterClientMultiThreadedTest java program takes custom test scripts
as its input, and spawns multiple threads that will run the test script.
Each threads has a unique UUID so they can be be treated as separate clients.

To test single threaded client: java -cp ./out SorterClientMultiThreadedTest test_cases/<test case name>
to test multithreaded : java -cp ./out SorterClientMultiThreadedTest test_cases/<test 1> test_cases/<test 2> test_cases/<test 3> ...

so running java -cp ./out SorterClientMultiThreadedTest test_cases/<test 1> test_cases/<test 2> test_cases/<test 3> ... test_cases/<test 10>
will spawn 10 threads each with its own unique id and hence the server will give each of them their own stack

test commands:
  - The tester program will interpret these commands from a text files
  
  1. pv: push value to the stack
       ex: pv 5 - will push 5 to the stack
  2. po: push operator to the stack
       ex: po descending - will push to descending operator to the stack
  3. pop: pop a value from the stack, and the value will be stored in an internal variable. We can use cv to test the result of this operation
       ex: pop - will pop the top of the stack and store it in an internal varaible.
  4. cv: check the result of the last popped value from the stack 
       ex: cv 5 - compare the last popped value form the stack and test if it matchess out expectation. Value mismatch will terminate the testing program and print an error message showing the expected value and popped value
  5. ie: check if the stack is empty
       ex: ie false - Returns error if the stack is empty
  6. printv: Print the last popped value
       ex: pv
  7. pcv: Pop and check value
       ex: pcv 5 - Pop a value from the stack and throw an error if the popped result is not 5
  8. wpopcv: Pop, check value, and test delay
       usage: wpopcv <expected value> <delay> <tolerance>
       ex: wpopcv 5 500 0.03
           - Pop the value from the stack
           - Check if the delay from executing this command to getting the value is within the range of [(1.0 +/- tolerance) * delay]. throw an error if it's not within the range of that time
           - Check if the value matches <expected value> Throw an error if it's not
           + So in this example, the value popped from the stack shoul have a value of 5
           + and the time it takes to get the value should be in the range of [485...515] miliseconds

(I think) my implementation prevents race condition from happening when stacks are accessed from different clients.
Test cases such as test_cases/test_large_number* push and pops large number of values to the stack and pops them and check if everything is 
pushed and popped correctly and that race condition doesn't happen.
  