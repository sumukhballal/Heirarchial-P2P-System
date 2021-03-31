#!/bin/bash


clients=$1
sudo kill -9 $(ps aux | grep java | awk '{print $2}')


echo "Starting all Super Peer Nodes! "

for ((i = 1 ; i <= 10 ; i++));
do
        echo "Starting Super peer node $i"
        cd super_p2p$i
        java -cp ./target main.SuperPeerNode 1 &
        cd ..
        sleep 10
done


echo "---------------------------------------------------------------------"
echo "Starting the number of p2p nodes requested! "
echo "This will generate 300 download requests!"
echo "Use the average script to calculate the average for the folder you want!"
echo "----------------------------------------------------------------------"

for ((i = $(($clients+1)) ; i <= 10 ; i++));
do
	echo "Running the remaining client p2p$i in server only mode"
	cd p2p$i
        java -cp ./target main.P2P 3 &
        cd ..
done

echo " "
echo "----------"
echo "Now starting those clients which are going to be donwloading a random file 300 times! "
echo "----------"
echo " "

for (( i = 1 ; i <= $clients ; i++ ));
do
	echo ""
	echo "Starting p2p node $i , folder p2p$i it will try to download file file_$((10-$i)).txt"
	cd p2p$i
	java -cp ./target main.P2P 2 300 file_$((10-$i)).txt &
	cd ..
	echo ""
done

echo "------------------------"
echo "Script is done! Check your logs! "
echo "------------------------"
