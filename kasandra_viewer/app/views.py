from flask import jsonify, render_template, request

from app import app

from app.logic.news_service import get_corpuses_name_len, get_corpuses_list, get_news_titles
from app.logic.compute_service import do_vectorization, do_clustering, do_analysis


@app.route('/')
@app.route('/index')
@app.route('/vectorization')
def index():
    return render_template("pages/vectorization.html", titles=get_corpuses_list())


@app.route('/vectorization/compute', methods=['POST'])
def vectorization():
    data = request.get_json(force=True)
    print("/vectorization/compute, request: %s" % data)

    res = do_vectorization(data)
    return jsonify(res)


@app.route('/clustering')
def clustering():
    return render_template("pages/clustering.html", titles=get_corpuses_list())


@app.route('/clustering/compute', methods=['POST'])
def clustering_compute():
    data = request.get_json(force=True)
    print("/clustering/compute, request: %s" % data)

    res = do_clustering(data)
    return jsonify(res)


@app.route('/analysis')
def analysis():
    return render_template("pages/analysis.html", titles=get_corpuses_list())


@app.route('/analysis/compute', methods=['POST'])
def analysis_compute():
    data = request.get_json(force=True)
    print("/analysis/compute, request: %s" % data)

    res = do_analysis(data)

    return jsonify(res)


@app.route('/news/<news_id>')
def news_list(news_id):
    return jsonify(get_news_titles(news_id))


@app.route('/corpuse/list')
def corpuse_list():
    news = {
        "news": get_corpuses_name_len()
    }
    return jsonify(news)
