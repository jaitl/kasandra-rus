from flask import jsonify, render_template

from app import app

from .lib import load_news

news = load_news.load_news()


@app.route('/')
@app.route('/index')
@app.route('/vectorization')
def index():
    titles = map(lambda x: {"name": x['name'], "alias": x["alias"]}, news.values())

    return render_template("pages/vectorization.html", titles=titles)


@app.route('/vectorization/<algorithm>/<news_id>')
def vectorization(algorithm, news_id):
    print(algorithm, news_id)
    return 'ok'


@app.route('/clustering')
def clustering():
    return render_template("pages/clustering.html")


@app.route('/analysis')
def analysis():
    return render_template("pages/analysis.html")


@app.route('/news/<news_id>')
def news_list(news_id):
    news_data = news[news_id]['news']
    news_titles = list(map(lambda x: x['title'], news_data))

    return jsonify(news_titles)
