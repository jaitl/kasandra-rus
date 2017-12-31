from app.lib.normalizing import nomalize
from app.lib.vectorization import tf_idf, sem_groups


def do_vectorization(algoritm, news):
    news_content = list(map(lambda x: x['content'], news))
    norm_texts = nomalize(news_content)

    if algoritm == "tf_idf":
        vect_res = tf_idf(norm_texts)
    elif algoritm == "sem_group_tf_idf":
        vect_res = sem_groups(norm_texts)
    else:
        raise Exception("unknown vectorization algoritm: %s" % algoritm)

    vect_list = [[y.tolist() for y in x.toarray()] for x in vect_res]
    return vect_list
