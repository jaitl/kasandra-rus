from flask import jsonify, render_template, request

from app import app

from app.logic.load_news import load_news
from app.logic.compute_service import do_vectorization, do_clustering, do_analysis

news = load_news()


@app.route('/')
@app.route('/index')
@app.route('/vectorization')
def index():
    titles = map(lambda x: {"name": x['name'], "alias": x["alias"]}, news.values())

    return render_template("pages/vectorization.html", titles=titles)


@app.route('/vectorization/compute', methods=['POST'])
def vectorization():
    data = request.get_json(force=True)
    print(data)
    news_id = data['news_id']
    res = do_vectorization(data, news[news_id]['news'])
    return jsonify(res)


@app.route('/clustering')
def clustering():
    titles = map(lambda x: {"name": x['name'], "alias": x["alias"]}, news.values())

    return render_template("pages/clustering.html", titles=titles)


@app.route('/clustering/compute', methods=['POST'])
def clustering_compute():
    data = request.get_json(force=True)
    print(data)
    news_id = data['news_id']
    res = do_clustering(data, news[news_id]['news'])
    return jsonify(res)


@app.route('/analysis')
def analysis():
    titles = map(lambda x: {"name": x['name'], "alias": x["alias"]}, news.values())

    return render_template("pages/analysis.html", titles=titles)


@app.route('/analysis/compute', methods=['POST'])
def analysis_compute():
    data = request.get_json(force=True)
    print(data)
    news_id = data['news_id']

    res = do_analysis(data, news[news_id]['news'])

    return jsonify(res)


@app.route('/news/<news_id>')
def news_list(news_id):
    news_data = news[news_id]['news']
    news_titles = list(map(lambda x: x['title'], news_data))

    return jsonify(news_titles)
