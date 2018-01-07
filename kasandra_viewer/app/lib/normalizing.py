from pymystem3 import Mystem
import re
import os

from app.config import path_to_res, path_to_mystem

mystem_part = {
    'A': 'ADJ',  # прилагательное
    'ADV': 'ADV',  # наречие
    'ADVPRO': 'ADV',  # местоименное наречие
    'ANUM': 'ADJ',  # числительное-прилагательное
    'APRO': 'DET',  # местоимение-прилагательное
    'COM': 'ADJ',  # часть композита - сложного слова
    'CONJ': 'SCONJ',  # союз
    'INTJ': 'INTJ',  # междометие
    'NUM': 'NUM',  # числительное
    'PART': 'PART',  # частица
    'PR': 'ADP',  # предлог
    'S': 'NOUN',  # существительное
    'SPRO': 'PRON',
    'V': 'VERB',  # глагол
}


def load_stopwords():
    res_dir = path_to_res()
    stopwords = set()

    with open(os.path.join(res_dir, 'stopwords.txt'), mode="r", encoding="utf8") as file:
        for line in file:
            stopwords.add(line.replace('\n', ''))

    return stopwords


def mystem_analisys(mystem, text):
    try:
        res = mystem.analyze(text)
        stem_res = []
        for r in res:
            analysis = r['analysis']
            try:
                gr = analysis[0]
                lex = analysis[0]['lex']
                parts = gr['gr'].split("=")
                parts2 = parts[0].split(",")
                part = parts2[0]
                lex_pas = "%s_%s" % (lex, mystem_part[part])
                stem_res.append((lex, lex_pas))
            except Exception:
                stem_res.append((r['text'], r['text']))
        return stem_res
    except Exception:
        return list(map(lambda x: (x, x), mystem.lemmatize(text)))

    return stem_res


def nomalize(texts, with_pos=False):
    stopwords = load_stopwords()
    r = re.compile('^[А-ЯЙа-яй]*$')
    mystem = Mystem(entire_input=False, mystem_bin=path_to_mystem())
    tokens_corpuse = []
    for text in texts:
        words = filter(lambda x: r.match(x[0]), mystem_analisys(mystem, text))
        tokens = filter(lambda w: w[0] not in stopwords, words)

        tokens_res = []
        if with_pos:
            tokens_res = list(map(lambda x: x[1], tokens))
        else:
            tokens_res = list(map(lambda x: x[0], tokens))

        tokens_corpuse.append(tokens_res)
    return tokens_corpuse
