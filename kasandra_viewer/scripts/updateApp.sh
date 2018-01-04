#!/bin/bash
systemctl stop kasApp.service
cd /data/kasandra-rus
git pull
systemctl start kasApp.service
