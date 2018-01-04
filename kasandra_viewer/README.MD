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
2. Copy `scripts/kasApp.service` to `/etc/systemd/system/`
3. Run
systemctl daemon-reload 
systemctl start kasApp.service 
systemctl enable kasApp.service
systemctl status kasApp.service