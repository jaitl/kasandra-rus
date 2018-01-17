from gensim.models.keyedvectors import KeyedVectors
from gensim.models import Word2Vec
from pymystem3 import Mystem

from app.config import path_to_word_to_vec_model


def init_word3vec():
    model_path = path_to_word_to_vec_model()
    word_vectors = KeyedVectors.load_word2vec_format(model_path, binary=True)
    word_vectors.init_sims(replace=True)
    return word_vectors

# инициализация занимает около 10 секунд, это слишком долго, что ты инициализировать каждый раз
# по этому инициализируется при запуске приложения
word_vectors = init_word3vec()


def similar(word):
    try:
        sim = word_vectors.most_similar(positive=[word])
        return list(map(lambda x: x[0], sim))
    except Exception as e:
        return [word]


def extractSemanticGroup(tokens_matrix, with_pos=False):
    res_seq = []
    sem_groups = {}

    for words in tokens_matrix:
        res_tokens = []
        for token in words:
            if token not in sem_groups:
                sims = similar(token)
                sem_groups[token] = token
                res_tokens.append(token)
                for sim in sims:
                    sem_groups[sim] = token
            else:
                res_tokens.append(sem_groups[token])
        res_seq.append(res_tokens)
    if not with_pos:
        res_seq = map(lambda x: list(map(lambda y: y.split("_")[0], x)), res_seq)

    return list(res_seq)
