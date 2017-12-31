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


def path_to_res():
    return os.path.join(work_dir(), "res")

def path_to_word_to_vec_model():
    '''
        Модель обученная на русских новостях с сайта
        http://rusvectores.org/ru/models/
    '''
    return config()['word2vec_model']
