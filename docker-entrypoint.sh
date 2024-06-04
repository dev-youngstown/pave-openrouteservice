#!/usr/bin/env bash

echo "Running container as user $(whoami) with id $(id -u)"

if [[ -d /ors-core ]] || [[ -d /ors-conf ]]; then
  echo "You're mounting old paths. Remove them and migrate to the new docker setup: https://giscience.github.io/openrouteservice/installation/Running-with-Docker.html"
  echo "Exit setup due to old folders /ors-core or /ors-conf being mounted"
  sleep 5
  exit 1
fi

ors_base=${1}
catalina_base=${ors_base}/tomcat
graphs=${ors_base}/ors-core/data/graphs

echo "ORS Path: ${ors_base}"
echo "Catalina Path: ${catalina_base}"


if [ -z "${CATALINA_OPTS}" ]; then
  export CATALINA_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9001 -Dcom.sun.management.jmxremote.rmi.port=9001 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=localhost"
fi

if [ -z "${JAVA_OPTS}" ]; then
  export JAVA_OPTS="-Djava.awt.headless=true -server -XX:TargetSurvivorRatio=75 -XX:SurvivorRatio=64 -XX:MaxTenuringThreshold=3 -XX:+UseG1GC -XX:+ScavengeBeforeFullGC -XX:ParallelGCThreads=4 -Xms1g -Xmx21g"
fi

{
  echo "CATALINA_BASE=\"${catalina_base}\""
  echo "CATALINA_HOME=\"${catalina_base}\""
  echo "CATALINA_PID=\"${catalina_base}/temp/tomcat.pid\""
  echo "CATALINA_OPTS=\"${CATALINA_OPTS}\""
  echo "JAVA_OPTS=\"${JAVA_OPTS}\""
} >"${catalina_base}"/bin/setenv.sh

if [ "${BUILD_GRAPHS}" = "True" ]; then
  rm -rf "${graphs:?}"/*
fi

echo "### openrouteservice configuration ###"
# Always overwrite the example config in case another one is present
cp -f "${ors_base}/tmp/ors-config.yml" "${ors_base}/ors-conf/ors-config-example.yml"
# Check for old .json configs
JSON_FILES=$(ls -d -- "${ors_base}/ors-conf/"*.json 2>/dev/null)
if [ -n "$JSON_FILES" ]; then
    echo "Old .json config found. They're deprecated and will be replaced in ORS version 8."
    echo "Please migrate to the new .yml example."
fi
# No config found. Use the base config
if [ ! -f "${ors_base}/ors-conf/ors-config.yml" ]; then
  echo "Copy ors-config.yml"
  cp -f "${ors_base}/tmp/ors-config.yml" "${ors_base}/ors-conf/ors-config.yml"
fi

if [ ! -f "${ors_base}/ors-core/data/osm_file.pbf" ]; then
  echo "download osm_file.pbf"
  wget https://download.geofabrik.de/north-america/us-latest.osm.pbf -O ${ors_base}/ors-core/data/osm_file.pbf
fi

if [ "${BUILD_GRAPHS}" = "True" ]; then
  Using environment variables for database details
  DB_NAME=${NF_ORS_GISDB_DATABASE}
  DB_USER=${NF_ORS_GISDB_USERNAME}
  DB_PASS=${NF_ORS_GISDB_PASSWORD}
  DB_HOST=${NF_ORS_GISDB_HOST}
  DB_PORT=${NF_ORS_GISDB_PORT}
  
  # Import OSM data into PostgreSQL using environment variables. Uncomment to import new data into the gisdb
  # echo "Importing OSM data into ${DB_NAME}"
  # echo $DB_PASS | osm2pgsql -c -d $DB_NAME -U $DB_USER -W -H $DB_HOST -P $DB_PORT -C 16384 -G --hstore ${ors_base}/ors-core/data/osm_file.pbf
fi



# so docker can stop the process gracefully
exec "${catalina_base}"/bin/catalina.sh run
