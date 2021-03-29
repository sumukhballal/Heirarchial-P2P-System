#!/bin/bash

cd p2p1
java -cp ./target main.P2P 2 1000 file_2.txt &
cd ..
cd p2p2
java -cp ./target main.P2P 2 1000 file_1.txt &
cd ..
