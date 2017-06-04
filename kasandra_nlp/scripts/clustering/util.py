import json
from itertools import groupby

import datetime


def zip_news(n, l):
    return list(map(assign_label_to_news, zip(n, l)))


def assign_label_to_news(tuplezz):
    (nws, lbl) = tuplezz
    nws.label = lbl.item()
    return nws


def filter_words(text):
    words_list = text.split()
    newWords = [x for x in words_list if len(x) > 3]
    return " ".join(newWords)


def print_clusters(cluster_news, clustre_labels):
    newsLabels = zip_news(cluster_news, clustre_labels)
    newsLabels = sorted(newsLabels, key=lambda n: n.label)
    for label, group in groupby(newsLabels, lambda n: n.label):
        groupList = list(group)
        print("Cluster: %s, count news: %s, titles:" % (label, len(groupList)))
        for gr in groupList:
            print("\t" + gr.title)


def print_topics(components, feature_names, n_top_words):
    for topic_idx, topic in enumerate(components):
        print("Topic #%d:" % topic_idx)
        print(" ".join([feature_names[i] for i in topic.argsort()[:-n_top_words - 1:-1]]))


def millis_to_str(time):
    date_template = "%d.%m.%Y %H:%M:%S"
    date = datetime.datetime.fromtimestamp(time / 1000)
    return date.strftime(date_template)

def save_clusters(news, labels, file):
    news_clusters = {}
    for i in range(0, len(labels)):
        n = news[i]
        l = labels[i]
        news_clusters[n.id] = int(l)

    with open(file, encoding="utf8", mode="w") as f:
        d_json = json.dumps(news_clusters, ensure_ascii=False)
        f.write(d_json + '\n')