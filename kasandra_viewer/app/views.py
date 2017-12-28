from flask import render_template
from flask import jsonify
from app import app


news = [
    ("Новости про олимпиаду", "olympiad", "Текст новости про олимпиаду"),
    ("Новости про политику", "politics", "Текст новости про политику"),
    ("Новости про знаменитостей", "celebrity", "Текст новости про знаменитостей")
]

titles = list(map(lambda x: (x[0], x[1]), news))

@app.route('/')
@app.route('/index')
@app.route('/vectorization')
def index():
    return render_template("pages/vectorization.html", news = titles)


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
    n = list(filter(lambda x: x[1] == news_id, news))
    return jsonify(n)
