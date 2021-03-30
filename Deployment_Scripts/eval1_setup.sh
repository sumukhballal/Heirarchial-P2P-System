#!/bin/bash

mkdir -p ../Evaluation/Eval1/

# Cluster 1 
./replicate_supernode_folders.sh "../Super_Peer_Node" "../Evaluation/Eval1/" 1 8000 None
./replicate_p2p_folders.sh "../Peer_Node" "../Evaluation/Eval1/" 1 8001 8000
./replicate_p2p_folders.sh "../Peer_Node" "../Evaluation/Eval1/" 2 8002 8000
./replicate_p2p_folders.sh "../Peer_Node" "../Evaluation/Eval1/" 3 8003 8000

# Cluster 2
./replicate_supernode_folders.sh "../Super_Peer_Node" "../Evaluation/Eval1/" 2 8004 localhost:8000 
./replicate_p2p_folders.sh "../Peer_Node" "../Evaluation/Eval1/" 4 8005 8004
./replicate_p2p_folders.sh "../Peer_Node" "../Evaluation/Eval1/" 5 8006 8004
./replicate_p2p_folders.sh "../Peer_Node" "../Evaluation/Eval1/" 6 8007 8004

# Cluster 3
./replicate_supernode_folders.sh "../Super_Peer_Node" "../Evaluation/Eval1/" 3 8008 localhost:8000;localhost:8004
./replicate_p2p_folders.sh "../Peer_Node" "../Evaluation/Eval1/" 7 8009 8008
./replicate_p2p_folders.sh "../Peer_Node" "../Evaluation/Eval1/" 8 8010 8008
./replicate_p2p_folders.sh "../Peer_Node" "../Evaluation/Eval1/" 9 8011 8008

./compile_all.sh "../Evaluation/Eval1"

cp -r ./eval1_run.sh ../Evaluation/Eval1/
