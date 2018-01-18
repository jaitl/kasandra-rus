"""
    Вычисляет спектр (временной ряд) для конктретного тематического кластера

"""


from datetime import datetime, timedelta
from dateutil import tz
from scipy.sparse import vstack
from scipy import spatial

import matplotlib
matplotlib.use('agg')

from matplotlib.dates import MO

import numpy as np
import matplotlib.pyplot as plt
import matplotlib.dates as mdates


def compute_mix_max_date(seg_news):
    msk = tz.gettz('Europe/Moscow')
    
    dates = [x[0]['date'] for x in seg_news]
    min_date = min(dates)
    max_date = max(dates)
    
    min_datetime = datetime.fromtimestamp(min_date / 1000)
    max_datetime = datetime.fromtimestamp(max_date / 1000) + timedelta(days=1)
    
    start_day = datetime(min_datetime.year, min_datetime.month, min_datetime.day, tzinfo=msk)
    end_day = datetime(max_datetime.year, max_datetime.month, max_datetime.day, tzinfo=msk)
    
    return (start_day, end_day)


def segment_news_daily(seg_news, start_year, end_year):
    day_millis = 24 * 60 * 60 * 1000
    
    segmented_news = {}
    for start_day in range(int(start_year.timestamp())*1000, int(end_year.timestamp())*1000 + 1, day_millis):
        end_day = start_day + day_millis
        cur_news = [n for n in seg_news if start_day <= n[0]['date'] < end_day]
        segmented_news[start_day] = cur_news
    return segmented_news


def day_cos(day_news, shape):
    year_centrid = np.empty(shape[1])
    year_centrid.fill(1)
    cos_news = {}
    for day, (d_news) in day_news.items():
        if len(d_news) > 0:
            vectors = [x[1] for x in d_news]
            matrix = vstack(vectors, format='csr')
            day_centroid = np.array(matrix.mean(axis=0))[0]
            cos_news[day] = 1 - spatial.distance.cosine(year_centrid, day_centroid)
        else:
            cos_news[day] = 0
    return cos_news


def compute_for_cluster(cl_news, shape):
    (start_year, end_year) = compute_mix_max_date(cl_news)
    segmented_news = segment_news_daily(cl_news, start_year, end_year)
    cos_news = day_cos(segmented_news, shape)
    
    return cos_news, start_year, end_year


def generate_plot(cos_days, start_year, end_year, label, path, figsize=(18, 8)):
    fig = plt.figure(figsize=figsize)

    ax = fig.gca()

    myFmt = mdates.DateFormatter('%d.%m.%Y')
    ax.xaxis.set_major_formatter(myFmt)
    ax.xaxis.set_major_locator(mdates.WeekdayLocator(byweekday=MO))

    datemin = start_year - timedelta(days=7)
    datemax = end_year + timedelta(days=7)
    ax.set_xlim(datemin, datemax)

    x = []
    y = []

    sorted_dates = sorted(list(cos_days.keys()))

    for ddd in sorted_dates:
        x.append(datetime.fromtimestamp(ddd / 1000))
        y.append(cos_days[ddd])

    ax.plot(x, y, label=label)
    ax.legend(loc='upper right')
    fig.autofmt_xdate(rotation=60)
    fig.savefig(path)
    plt.close(fig)
