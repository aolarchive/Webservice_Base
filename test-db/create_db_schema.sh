#!/bin/sh

. ./db_vars.sh

for file in `ls tables/*.sql`
do
  echo loading: $file
  $MYSQL_CMD --user=$USER --password=$PASSWORD -h $MYSQL_HOST -P $MYSQL_PORT < $file
done

for file in `ls stp/*.sql`
do
  echo loading: $file
  $MYSQL_CMD --user=$USER --password=$PASSWORD -h $MYSQL_HOST -P $MYSQL_PORT < $file
done

for file in `ls import/*.sql`
do
  echo loading: $file
  $MYSQL_CMD --user=$USER --password=$PASSWORD -h $MYSQL_HOST -P $MYSQL_PORT < $file
done

