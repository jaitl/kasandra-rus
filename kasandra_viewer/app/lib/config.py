import json
import os
from pathlib import Path


def work_dir():
    return str(Path().resolve())


def config():
    with open(os.path.join(work_dir(), 'config.json'), mode='r', encoding='utf8') as f:
        return json.load(f)


def path_to_corpus():
    return os.path.join(work_dir(), "news_corpus")
