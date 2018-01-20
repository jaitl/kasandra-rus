
import json
import os

import app.config as conf


def load_news():
    path = conf.path_to_corpus()
    data = {}

    filenames = os.listdir(path)

    print("load news...")

    for name in filenames:
        if not name.endswith(".json") or name.startswith("."):
            continue

        file_path = os.path.join(path, name)
        print("load file: %s" % file_path)

        with open(file_path, mode='r', encoding='utf8') as f:
            d = json.load(f)
            data[d['alias']] = d

    return data


news = load_news()


def get_corpuses_name_len():
    name_count = [(x['name'], len(x['news'])) for x in news.values()]
    return name_count


def get_corpuses_list():
    titles = map(lambda x: {"name": x['name'], "alias": x["alias"]}, news.values())
    return titles


def get_news_data(news_id):
    return news[news_id]['news']


def get_news_titles(news_id):
    news_data = news[news_id]['news']
    news_titles = list(map(lambda x: x['title'], news_data))

    return news_titles


def get_news_content(news_id):
    content = list(map(lambda x: x['content'], news[news_id]['news']))
    
    return content
