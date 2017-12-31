import json
import os

import app.config as conf


def load_news():
    path = conf.path_to_corpus()
    data = {}

    filenames = os.listdir(path)

    for name in filenames:
        with open(os.path.join(path, name), mode='r', encoding='utf8') as f:
            d = json.load(f)
            data[d['alias']] = d

    return data
