#!/usr/bin/env bash

fromVersion=2.1.214
toVersion=2.2.220
DBNAME="munindb"

if head -n 1 ${DBNAME}.mv.db | grep -q 'format:1'; then
  echo "${DBNAME} is in version 1 format, run migrateDb.sh instead"
  exit
fi

echo "This will upgrade the ./${DBNAME} from version ${fromVersion} to ${toVersion}"
read -p "Press enter to continue!" -n 1 -r

pluginVer=3.5.0
mvn org.apache.maven.plugins:maven-dependency-plugin:${pluginVer}:get -Dartifact=com.h2database:h2:${fromVersion}:jar
mvn org.apache.maven.plugins:maven-dependency-plugin:${pluginVer}:get -Dartifact=com.h2database:h2:${toVersion}:jar

localRepository=$(mvn help:evaluate -Dexpression=settings.localRepository|grep -v "[INFO]" | grep -v "Download")
echo "localRepository=${localRepository}"

fromJar="${localRepository}/com/h2database/h2/${fromVersion}/h2-${fromVersion}.jar"
toJar="${localRepository}/com/h2database/h2/${toVersion}/h2-${toVersion}.jar"

if [[ -f "${fromJar}" ]]; then
  echo "Exporting the content"
  java -cp "${fromJar}" org.h2.tools.Script -url jdbc:h2:file:./${DBNAME} -user sa -script ${DBNAME}backup.zip -options compression zip || exit
else
  echo "Failed to find h2 jar in ${fromJar}"
  exit 1
fi

echo "Move existing db to old${DBNAME}"
mv -f ${DBNAME}.mv.db old${DBNAME}.mv.db
if [[ -f ${DBNAME}.trace.db ]]; then
  mv -f ${DBNAME}.trace.db old${DBNAME}.trace.db
fi

if [[ -f "${toJar}" ]]; then
  echo "Restoring data to new h2 db"
  java -cp "${toJar}" org.h2.tools.RunScript -url jdbc:h2:file:./${DBNAME} -user sa -script ${DBNAME}backup.zip -options compression zip || exit
else
  echo "Failed to find h2 jar in ${toJar}"
  exit 1
fi
echo "Done!"