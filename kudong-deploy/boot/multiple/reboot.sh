#!/bin/bash

servername="minesquare"
ram=18G
server_closing_wait_time=30
server_broadcast_wait_time=30

for screenName in $(ps -ef | grep -E 'grep|java|bash' | grep -E -o "\[([^\[]*)\]" | grep -E "*-minecraft")
do
	echo "Killing ${screenName} SERVER ....";
	screen -S $screenName -X stuff "^M^Mbroadcast 서버 안정화를 위해 30초 뒤 서버가 재부팅 됩니다...^M"

	for sec in $(eval echo {1..$server_broadcast_wait_time})
	do
		echo Wait Server Close Broadcast $(expr ${server_broadcast_wait_time} + 1 - ${sec}) Seconds .....
		sleep 1;
	done

	screen -S $screenName -X stuff "^M^Mstop^M"
	echo "${screenName}'s minecraft server closed.";
done

for sec in $(eval echo {1..$server_closing_wait_time})
do
	echo Wait Server Closing.... $(expr ${server_closing_wait_time} + 1 - ${sec}) Seconds .....
	sleep 1;
done

for pid in $(ps -ef | grep -E 'java -Duser.timezone=KST -Du=minesquare-minecraft' | grep -v -E 'grep|SCREEN|bash' | awk '{print $2}')
do
	kill $pid;
	screen -S [${servername}-minecraft] -X stuff \"^C\"
	sleep 3;
done

cd ./minecraft
screen -dmS [${servername}-minecraft] java -Duser.timezone=KST -Du=minesquare-minecraft -Xms$ram -Xmx$ram -server -jar paper.jar -nogui
cd ..
echo "STARTING SERVER => [${servername}-minecraft]"
