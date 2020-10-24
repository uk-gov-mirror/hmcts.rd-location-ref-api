#!/usr/bin/env sh

set -e

if [ -z "$POSTGRES_PASSWORD" ]; then
  echo "ERROR: Missing environment variables. Set value for '$POSTGRES_PASSWORD'."
  exit 1
fi

echo "Creating dbrdlocationref database . . ."

psql -v ON_ERROR_STOP=1 --username postgres --dbname postgres <<-EOSQL
  CREATE ROLE dbrdlocationref WITH PASSWORD 'dbrdlocationref';
  CREATE DATABASE dbrdlocationref ENCODING = 'UTF-8' CONNECTION LIMIT = -1;
  GRANT ALL PRIVILEGES ON DATABASE dbrdlocationref TO dbrdlocationref;
  ALTER ROLE dbrdlocationref WITH LOGIN;
EOSQL

echo "Done creating database dbrdlocationref."
