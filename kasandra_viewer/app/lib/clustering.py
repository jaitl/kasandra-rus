from sklearn.cluster import KMeans, AffinityPropagation


def kmeans(matrix, n_clusters):
    km_matrix = KMeans(n_clusters=n_clusters).fit(matrix)
    labels = km_matrix.labels_

    return labels

def affinity_propagation(matrix):
    af_matrix = AffinityPropagation().fit(matrix)
    labels = af_matrix.labels_

    return labels
