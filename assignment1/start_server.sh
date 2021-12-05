#!/bin/bash
killall rmiregistry
sleep 2
cd out
rmiregistry&
cd ..
echo waiting 5 seconds before starting server
sleep 5
java -cp ./out SorterServer 