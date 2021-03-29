#!/bin/bash

# Start Indexing server

size=$1

cd indexserver
java -cp ./target main.IndexingServer &
cd ..

wait 10

cd p2p1
java -cp ./target main.P2P 3 2 file_2_$size.txt &
cd ..
cd p2p2
java -cp ./target main.P2P 3 2 file_1_$size.txt &
cd ..
cd p2p3
java -cp ./target main.P2P 3 2 file_1_$size.txt &
cd ..
cd p2p4
java -cp ./target main.P2P 3 2 file_2_$size.txt &
cd ..


