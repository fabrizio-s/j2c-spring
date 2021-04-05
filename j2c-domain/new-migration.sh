#!/bin/bash

# creates new empty migration file with timestamp.
#
# example usage: "./new-migration.sh src/main/db/postgresql/migrations user_table"
#            -> src/main/db/postgresql/migrations/V1.123123123__user_table.sql

# args
basedir=$1
if [ -z $basedir ]; then
  echo "Directory containing migrations is required"
  exit 1
elif [ ! -d $basedir ]; then
  echo "Invalid directory '$basedir'"
  exit 1
fi
description=$2 # description of the migration to be created.

# vars
separator="."
latest_version=$(find $basedir -maxdepth 1 -type f -printf "%f\n" | cut -d$separator -f1 | sed -r 's/[^0-9]//g' | sort -rn | head -1)

# generate filename for new migration from version and description.
new_filename () {
  local version=${1:-1} # defaults to 1 if empty.
  local description=${2:-"new_migration"} # defaults to "new_migration" if empty.

  echo V$version$separator"$(date +%s%3N)"__$description.sql
}

# check if $latest_version is empty because no migrations exist in $basedir.
if [ -z $latest_version ]; then
  # create first migration.
  touch ${basedir}/$(new_filename 1 $description);
else
  new_version=$(($latest_version + 1));
  touch ${basedir}/$(new_filename $new_version $description)
fi
