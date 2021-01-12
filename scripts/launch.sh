#!/bin/bash
if test $# -ne 2  ; then
    echo 'Usage $0 <app_name> <schema>'
    exit 0
fi
cd /app

echo "Appending env var translated from dot to underscore"
eval $(awk -f ./addEnvForDotWithUnderscore.awk)

if [ -z "${spring_datasource_database}" ]; then
    export spring_datasource_database=$POSTGRES_DB
fi

echo "POSTGRES_HOST= '$POSTGRES_HOST', database = '$spring_datasource_database', schema = '$2'"
echo "application = '$APPLICATION', app_name = '$1'"

# Need to build the spring.datasource.url as database and schema name may be set differently in the Env Var settings.
URL="jdbc:postgresql://$POSTGRES_HOST/$spring_datasource_database?currentSchema=$2"

VERSION=$(cat ./pom.xml | grep -m 1 '<version>' | awk -F"[><]" '{print $3}')
echo "Version: '$VERSION'"

echo "executing java"
echo "java -Dspring.datasource.url=$URL -jar $1-$VERSION.jar"
exec  java -Dspring.datasource.url=$URL -jar $1-$VERSION.jar
