#!/bin/bash

property_file=src/main/resources/application.properties

echo "logging.level.root=error" > $property_file

echo -e "spring.main.banner-mode=off\n" >> $property_file

cat cookies.txt >> property_file