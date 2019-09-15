#!/bin/bash

echo "======================================================================"

echo "installing java 8 ..."

sudo apt install openjdk-8-jdk-headless -y

install_python3(){

echo "installing python3 ..."

sudo add-apt-repository ppa:jonathonf/python-3.6
sudo apt-get update
sudo apt-get install python3.6

echo "done installing python3 ..."
}


install_pip3(){

echo "installing pip3 ..."

sudo apt install python3-pip

echo "updating pip3 ..."
sudo -H pip3 install --upgrade pip

echo "done setuping pip3 ..."
}

install_dependency(){
echo "installing python dependency ..."

# sudo -H pip3 install -r requirements.txt
sudo -H pip3 install -r requirements.txt

echo "done installing python dependency ..."
}

echo "==================================================================================";

if [ $(which python3) ]; then
	if [ $(which pip3) ]; then
		echo "" > /dev/null
	else
		install_pip3
	fi

else
	install_python3
	install_pip3
fi


install_dependency


python3 setup_cron.py

echo -e "All Set,check \e[33mcrontab -l \e[0m"

echo "hopefully nothing is broken :p"



