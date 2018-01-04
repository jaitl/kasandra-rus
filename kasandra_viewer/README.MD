# Kasandra viewer
Demonstration of the master's thesis

## Dependencies
```
pip install flask
pip install pymystem3
pip install sklearn
pip install gensim
```

## Run local
```
python run.py
```

## Run server
1. Clone this repo to `/data/kasandra-rus`
2. Run `chmod 777 /data/kasandra-rus/kasandra_viewer/scripts/runKasandraApp.sh`
3. Copy `scripts/kasApp.service` to `/etc/systemd/system/`
4. Install Anaconda
5. Run
```
systemctl daemon-reload
systemctl enable kasApp.service
systemctl start kasApp.service 
systemctl status kasApp.service
```
