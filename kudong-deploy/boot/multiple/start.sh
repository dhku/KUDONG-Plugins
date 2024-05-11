#!/bin/bash

tmuxName="msq";
tmuxLayoutType="tiled";
tmuxConfigDir="/home/ubuntu/.tmuxinator/";

tmuxFileDir=$tmuxConfigDir;
tmuxFileDir+=$tmuxName;
tmuxFileDir+=".yml";

lobby_name="lobby"
server1_name="server1"
proxy_name="proxy"

ram=8G
wait_time=3

echo == START SERVER PROCESS ==

#tmuxinator work
#delete previous tmux setting file
if [ -f "${tmuxFileDir}" ];
then
	echo "deleting previous tmux setting file ${tmuxConfigDir}";
	
	rm -rf ${tmuxFileDir};
fi

mkdir -p ${tmuxConfigDir}
touch  ${tmuxFileDir}

echo "# ${tmuxFileDir}" 						>> $tmuxFileDir;
echo "name: ${tmuxName}" 						>> $tmuxFileDir;
echo "root: ~/" 							>> $tmuxFileDir;
echo "windows:"								>> $tmuxFileDir;
echo "  - editor:"							>> $tmuxFileDir;
echo "      layout: ${tmuxLayoutType}"					>> $tmuxFileDir;
echo "      panes:"							>> $tmuxFileDir;

chmod 777 ${tmuxConfigDir};

for sec in $(eval echo {1..$wait_time})
do
	echo Wait $(expr ${wait_time} + 1 - ${sec}) Seconds .....
	sleep 1;
done

echo "STARTING SERVER => [${lobby_name}-minecraft]"

cd ./${lobby_name}
screen -dmS [${lobby_name}-minecraft] java -Duser.timezone=KST -Du=${lobby_name}-minecraft -Xms$ram -Xmx$ram -server -jar paper.jar -nogui
cd ..

echo "        -  printf '\033]2;%s\033\\' '${lobby_name}'; screen -r [${lobby_name}-minecraft]"	>> $tmuxFileDir;

echo "STARTING SERVER => [${server1_name}-minecraft]"

cd ./${server1_name}
screen -dmS [${server1_name}-minecraft] java -Duser.timezone=KST -Du=${server1_name}-minecraft -Xms$ram -Xmx$ram -server -jar paper.jar -nogui
cd ..

echo "        -  printf '\033]2;%s\033\\' '${server1_name}'; screen -r [${server1_name}-minecraft]"	>> $tmuxFileDir;

echo "STARTING SERVER => [${proxy_name}-proxy]"

cd ./${proxy_name}
screen -dmS [${proxy_name}-proxy] java -Duser.timezone=KST -Du=${proxy_name}-proxy -Xms2G -Xmx2G -server -jar BungeeCord.jar -nogui
cd ..

echo "        -  printf '\033]2;%s\033\\' '${proxy_name}'; screen -r [${proxy_name}-proxy]"	>> $tmuxFileDir;

#start tmux screen
tmux kill-session -t $tmuxName;

if ! screen -ls | grep -q "${tmuxName}"; 
then
	screen -dmS $tmuxName;
	screen -S $tmuxName -X multiuser on;
	screen -S $tmuxName -X acladd authorized_user;
fi

screen -S $tmuxName -X stuff "tmuxinator start ${tmuxName}^M";

screen -x msq;
