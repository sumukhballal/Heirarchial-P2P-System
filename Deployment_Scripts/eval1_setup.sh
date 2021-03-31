#!/bin/bash

sudo kill -9 $(ps aux | grep java | awk '{print $2}')
rm -rf ../Evaluation/Eval1/
mkdir -p ../Evaluation/Eval1/

# All Super nodes 
./replicate_supernode_folders.sh "../Super_Peer_Node" "../Evaluation/Eval1/" 1 8000 None
./replicate_supernode_folders.sh "../Super_Peer_Node" "../Evaluation/Eval1/" 2 8001 "localhost:8000;"
./replicate_supernode_folders.sh "../Super_Peer_Node" "../Evaluation/Eval1/" 3 8002 "localhost:8000;localhost:8001"


# Clusters
./replicate_p2p_folders.sh "../Peer_Node" "../Evaluation/Eval1/" 1 8003 8000
./replicate_p2p_folders.sh "../Peer_Node" "../Evaluation/Eval1/" 2 8004 8001
./replicate_p2p_folders.sh "../Peer_Node" "../Evaluation/Eval1/" 3 8005 8002


./compile_all.sh "../Evaluation/Eval1"

cp -r ./eval1_1_run.sh ../Evaluation/Eval1/
cp -r ./eval1_2_run.sh ../Evaluation/Eval1/
