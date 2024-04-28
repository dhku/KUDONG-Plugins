#!/bin/bash

servername="minesquare"

for screenName in $(ps -ef | grep -E 'grep|java|bash' | grep -E -o "\[([^\[]*)\]" | grep -E "*-minecraft")
do
	echo "Killing ${screenName} SERVER ....";
	screen -S $screenName -X stuff "^M^Mbroadcast 잠시후 서버가 종료 됩니다...^M"
	sleep 5;
	screen -S $screenName -X stuff "^M^Mstop^M"
	echo "${screenName}'s minecraft server closed.";
done
