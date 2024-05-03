#!/bin/bash

servername="minesquare"
ram=18G
wait_time=3

echo == START SERVER PROCESS ==

for sec in $(eval echo {1..$wait_time})
do
	echo Wait $(expr ${wait_time} + 1 - ${sec}) Seconds .....
	sleep 1;
done

echo "STARTING SERVER => [${servername}-minecraft]"

cd ./minecraft
screen -dmS [${servername}-minecraft] java -Duser.timezone=KST -Du=minesquare-minecraft -Xms$ram -Xmx$ram -server -jar paper.jar -nogui
cd ..