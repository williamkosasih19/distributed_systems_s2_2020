#!/bin/bash
DEBUG=""
if [ $(cat debug.conf) == "true" ]
then
  DEBUG="DEBUG"
fi
java -cp "./out" Voting M1 $DEBUG