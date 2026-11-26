#!/usr/bin/env bash
# Legacy 2016 manual WAR deployment script for Cymbal HK Ops VM
# WARNING: Causes ~30 seconds of downtime during Tomcat restart and drops active HttpSessions.
set -euo pipefail

APP_DIR="$(cd "$(dirname "$0")/../legacy-app" && pwd)"
VM_HOST="${VM_HOST:-ops-vm-hk-01.cymbalhk.internal}"
VM_USER="${VM_USER:-tomcat}"
TOMCAT_WEBAPPS="/opt/tomcat8/webapps"

echo "[1/3] Building WAR with Maven (requires JDK 8)..."
cd "$APP_DIR"
mvn clean package -DskipTests

echo "[2/3] Copying WAR to legacy VM ($VM_HOST)..."
scp target/cymbal-portal-1.0.0-SNAPSHOT.war "${VM_USER}@${VM_HOST}:${TOMCAT_WEBAPPS}/cymbal-portal.war"

echo "[3/3] Restarting Tomcat 8 via systemd..."
ssh "${VM_USER}@${VM_HOST}" "sudo systemctl restart tomcat8 && sudo systemctl status tomcat8 --no-pager"
echo "Deployment complete. Verify http://${VM_HOST}:8080/cymbal-portal/login"
