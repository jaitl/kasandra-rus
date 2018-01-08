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
