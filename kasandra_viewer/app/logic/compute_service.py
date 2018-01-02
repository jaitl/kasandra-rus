from app.lib.normalizing import nomalize
from app.lib.vectorization import tf_idf, sem_groups
from app.lib.clustering import kmeans, affinity_propagation

from sklearn import metrics


def compute_matrix(data, news):
    news_content = list(map(lambda x: x['content'], news))

    if data['limit_words'] == True:
        word_count = data['words_count']
    else:
        word_count = None

    if data['vect_algorithm'] == "tf_idf":
        norm_texts = nomalize(news_content)
        (names, vect_res) = tf_idf(norm_texts, word_count)
    elif data['vect_algorithm'] == "sem_group_tf_idf":
        norm_texts = nomalize(news_content, with_pos=True)
        (names, vect_res) = sem_groups(norm_texts, word_count)
    else:
        raise Exception("unknown vectorization algorithm: %s" %
                        data['vect_algorithm'])

    return (names, vect_res)


def compute_clusters(data, matrix):
    if data['cluster_algorithm'] == 'kmeans':
        cluster_count = data['cluster_count']
        labels = kmeans(matrix, cluster_count)
    elif data['cluster_algorithm'] == 'aff_prop':
        labels = affinity_propagation(matrix)
    else:
        raise Exception("unknown clustering algorithm: %s" %
                        data['cluster_algorithm'])

    return labels


def do_vectorization(data, news):
    (names, vect_res) = compute_matrix(data, news)

    vect_list = [[round(y, 3) for y in x] for x in vect_res.toarray()]

    result = {
        "title": names,
        "matrix": vect_list,
        "rows": vect_res.shape[0],
        "columns": vect_res.shape[1]
    }
    return result


def do_clustering(data, news):
    news_titles = list(map(lambda x: x['title'], news))
    (names, matrix) = compute_matrix(data, news)
    labels = compute_clusters(data, matrix)

    ss = metrics.silhouette_score(matrix, labels)

    news_clusters = []

    for num, lab, news_title in zip(range(1, len(news_titles) + 2), labels, news_titles):
        news_clusters.append({"number": int(num), "label": int(lab), "news_title": news_title})

    result = {
        "news_clusters": news_clusters,
        "silhouette_score": round(ss, 3)
    }

    return result
