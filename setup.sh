#!/bin/bash

install_java8(){

echo "installing java 8 ..."

sudo apt install openjdk-8-jdk-headless -y

echo "done installing java 8 ..."

}

install_python3(){

echo "installing python3 ..."

sudo add-apt-repository ppa:jonathonf/python-3.6
sudo apt-get update
sudo apt-get install python3.6

echo "done installing python3 ..."
}


install_pip3(){

echo "installing pip3 ..."

sudo apt install python3-pip -y

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



if [ $(java -version 2>&1 | head -1 | cut -d'"' -f2 | sed '/^1\./s///' | cut -d'.' -f1) != "8" ];
then
	echo "======================================================================"
	echo "Java 8 not found on system(I may be wrong :p)"
	install_java8
	echo "==================================================================================";
fi


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

echo "setting system timezone to Asia/Kolkata .."
sudo timedatectl set-timezone Asia/Kolkata
echo "restarting cron service .."
sudo service cron restart

python3 setup_cron.py

echo -e "All Set,check \e[33mcrontab -l \e[0m"

echo "hopefully nothing is broken :p"



