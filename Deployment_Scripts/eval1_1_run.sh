#!/bin/bash

echo "Starting all Super Peer Nodes! "

for ((i = 1 ; i <= 2 ; i++));
do 
	echo "Starting Super peer node $i"
	cd super_p2p$i
	java -cp ./target main.SuperPeerNode &
	cd ..
	sleep 10
done

echo ""
echo "Starting all P2p nodes! "

for ((i = 1 ; i <= 2 ; i++));
do
	cd p2p$i
	java -cp ./target main.P2P 1 file_2.txt &
	cd ..
done
