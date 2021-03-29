#!/bin/bash


input_directory=$1
output_directory=$2
node_number=$3
port_number=$4

# Create the Peer Node directory

output="$output_directory/p2p$node_number"

mkdir -p "$output"
cp -r "$input_directory/." "$output"

# Chnage config properties

sed -i "s/change_port_number/$port_number/g" "$output/resources/config.properties"

# Create a file in the file directory

dd if=/dev/zero of="$output/files/file_$node_number.txt"  bs=1000  count=1
