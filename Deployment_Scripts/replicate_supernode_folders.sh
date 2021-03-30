#!/bin/bash


input_directory=$1
output_directory=$2
node_number=$3
super_peer_port_number=$4
super_peer_connection_string=$5

# Create the Peer Node directory

output="$output_directory/super_p2p$node_number"

mkdir -p "$output"
cp -r "$input_directory/." "$output"

# Chnage config properties

sed -i "s/change_super_node_port/$super_peer_port_number/g" "$output/resources/config.properties"
sed -i "s/change_super_node_connection_string/$super_peer_connection_string/g" "$output/resources/config.properties"

