#!/bin/bash

if [ "$*" == "" ]; then
	echo "enter some commit message"
	exit -1
fi


./cookie_remove.sh

git add --all

git commit -m "$*"

git push -u origin master
