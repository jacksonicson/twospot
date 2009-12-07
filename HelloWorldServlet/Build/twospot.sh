#!/usr/bin/env bash

usage="Usage: twospot.sh (start|stop)"

if [ $# -le 0 ]; then
	echo $usage
	exit 1
fi

startStop=$1
shift

function doStart()
{
	nohup python start.py fileserver > "fileserver.log2" 2>&1 < /dev/null &
	nohup python start.py master > "master.log2" 2>&1 < /dev/null &
	nohup python start.py frontend > "frontend.log2" 2>&1 < /dev/null &
	nohup python start.py controller > "controller.log2" 2>&1 < /dev/null &
}

function doStop()
{
	nohup python start.py kill > "kill.log2" 2>&1 < /dev/null &
}

case $startStop in
	(start)
		doStart
		;;
	(stop)
		doStop
		;;
esac
