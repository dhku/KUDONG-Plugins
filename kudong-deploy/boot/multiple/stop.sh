#!/bin/bash

server_closing_wait_time=5
server_broadcast_wait_time=5
minecraftList=()

#Broadcast Message
for screenName in $(ps -ef | grep -E 'grep|java|bash' | grep -E -o "\[([^\[]*)\]" | grep -E "*-minecraft")
do	
	minecraftList+=($screenName)
	screen -S $screenName -X stuff "^M^Mbroadcast 서버 안정화를 위해 ${server_broadcast_wait_time}초 뒤 서버가 재부팅 됩니다...^M"
done
 
for sec in $(eval echo {1..$server_broadcast_wait_time})
do
	echo Wait Server Close Broadcast $(expr ${server_broadcast_wait_time} + 1 - ${sec}) Seconds .....
	sleep 1;
done

#Close Proxy Server
for screenName in $(ps -ef | grep -E 'grep|java|bash' | grep -E -o "\[([^\[]*)\]" | grep -E "*-proxy")
do
	echo "Killing ${screenName} PROXY SERVER ....";
	screen -S $screenName -X stuff "^M^Mend^M"
	echo "${screenName}'s Proxy server closed.";
done

#Close Minecraft Server
for screenName in $(ps -ef | grep -E 'grep|java|bash' | grep -E -o "\[([^\[]*)\]" | grep -E "*-minecraft")
do	
	echo "Killing ${screenName} SERVER ....";
	screen -S $screenName -X stuff "^M^Msave-all^M"
	screen -S $screenName -X stuff "^M^Mstop^M"
	echo "${screenName}'s minecraft server closed.";
done

for sec in $(eval echo {1..$server_closing_wait_time})
do
	echo Wait Server Closing.... $(expr ${server_closing_wait_time} + 1 - ${sec}) Seconds .....
	sleep 1;
done

for screenName in ${minecraftList[@]};
do
	for pid in $(ps -ef | grep -E 'java -Duser.timezone=KST -Du=${screenName}-minecraft' | grep -v -E 'grep|SCREEN|bash' | awk '{print $2}')
	do
		kill $pid;
		screen -S [${screenName}-minecraft] -X stuff "^C^C"
	done
	echo "KILLED ${screenName}'s minecraft server PID.";
done


