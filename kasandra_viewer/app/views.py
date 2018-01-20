from flask import jsonify, render_template, request, redirect, url_for
import os

from app import app

from app.logic.news_service import get_corpuses_name_len, get_corpuses_list, get_news_titles, reload_news
from app.logic.compute_service import do_vectorization, do_clustering, do_analysis
from app.config import path_to_corpus


@app.route('/')
@app.route('/corpuse')
def corpuse():
    return render_template("pages/corpuse.html")


@app.route('/corpuse/upload', methods=['POST'])
def corpuse_upload():
    file = request.files['file']

    file_name = file.filename

    if file_name.endswith('.json'):
        file.save(os.path.join(path_to_corpus(), file_name))
        reload_news()

    return redirect(url_for('corpuse'))


@app.route('/vectorization')
def vectorization():
    return render_template("pages/vectorization.html", titles=get_corpuses_list())


@app.route('/vectorization/compute', methods=['POST'])
def vectorization_compute():
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
