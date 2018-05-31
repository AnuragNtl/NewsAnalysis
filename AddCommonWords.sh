#!/bin/bash

function printWordLists()
{
	local lines
	local ct
	local timer
	local t
	local t1
	declare -i lines
	declare -i ct
	lines=$2
	ct=0
	timer=0
for i in $(cat $1)
	do
		t=$(/usr/bin/time -f "%e" node ./AddCommonWord.js $i 2>&1 > /dev/null)
		timer=$(echo "$timer+$t" | bc -l)
		ct=$((ct+1))
		if test $((ct%10)) -eq 0
			then
			echo "$ct words processed in $timer seconds"
			t1=$(echo "$timer/$ct" | bc -l)
			echo "ETA" $(echo "($t1*($lines-$ct))/60" | bc -l) "minutes"
		fi
	done
}

if test $# -ge 1
	then
	lines=$(wc -l $1 | cut -d " " -f 1)
	printWordLists $1 $lines
	else
	echo "Usage: AddCommonWords.sh WordListFileName"
fi
