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
   
    #sshfs $id@$ip:$hostBaseDir $mountDir;
    sshfs -o password_stdin $id@$ip:$hostBaseDir $mountDir <<< "${hostPassword}"
    echo "$id@$ip:$hostBaseDir $mountDir";
    mountedDirList["$hostname"]="$mountDir";
    echo $separationPhrase

done

#=====================
#
#close server
#
#=====================

echo "Closing Server Process Start......"

for hostName in ${hostIPArr[@]};
do
	
	for screenName in $(sshpass -p ${hostPasswordArr["$hostname"]} ssh ${hostIDArr["$hostname"]}@${hostIPArr["$hostname"]} ps -ef | grep -v -E 'grep|java|bash' | grep -E -o "\[([^\[]*)\]" | grep -E "*-minecraft");
	do
		echo "killing ${screenName}'s minecraft server...."
		sshpass -p ${hostPasswordArr["$hostname"]} ssh ${hostIDArr["$hostname"]}@${hostIPArr["$hostname"]} screen -S $screenName -X stuff \"^M^Mstop^M\";
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
	ram=$(removeDoublequotes $(echo $settingsJson | jq ".servers.\"${servername}\".ram "));

	echo "hostname = ${hostname}"
	echo "ram = ${ram}"

	#SET COPY TARGET DIRECTORY 
	copyTargetDir="${mountedDirList["$hostname"]}/$servername";

	mkdir -p $copyTargetDir;

	cp -r -f $currentDir/resource $copyTargetDir;


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





