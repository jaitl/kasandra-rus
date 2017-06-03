import time

from sklearn.externals import joblib
from sklearn.feature_extraction.text import TfidfVectorizer

from scripts.normalizing.news import News

normalized_path = '/data/kasandra/year/lenta.rbc.ria.vesti.2016.normalized.json'
tfidf_matrix_path = '/data/kasandra/year/lenta.rbc.ria.vesti.2016.pkl'

news = []

print("load news")
with open(normalized_path, encoding="utf8") as f:
    for line in f:
        news.append(News.from_json(line))


def lemmatize(text):
    return text.split(" ")

print("start vectorization")
start_time = time.time()

tfidf_vectorizer = TfidfVectorizer(use_idf=True,
                                   tokenizer=lemmatize,
                                   ngram_range=(1, 3))

tfidf_matrix = tfidf_vectorizer.fit_transform(map(lambda n: n.content, news))

print("vectorization time: %s seconds" % (time.time() - start_time))

joblib.dump(tfidf_vectorizer, tfidf_matrix_path)

print("end vectorization news")
