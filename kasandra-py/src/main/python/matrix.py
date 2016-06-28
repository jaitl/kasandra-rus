import numpy as np
import pandas as pd
import json
import stemming
from sklearn.feature_extraction.text import TfidfVectorizer


with open('data/news.json', encoding="utf8") as f:
    news = json.load(f)

texts = list(map(lambda x: x['title'], news['news']))

#define vectorizer parameters
tfidf_vectorizer = TfidfVectorizer(max_df=1, max_features=200000,
                                   min_df=0, use_idf=True,
                                   tokenizer=stemming.tokenize_and_stem)

tfidf_matrix = tfidf_vectorizer.fit_transform(texts)


def main():
    idf = tfidf_vectorizer.idf_
    print (dict(zip(tfidf_vectorizer.get_feature_names(), idf)))
    print (tfidf_vectorizer.get_feature_names())
    print (tfidf_matrix.todense())

if __name__ == "__main__":
    main()