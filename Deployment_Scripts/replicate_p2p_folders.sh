#!/bin/bash


input_directory=$1
output_directory=$2
node_number=$3
peer_port_number=$4
super_port_number=$5

# Create the Peer Node directory

output="$output_directory/p2p$node_number"

mkdir -p "$output"
cp -r "$input_directory/." "$output"

# Chnage config properties

sed -i "s/change_peer_node_port/$peer_port_number/g" "$output/resources/config.properties"
sed -i "s/change_super_peer_port/$super_port_number/g" "$output/resources/config.properties"

# Create a file in the file directory
dd if=/dev/zero of="$output/files/file_$node_number.txt"  bs=5000  count=1
