#! /bin/sh/

Xvfb :0 -ac -screen 0 1024x768x24 &

#2>&1 >/dev/null & export DISPLAY=:1*
