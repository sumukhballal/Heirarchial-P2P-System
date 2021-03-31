#!/bin/bash

sudo kill -9 $(ps aux | grep java | awk '{print $2}')

echo "Starting all Super Peer Nodes! "

s=10
for ((i = 1 ; i <= 3 ; i++));
do 
	echo "Starting Super peer node $i"
	cd super_p2p$i
	java -cp ./target main.SuperPeerNode &
	cd ..
	sleep $s
	s=$((s+20))
done

echo ""
echo "Starting all P2p nodes! "

k=3

for ((i = 1 ; i <= 3 ; i++));
do
	cd p2p$i
	java -cp ./target main.P2P 1 file_$k.txt &
	k=$((k-1))
	cd ..
done
