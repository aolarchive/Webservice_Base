#!/bin/bash

usage() 
{
  echo "Usage: $command [-classpath classpath] /path/to/config.xml"
  echo "Note: You should include path to your derrived jar in classpath"
  exit 1
}

command="$0"

classpath=""
if [ "$1" == "-classpath" ]
then
   shift 
   if [ $# -eq 0 ]
   then
      echo "Error: You must specify a classpath";
      usage
   fi

   classpath="$1"
   shift
fi

if [ $# -eq 0 ]
then
  echo "Error: You must specify a configuration file"
  usage
fi
if [ $# -ne 1 ]
then
  echo "Error: You must specify only one file as configuration file"
  usage
fi

#change to lib directory to simplify classpath :)
cd ../../lib

java -classpath java_memcached-release_2.0.1.jar:json.jar:commons-beanutils-bean-collections.jar:json-lib-2.2.1-jdk15.jar:commons-beanutils-core.jar:junit-4.5.jar:commons-beanutils.jar:log4j-1.2.15.jar:commons-codec-1.3.jar:mysql-connector-java-5.1.8-bin.jar:commons-collections-3.2.1.jar:servlet-api.jar:commons-httpclient-3.0.jar:commons-lang-2.4.jar:webservice_base.jar:commons-logging-1.1.1.jar:xstream-1.3.jar:ezmorph-1.0.4.jar:$classpath ValidateConfig $1 2>&1

cd -
