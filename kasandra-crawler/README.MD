# Kasandra crawler
## Run
```bash
./gradlew run

```
## Build jar
```bash
./gradlew clean shadowJar

```
## Run jar
```bash
java -jar build/libs/kasandra-crawler-1.0-all.jar

```
## ENV
* `LOG_PATH` - path to logs

## Systemd install
1. Copy `scripts/kasCrawler.service` to `/usr/lib/systemd/system/`
2. Copy `runCrawler.sh` to `/data/kasandra`
3. Run
```bash
systemctl daemon-reload 
systemctl start kasCrawler.service 
systemctl enable kasCrawler.service
systemctl status kasCrawler.service

```
