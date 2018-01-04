#!/bin/bash
export PATH=/root/anaconda3/bin:$PATH
cd /data/kasandra-rus/kasandra_viewer
nohup python run.py &
