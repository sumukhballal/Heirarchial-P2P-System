#!/bin/bash

input_directory=$1


for f in $(find $input_directory -maxdepth 1 -mindepth 1 -type d -printf '%f\n');
do
	make compile -C "$input_directory/$f/" 
done
