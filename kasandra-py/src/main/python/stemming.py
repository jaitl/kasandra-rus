#! /usr/bin/env python
# -*- coding: utf-8 -*-
import sys
from pymystem3 import Mystem

# TODO попробовать NLTK 3.0
# https://tech.yandex.ru/mystem/doc/index-docpage/
# https://pypi.python.org/pypi/pymystem3/0.1.1
mystem = Mystem(entire_input=False)

def tokenize_only(text):
    res = mystem.analyze(text)
    return list(map(lambda x: x.get('text').lower(), res))

def tokenize_and_stem(text):
    return mystem.lemmatize(text)

def main(argv=sys.argv):
    if not len(argv) == 2:
        print("python3 stemming.py \"Мама мыла раму.\"")
        return

    tokens = tokenize_only(argv[1])
    stems = tokenize_and_stem(argv[1])
    print("tokens: " + ", ".join(tokens))
    print("stems: " + ", ".join(stems))
    print("full: ")
    print(mystem.analyze(argv[1]))

def main_2():
    text = "Против губернатора Кировской области Никиты Белых возбуждено уголовное дело в связи с получением им взятки в особо крупном размере"

    tokens = tokenize_only(text)
    stems = tokenize_and_stem(text)
    print("tokens: " + ", ".join(tokens))
    print("stems: " + ", ".join(stems))
    print("full: ")
    print(mystem.analyze(text))

if __name__ == "__main__":
    main_2()
