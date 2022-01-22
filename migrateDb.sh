#!/usr/bin/env bash

#needsUpgrade=$(head -n 1 munindb.mv.db | grep 'version:4493')
echo "Backing up munindb"
curl https://repo1.maven.org/maven2/com/h2database/h2/1.4.200/h2-1.4.200.jar -o h2-1.4.200.jar
java -cp h2-1.4.200.jar org.h2.tools.Script -url jdbc:h2:file:./munindb -user sa -script munindbbackup.zip -options compression zip
echo "Renaming old munindb"
mv munindb.mv.db oldmunindb.mv.db
if [[ -f munindb.trace.db ]]; then
  mv munindb.trace.db oldmunindb.trace.db
fi
echo "Restoring data to new h2 db"
curl https://repo1.maven.org/maven2/com/h2database/h2/2.1.210/h2-2.1.210.jar -o h2-2.1.210.jar
java -cp h2-2.1.210.jar org.h2.tools.RunScript -url jdbc:h2:file:./munindb -user sa -script munindbbackup.zip -options compression zip
echo "Done!"
