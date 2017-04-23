import json
import re
import multiprocessing
import time

from pymystem3 import Mystem


class News:
    def __init__(self, id, date, title, content, url, siteType):
        self.id = id
        self.date = date
        self.title = title
        self.content = content
        self.url = url
        self.siteType = siteType

    @classmethod
    def from_json(cls, json_str):
        json_dict = json.loads(json_str)
        return cls(**json_dict)


stopwords = set()

with open('../res/stopwords.txt', mode="r", encoding="utf8") as file:
    for line in file:
        stopwords.add(line.replace('\n', ''))

r = re.compile('^[А-ЯЙа-яй]*$')
mystem = Mystem(entire_input=False)


def normalize(new):
    words = filter(r.match, mystem.lemmatize(new.content))
    not_stop_words = filter(lambda w: w not in stopwords, words)
    new.content = " ".join(not_stop_words)
    return new


raw_path = '/data/kasandra/year/104636.raw.json'
normalized_path = '/data/kasandra/year/104636.normalized.json'

raw_news = []
news = []

duplicates_names = set()

print("start normalization....")

print("load file")

with open(raw_path, encoding="utf8") as f:
    for line in f:
        raw_news.append(News.from_json(line))

print("clear duplicates")

for new in raw_news:
    if new.title not in duplicates_names:
        duplicates_names.add(new.title)
        news.append(new)

pool = multiprocessing.Pool(4)

print("normalize news, count: %s" % len(news))
start_time = time.time()
normalized_news = pool.map(normalize, news)
print("normalization time: %s seconds" % (time.time() - start_time))


print("sort news by date")
normalized_news.sort(key=lambda new: new.date)

print("save normalized news")
with open(normalized_path, encoding="utf8", mode="w") as f:
    for new in normalized_news:
        d_json = json.dumps(new.__dict__, separators=(',', ':'), ensure_ascii=False)
        f.write(d_json + '\n')

print("end normalization, unique news: %s" % len(duplicates_names))
