#!/bin/bash

mkdir -p ../Evaluation/Eval2/

mkdir -p ../Evaluation/Eval2/Test_1 # where n=2
./replicate_p2p_folders.sh "../Peer Node" "../Evaluation/Eval2/Test_1" 1 8004
./replicate_p2p_folders.sh "../Peer Node" "../Evaluation/Eval2/Test_1" 2 8005

mkdir -p ../Evaluation/Eval2/Test_2
./replicate_p2p_folders.sh "../Peer Node" "../Evaluation/Eval2/Test_2" 1 8006
./replicate_p2p_folders.sh "../Peer Node" "../Evaluation/Eval2/Test_2" 2 8007
./replicate_p2p_folders.sh "../Peer Node" "../Evaluation/Eval2/Test_2" 3 8008
./replicate_p2p_folders.sh "../Peer Node" "../Evaluation/Eval2/Test_2" 4 8009

mkdir -p ../Evaluation/Eval2/Test_3
./replicate_p2p_folders.sh "../Peer Node" "../Evaluation/Eval2/Test_3" 1 8010
./replicate_p2p_folders.sh "../Peer Node" "../Evaluation/Eval2/Test_3" 2 8011
./replicate_p2p_folders.sh "../Peer Node" "../Evaluation/Eval2/Test_3" 3 8012
./replicate_p2p_folders.sh "../Peer Node" "../Evaluation/Eval2/Test_3" 4 8013
./replicate_p2p_folders.sh "../Peer Node" "../Evaluation/Eval2/Test_3" 5 8014
./replicate_p2p_folders.sh "../Peer Node" "../Evaluation/Eval2/Test_3" 6 8015
./replicate_p2p_folders.sh "../Peer Node" "../Evaluation/Eval2/Test_3" 7 8016
./replicate_p2p_folders.sh "../Peer Node" "../Evaluation/Eval2/Test_3" 8 8017



./replicate_index_server_folder.sh "../Indexing Server" "../Evaluation/Eval2/"

./compile_all.sh "../Evaluation/Eval2/Test_1"
./compile_all.sh "../Evaluation/Eval2/Test_2"
./compile_all.sh "../Evaluation/Eval2/Test_3"
./compile_all.sh "../Evaluation/Eval2/"


cp -r ./eval2_run.sh ../Evaluation/Eval2/
cp -r ./eval2_1_run.sh ../Evaluation/Eval2/Test_1
cp -r ./eval2_2_run.sh ../Evaluation/Eval2/Test_2
cp -r ./eval2_3_run.sh ../Evaluation/Eval2/Test_3
