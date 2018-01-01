from sklearn.feature_extraction.text import TfidfVectorizer

from .semantic_group import extractSemanticGroup


def tf_idf(tokens_list, max_features=None):
    tfidf_vectorizer = TfidfVectorizer(use_idf=True, min_df=2, max_df=0.9, norm='l2',
                                       tokenizer=lambda x: x.split(' '), max_features=max_features)

    tokens_str = list(map(lambda x: " ".join(x), tokens_list))
    tfidf_matrix = tfidf_vectorizer.fit_transform(tokens_str)
    return (tfidf_vectorizer.get_feature_names(), tfidf_matrix)


def sem_groups(tokens_list, max_features=None):
    groups = extractSemanticGroup(tokens_list)
    return tf_idf(groups, max_features)
