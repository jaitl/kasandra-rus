from sklearn.cluster import KMeans
from collections import Counter
from sklearn.feature_extraction.text import TfidfVectorizer

import logging
import time
import sys

from scripts.clustering.news import News
from scripts.clustering.util import *

start_time = time.time()

# Files
dataset = '/data/kasandra/year/all.normalized.json'
result_base = '/data/kasandra/year/result'

n_clusters = 300
year = 2016

log_path = '/data/logs/%s.kmeans.ngram.logs' % year

# Первая неделя марта
(mart_start, mart_end) = (1425157200000, 1427835600000)

root = logging.getLogger()
root.setLevel(logging.DEBUG)

ch = logging.StreamHandler(sys.stdout)
ch.setLevel(logging.DEBUG)
formatter = logging.Formatter('[%(asctime)s] %(levelname)-5s %(message)s')
ch.setFormatter(formatter)
root.addHandler(ch)

fh = logging.FileHandler(log_path)
fh.setLevel(logging.DEBUG)
fh.setFormatter(formatter)
root.addHandler(fh)

logging.info("log file name: %s" % log_path)

logging.info("Start load news...")

news = []
with open(dataset, encoding="utf8") as f:
    for line in f:
        news.append(News.from_json(line))

words = []
for n in news:
    words.extend(n.content.split())

logging.info("Start couting news...")

counts = Counter(words)
one_time = [k for k, v in dict(counts).items() if v == 1]
logging.info("total words in dataset: %s" % (len(words) - len(one_time)))

one_time_words = set(one_time)

mart_news = list(filter(lambda x: x.date > mart_start and x.date < mart_end, news))
mart_content = [filter_words(x.content) for x in mart_news]
logging.info(
    "count news range: %s, from: %s, to: %s" % (len(mart_content), millis_to_str(mart_start), millis_to_str(mart_end)))

start_vectorize = time.time()
logging.info("Start vectorization...")

tfidf_vectorizer = TfidfVectorizer(use_idf=True, tokenizer=lambda text: text.split(" "), stop_words=one_time_words,
                                   max_df=0.5, min_df=3, norm='l2', ngram_range=(1, 3))

tfidf_matrix = tfidf_vectorizer.fit_transform(mart_content)

logging.info("vocabulary size: %s, vectorize time: %s s" % (tfidf_matrix.shape[1], time.time() - start_vectorize))

result_path = result_base + '/%s.%s.kmeans.ngram.json' % (year, n_clusters)

start_clustering = time.time()
logging.info("Start clustering for n: %s..." % n_clusters)

km = KMeans(n_clusters=n_clusters, n_jobs=-1).fit(tfidf_matrix)
labels = km.labels_

logging.info("clustering time: %s s" % (time.time() - start_clustering))
logging.info("Start save result...")
save_clusters(mart_news, labels, result_path)

logging.info("End script, total time: %s s" % (time.time() - start_time))
