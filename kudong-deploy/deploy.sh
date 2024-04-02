#!/bin/bash

currentDir=$(pwd -P);

echo "currentDir = $currentDir" 

hostJson=$(cat $currentDir/hosts.json | jq '.');
mountDir=$currentDir/mount;
buildDir=$currentDir/target;

separationPhrase="-----------------------------------";

echo $hostJson;

removeDoublequotes()
{
	temp=$1;
	echo ${temp:1:-1};
}

declare -A hostArr;
declare -A hostIPArr;
declare -A hostIDArr;
declare -A hostPasswordArr;
declare -A hostBaseArr;
declare -A mountedDirList;

echo $separationPhrase

for i in $(seq $(echo $hostJson | jq ' . | keys | length'));
do
    index=$(($i - 1));
    hostname=$(removeDoublequotes $(echo $hostJson | jq ' . | keys | .['$index']'));
    echo "hostname = $hostname"
    hostArr["$hostname"]="$hostname";
   
    query=".\"$hostname\".\"ip\"";
    ip=$(removeDoublequotes $(echo $hostJson | jq $query));
    echo "ip = $ip"

    hostIPArr["$hostname"]="$ip";

    query=".\"$hostname\".\"id\"";
    id=$(removeDoublequotes $(echo $hostJson | jq $query));
    echo "id = $id"

    hostIDArr["$hostname"]="$id";

    query=".\"$hostname\".\"baseDir\"";
    hostBaseDir=$(removeDoublequotes $(echo $hostJson | jq $query));
    echo "hostBaseDir= $hostBaseDir"

    hostBaseArr["$hostname"]="$hostBaseDir";

        query=".\"$hostname\".\"password\"";
    hostPassword=$(removeDoublequotes $(echo $hostJson | jq $query));
    echo "hostPassword= $hostPassword"

    hostPasswordArr["$hostname"]="$hostPassword";

    mountDir="${mountDir}/$hostname";
    umount $mountDir;

    if [ -d $mountDir ]; then
	echo "$mountDir is Exist! "
	rm -rf $mountDir;
    fi
    echo "Start Mount....."
    mkdir -p $mountDir;
   
    #sudo sshfs $id@$ip:$hostBaseDir $mountDir;
    echo "${hostPassword}" | sshfs $id@$ip:$hostBaseDir $mountDir -o workaround=rename -o password_stdin -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null
    #sshfs -o password_stdin $id@$ip:$hostBaseDir $mountDir <<< "${hostPassword}"
    echo "$id@$ip:$hostBaseDir $mountDir";
    mountedDirList["$hostname"]="$mountDir";
    echo $separationPhrase

done

echo =====================================
echo
echo "Closing Server Process Start......"
echo 
echo =====================================

declare -A minecraftList;

for hostname in ${hostArr[@]};
do
	
	for screenName in $(sshpass -p ${hostPasswordArr["$hostname"]} ssh ${hostIDArr["$hostname"]}@${hostIPArr["$hostname"]} ps -ef | grep -v -E 'grep|java|bash' | grep -E -o "\[([^\[]*)\]" | grep -E "*-minecraft");
	do
		echo "killing ${screenName}'s minecraft server....";
        minecraftList["$screenName"]="$hostname";

        echo "hello ${minecraftList["$screenName"]}";

		sshpass -p ${hostPasswordArr["$hostname"]} ssh ${hostIDArr["$hostname"]}@${hostIPArr["$hostname"]} screen -S $screenName -X stuff \"^M^Mstop^M\";
        #ssh ${hostIDArr["$hostname"]}@${hostIPArr["$hostname"]} screen -S $screenName -X stuff \"^M^Mstop^M\";
	done 
done

for screenName in ${!minecraftList[@]};
do
	loopCount=0;
	while true;
	do
        hostname=$(echo "${minecraftList["$screenName"]}")

		sshResult=$(sshpass -p ${hostPasswordArr["$hostname"]} ssh ${hostIDArr["$hostname"]}@${hostIPArr["$hostname"]} ps -ef | grep -E 'java.*${screenName:1:-1}' | grep -v -E 'grep|SCREEN|bash' | awk '{print $2}')
		
		pidList=(`echo ${sshResult} | tr " " "\n"`)
		
		if [ "${#pidList[@]}" == 0 ];
		then
			break;
		fi
		echo "Test3"
		loopCount=$(($loopCount + 1));

		if [ $loopCount -eq 10 ];
		then
			sshpass -p ${hostPasswordArr["$hostname"]} ssh ${hostIDArr["$hostname"]}@${hostIPArr["$hostname"]} kill $pidList;
			sshpass -p ${hostPasswordArr["$hostname"]} ssh ${hostIDArr["$hostname"]}@${hostIPArr["$hostname"]} screen -S $screenName -X stuff \"^C\"
			echo "pid ${pidList} (${screenName}) killed..";

			break;
		fi
		
		echo "waiting for ${screenName}@${minecraftList["$screenName"]}"
		echo "[${loopCount}]sleep for 3 Sec, and retry...";
		sleep 3;
		
	done
done

#=====================
#
#READ server_settings.json
#
#=====================

settingsJson=$(cat $currentDir/server_settings.json | jq '.');

for i in $(seq $(echo $hostJson | jq ' . | keys | length'));
do
	index=$(($i - 1));
    servername=$(removeDoublequotes $(echo $settingsJson| jq " .servers | keys | .["$index"]"));
	echo "${servername}"
    
	hostname=$(removeDoublequotes $(echo $settingsJson | jq ".servers.\"${servername}\".host "));

	#SET COPY TARGET DIRECTORY 
	copyTargetDir="${mountedDirList["$hostname"]}/$servername";

	mkdir -p $copyTargetDir;

	cp -r -f $currentDir/resource/* $copyTargetDir;


	for i in $(seq $(echo $settingsJson| jq ".servers.\"${servername}\".module  | keys | length"));
	do
		index=$(($i - 1));
		jarName=$(removeDoublequotes $(echo $settingsJson| jq ".servers.\"${servername}\".module | keys | .["$index"]"));
		echo "jarName = ${jarName}"

		jarDir=$(removeDoublequotes $(echo $settingsJson| jq ".servers.\"${servername}\".module.\"${jarName}\""));
		echo "jarDir= ${jarDir}"

		cp -f ../target/${jarName} $copyTargetDir${jarDir};		
	done
done

#=====================
#
#    START SERVER
#
#=====================


for i in $(seq $(echo $hostJson | jq ' . | keys | length'));
do
	index=$(($i - 1));
    	servername=$(removeDoublequotes $(echo $settingsJson| jq " .servers | keys | .["$index"]"));
	echo "${servername}"

    hostname=$(removeDoublequotes $(echo $settingsJson | jq ".servers.\"${servername}\".host "));
	ram=$(removeDoublequotes $(echo $settingsJson | jq ".servers.\"${servername}\".ram "));

	echo "hostname = ${hostname}"
	echo "ram = ${ram}"

    echo "cd ${hostBaseArr["$hostname"]}/$servername/ && screen -dmS \[${servername}-minecraft\] java -jar -Xms$ram -Xmx$ram -server paper.jar -nogui"

    sshpass -p ${hostPasswordArr["$hostname"]} ssh ${hostIDArr["$hostname"]}@${hostIPArr["$hostname"]} 'cd ${hostBaseArr["$hostname"]}/$servername/ ; screen -dmS \[${servername}-minecraft\] java -jar -Xms$ram -Xmx$ram -server paper.jar -nogui';
    sshpass -p ${hostPasswordArr["$hostname"]} ssh ${hostIDArr["$hostname"]}@${hostIPArr["$hostname"]} 'cd ${hostBaseArr["$hostname"]}/$servername/ ; touch helloworld';

    #ssh ${hostIDArr["$hostname"]}@${hostIPArr["$hostname"]} "cd ${hostBaseArr["$hostname"]}/$servername/ && screen -dmS \[${servername}-minecraft\] java -jar -Xms$ram -Xmx$ram -server paper.jar -nogui";
done

#=====================
#
#    UNMOUNT SERVER
#
#=====================

for i in ${mountedDirList[@]}; 
do 
	echo "unmounting $i";
	umount $i;
done




