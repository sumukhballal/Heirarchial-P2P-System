#!/bin/bash

sudo kill -9 $(ps aux | grep java | awk '{print $2}')
rm -rf ../Evaluation/Eval2/
mkdir -p ../Evaluation/Eval2/




echo "Setup mesh network! "
# All Super nodes 
./replicate_supernode_folders.sh "../Super_Peer_Node" "../Evaluation/Eval2/mesh/" 1 8000 None

connection_string="localhost:8000;"
port=8000

for ((i = 2 ; i <= 10 ; i++));
do

port=$(($port+1))
current_string="localhost:$port;"

./replicate_supernode_folders.sh "../Super_Peer_Node" "../Evaluation/Eval2/mesh/" $i $port "$connection_string"
connection_string=$connection_string$current_string
done


# Clusters

superpeerport=8000

for ((i = 1 ; i <= 10 ; i++));
do
port=$(($port+1))
./replicate_p2p_folders.sh "../Peer_Node" "../Evaluation/Eval2/mesh/" $i $port $superpeerport
superpeerport=$(($superpeerport+1))
k=$((k+1))
done


echo "Setup Linear network! "
# All Super nodes
./replicate_supernode_folders.sh "../Super_Peer_Node" "../Evaluation/Eval2/linear/" 1 8000 None

port=8000

for ((i = 2 ; i <= 10 ; i++));
do
port=$(($port+1))
current_string="localhost:$((port-1));"
./replicate_supernode_folders.sh "../Super_Peer_Node" "../Evaluation/Eval2/linear/" $i $port "$current_string"
done


# Clusters

superpeerport=8000

for ((i = 1 ; i <= 10 ; i++));
do
port=$(($port+1))
./replicate_p2p_folders.sh "../Peer_Node" "../Evaluation/Eval2/linear/" $i $port $superpeerport
superpeerport=$(($superpeerport+1))
k=$((k+1))
done


./compile_all.sh "../Evaluation/Eval2/mesh"
./compile_all.sh "../Evaluation/Eval2/linear"

cp -r ./eval2_mesh_run.sh ../Evaluation/Eval2/mesh/
cp -r ./eval2_linear_run.sh ../Evaluation/Eval2/linear/
cp -r ./get_average_from_logs.sh ../Evaluation/Eval2/mesh/
cp -r ./get_average_from_logs.sh ../Evaluation/Eval2/linear/


