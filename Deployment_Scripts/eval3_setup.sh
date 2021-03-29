#!/bin/bash

mkdir -p ../Evaluation/Eval3/

./replicate_p2p_folders.sh "../Peer Node" "../Evaluation/Eval3/" 1 8020
./replicate_p2p_folders.sh "../Peer Node" "../Evaluation/Eval3/" 2 8021
./replicate_p2p_folders.sh "../Peer Node" "../Evaluation/Eval3/" 3 8022
./replicate_p2p_folders.sh "../Peer Node" "../Evaluation/Eval3/" 4 8023

./replicate_index_server_folder.sh "../Indexing Server" "../Evaluation/Eval3/"

./compile_all.sh "../Evaluation/Eval3"

cp -r ./eval3_run.sh ../Evaluation/Eval3/

./create_files_eval3.sh "../Evaluation/Eval3/p2p1" 1
./create_files_eval3.sh "../Evaluation/Eval3/p2p2" 2
./create_files_eval3.sh "../Evaluation/Eval3/p2p3" 3
./create_files_eval3.sh "../Evaluation/Eval3/p2p4" 4
