#!/bin/sh

cd ~/prod/
PID=.karaoke.pid

if [ -e $PID ]; then
	echo 'Karaoke already running'
else
	pacat -r --latency-msec=1 -d  alsa_input.pci-0000_00_1b.0.analog-stereo | pacat -p --latency-msec=1 -d  alsa_output.pci-0000_00_1b.0.analog-stereo &
	echo $! > $PID
	chmod 666 $PID
fi

