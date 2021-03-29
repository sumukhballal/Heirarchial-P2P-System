#!/bin/bash

# Start Indexing server

cd indexserver
java -cp ./target main.IndexingServer &
cd ..

wait 10

cd p2p1
java -cp ./target main.P2P 1 file_2.txt &
cd ..
cd p2p2
java -cp ./target main.P2P 1 file_1.txt &
cd ..
cd p2p3
java -cp ./target main.P2P 1 file_2.txt &
cd ..



