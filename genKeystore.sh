#!/usr/bin/env bash

if [[ -f src/main/resources/munin.p12 ]]; then
  rm src/main/resources/munin.p12
fi

keytool -genkeypair -alias munin -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore src/main/resources/munin.p12 \
-validity 3650 -dname "CN=Munin,OU=se.alipsa,O=Alipsa,ST=Sweden,C=SE" -ext "SAN:c=DNS:localhost,IP:127.0.0.1"
