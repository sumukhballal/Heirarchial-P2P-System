
#!/bin/bash

cd p2p1
java -cp ./target main.P2P 2 1000 file_2.txt &
cd ..
cd p2p2
java -cp ./target main.P2P 2 1000 file_1.txt &
cd ..
cd p2p3
java -cp ./target main.P2P 2 1000 file_2.txt &
cd ..
cd p2p4
java -cp ./target main.P2P 2 1000 file_3.txt &
cd ..

cd p2p5
java -cp ./target main.P2P 2 1000 file_4.txt &
cd ..
cd p2p6
java -cp ./target main.P2P 2 1000 file_5.txt &
cd ..
cd p2p7
java -cp ./target main.P2P 2 1000 file_6.txt &
cd ..
cd p2p8
java -cp ./target main.P2P 2 1000 file_7.txt &
cd ..

