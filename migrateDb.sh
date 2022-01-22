#!/usr/bin/env bash

# This script attempts to migrate the h2 database from v 1.4.x to 2.1.x
# The old database is renamed with the prefix old

DBNAME="munindb"

if head -n 1 ${DBNAME}.mv.db | grep -q 'format:2'; then
  echo "${DBNAME} already seems to be in the latest format"
  exit
fi

echo "Backing up ${DBNAME}"
curl -s https://repo1.maven.org/maven2/com/h2database/h2/1.4.200/h2-1.4.200.jar -o h2-1.4.200.jar
java -cp h2-1.4.200.jar org.h2.tools.Script -url jdbc:h2:file:./${DBNAME} -user sa -script ${DBNAME}backup.zip -options compression zip
echo "Renaming old ${DBNAME}"
mv ${DBNAME}.mv.db old${DBNAME}.mv.db
if [[ -f ${DBNAME}.trace.db ]]; then
  mv ${DBNAME}.trace.db old${DBNAME}.trace.db
fi
echo "Restoring data to new h2 db"
curl -s https://repo1.maven.org/maven2/com/h2database/h2/2.1.210/h2-2.1.210.jar -o h2-2.1.210.jar
java -cp h2-2.1.210.jar org.h2.tools.RunScript -url jdbc:h2:file:./${DBNAME} -user sa -script ${DBNAME}backup.zip -options compression zip FROM_1X
echo "cleaning up"
rm h2-1.4.200.jar
rm h2-2.1.210.jar
echo "Done!"
