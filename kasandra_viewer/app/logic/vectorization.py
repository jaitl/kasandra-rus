from app.lib.normalizing import nomalize
from app.lib.vectorization import tf_idf, sem_groups


def do_vectorization(data, news):
    news_content = list(map(lambda x: x['content'], news))

    if data['limit_words'] == True:
        word_count = data['words_count']
    else:
        word_count = None

    if data['algorithm']== "tf_idf":
        norm_texts = nomalize(news_content)
        (names, vect_res) = tf_idf(norm_texts, word_count)
    elif data['algorithm'] == "sem_group_tf_idf":
        norm_texts = nomalize(news_content, with_pos=True)
        (names, vect_res) = sem_groups(norm_texts, word_count)
    else:
        raise Exception("unknown vectorization algorithm: %s" % data['algorithm'])

    vect_list = [[round(y, 3) for y in x] for x in vect_res.toarray()]

    result = {
        "title": names,
        "matrix": vect_list,
        "rows": vect_res.shape[0],
        "columns": vect_res.shape[1]
    }
    return result
