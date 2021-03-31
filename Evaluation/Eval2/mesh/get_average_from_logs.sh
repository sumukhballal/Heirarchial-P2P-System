#!/bin/bash

logs=$1
time=$(cat $logs | grep avg_response_time | awk '{print $4}')

echo $(($time/1000000)) " milli seconds"
echo $(($time/1000000000)) " seconds"

