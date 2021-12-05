#!/bin/bash
java -cp ./out SorterClientMultithreadedTest test_cases/test_large_number_ascending 
java -cp ./out SorterClientMultithreadedTest test_cases/test_large_number_descending 
java -cp ./out SorterClientMultithreadedTest test_cases/test_large_number_max 
java -cp ./out SorterClientMultithreadedTest test_cases/test_large_number_min 
java -cp ./out SorterClientMultithreadedTest test_cases/test_min 
java -cp ./out SorterClientMultithreadedTest test_cases/test_max 
java -cp ./out SorterClientMultithreadedTest test_cases/test_ascending 
java -cp ./out SorterClientMultithreadedTest test_cases/test_descending 
java -cp ./out SorterClientMultithreadedTest test_cases/test_intertwined 
java -cp ./out SorterClientMultithreadedTest test_cases/test_delay 
java -cp ./out SorterClientMultithreadedTest test_cases/test_delay_long

