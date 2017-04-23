import json

from scripts.news import News

news = []

normalized_path = '/data/kasandra/year/98305.normalized.json'
filtred_path = '/data/kasandra/year/lenta.rbc.ria.news.json'

print("load news")
with open(normalized_path, encoding="utf8") as f:
    for line in f:
        news.append(News.from_json(line))

siteTypes = {}

for new in news:
    if new.siteType in siteTypes:
        count = siteTypes[new.siteType]
        siteTypes[new.siteType] = count + 1
    else:
        siteTypes[new.siteType] = 1

print(siteTypes)

def site_type_filter(new):
    return new.siteType == 'LENTA' or new.siteType == 'RBC' or new.siteType == 'RIA'


print("filter news")
filtred_news = filter(site_type_filter, news)

print("save filtred news")
with open(filtred_path, encoding="utf8", mode="w") as f:
    for new in filtred_news:
        d_json = json.dumps(new.__dict__, separators=(',', ':'), ensure_ascii=False)
        f.write(d_json + '\n')
