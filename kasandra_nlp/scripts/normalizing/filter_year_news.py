import json

from scripts.normalizing.news import News

news = []

raw_path = '/data/kasandra/year/raw.json'
filtred_path = '/data/kasandra/year/2016.raw.json'

print("load news")
with open(raw_path, encoding="utf8") as f:
    for line in f:
        news.append(News.from_json(line))
"""
# 2015
start_year_millis = 1420059600*1000
end_year_millis = 1451595599*1000
"""

# 2016
start_year_millis = 1451595600 * 1000
end_year_millis = 1483217999 * 1000

year_news = []

print("filter news")
for new in news:
    if new.date >= start_year_millis and new.date <= end_year_millis:
        year_news.append(new)

print("save filtred news")
with open(filtred_path, encoding="utf8", mode="w") as f:
    for new in year_news:
        d_json = json.dumps(new.__dict__, separators=(',', ':'), ensure_ascii=False)
        f.write(d_json + '\n')
