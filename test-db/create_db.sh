#!/bin/sh

. ./db_vars.sh
# must be root here
USER=root
PASSWORD=netscape

for file in `ls create_db.sql`
do
  echo loading: $file
  $MYSQL_CMD --user=$USER --password=$PASSWORD -h $MYSQL_HOST -P $MYSQL_PORT < $file
done

