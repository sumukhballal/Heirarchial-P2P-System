#!/bin/bash

mkdir -p ../Evaluation/Eval1/

./replicate_p2p_folders.sh "../Peer Node" "../Evaluation/Eval1/" 1 8001
./replicate_p2p_folders.sh "../Peer Node" "../Evaluation/Eval1/" 2 8002
./replicate_p2p_folders.sh "../Peer Node" "../Evaluation/Eval1/" 3 8003

./replicate_index_server_folder.sh "../Indexing Server" "../Evaluation/Eval1/"

./compile_all.sh "../Evaluation/Eval1"

cp -r ./eval1_run.sh ../Evaluation/Eval1/
