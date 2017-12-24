#######################################################################
#
# An example of creating Excel Line charts with Python and XlsxWriter.
#
# Copyright 2013-2017, John McNamara, jmcnamara@cpan.org
#
import xlsxwriter

data = [
    ("K-means", "tf_idf", 130, 396, 1, 397, 0.752, 0.390, 0.040, 5),
    ("K-means", "tf_idf_ngram_2", 130, 1429, 9, 1438, 0.731, 0.331, 0.021, 5),
    ("K-means", "sem_group", 130, 273, 270, 543, 0.735, 0.366, 0.044, 5),
    ("K-means", "lda_10k", 130, 203, 8639, 8842, 0.613, 0.085, 0.067, 4),
    ("K-means", "lda_20k", 130, 318, 17543, 17861, 0.603, 0.064, 0.053, 4),

    ("DbScan", "tf_idf", 227, 6, 1, 7, 0.457, 0.014, 0.003, 2),
    ("DbScan", "tf_idf_ngram_2", 145, 7, 9, 16, 0.328, 0.015, 0.002, 2),
    ("DbScan", "sem_group", 227, 7, 270, 277, 0.501, 0.026, 0.009, 2),
    ("DbScan", "lda_10k", 182, 3, 8639, 8642, 0.434, 0.023, 0.003, 2),
    ("DbScan", "lda_20k", 203, 5, 17543, 17548, 0.425, 0.031, 0.009, 2),

    ("Affinity Propagation", "tf_idf", 1164, 96, 1, 97, 0.706, 0.122, 0.024, 5),
    ("Affinity Propagation", "tf_idf_ngram_2", 1336, 138, 9, 147, 0.699, 0.095, 0.021, 4),
    ("Affinity Propagation", "sem_group", 1088, 89, 270, 359, 0.705, 0.126, 0.034, 5),
    ("Affinity Propagation", "lda_10k", 1052, 43, 8639, 8682, 0.645, 0.094, 0.055, 3),
    ("Affinity Propagation", "lda_20k", 1231, 79, 17543, 17622, 0.612, 0.101, 0.023, 3),

    ("Agglomerative Clustering", "tf_idf", 130, 1836, 1, 1837, 0.710, 0.280, 0.042, 4),
    ("Agglomerative Clustering", "tf_idf_ngram_2", 130, 11343, 9, 11352, 0.690, 0.303, 0.028, 3),
    ("Agglomerative Clustering", "sem_group", 130, 908, 270, 1178, 0.702, 0.276, 0.042, 3),
    ("Agglomerative Clustering", "lda_10k", 130, 416, 8639, 9055, 0.569, 0.069, 0.049, 2),
    ("Agglomerative Clustering", "lda_20k", 130, 641, 17543, 18184, 0.543, 0.061, 0.038, 2),

    ("BIRCH", "tf_idf", 130, 1396, 1, 1397, 0.714, 0.341, 0.042, 5),
    ("BIRCH", "tf_idf_ngram_2", 130, 9750, 9, 9759, 0.676, 0.304, 0.032, 4),
    ("BIRCH", "sem_group", 130, 603, 270, 873, 0.702, 0.321, 0.037, 5),
    ("BIRCH", "lda_10k", 130, 342, 8639, 8981, 0.573, 0.174, 0.072, 3),
    ("BIRCH", "lda_20k", 130, 593, 17543, 18136, 0.590, 0.114, 0.032, 3)
]

data = sorted(data, key=lambda x: x[5])

names_alg = list(map(lambda x: x[0] + " " + x[1], data))
time_alg = list(map(lambda x: x[5], data))
mesure_alg = list(map(lambda x: x[6], data))
ari_alg = list(map(lambda x: x[7], data))
silhouette_alg = list(map(lambda x: x[8], data))
score_alf = list(map(lambda x: x[9], data))

workbook = xlsxwriter.Workbook('cluster_compare.xlsx')


def add_worksheet(name):
    worksheet = workbook.add_worksheet(name)
    bold = workbook.add_format({'bold': 1})

    # Add the worksheet data that the charts will refer to.
    headings = ['Имя', 'Время', 'V мера', 'ARI', 'Силуэт', 'Экспертная оценка']

    worksheet.write_row('A1', headings, bold)
    worksheet.write_column('A2', names_alg)
    worksheet.write_column('B2', time_alg)
    worksheet.write_column('C2', mesure_alg)
    worksheet.write_column('D2', ari_alg)
    worksheet.write_column('E2', silhouette_alg)
    worksheet.write_column('F2', score_alf)

    return worksheet


def add_chart(name, val, pos, numb, wh):
    # Create a new chart object. In this case an embedded chart.
    chart = workbook.add_chart({'type': 'scatter'})

    for i in range(numb[0], numb[1] + 1):
        # Configure the first series.
        chart.add_series({
            'name': '=%s!$A$%s' % (wh.name, i),
            'categories': '=%s!$B$%s' % (wh.name, i),
            'values': '=%s!$%s$%s' % (wh.name, val, i),
            'marker': {'type': 'circle'},
            'data_labels': {'series_name': True, 'position': 'above'}
        })

    # Add a chart title and some axis labels.
    chart.set_title({'name': name})
    chart.set_x_axis({'name': 'Время (сек)'})
    chart.set_y_axis({'name': name})
    wh.insert_chart(pos, chart, {'x_offset': 25, 'y_offset': 10})


positions = ["D2", "E2", "F2"]
rows = [(2, 14), (15, 21), (22, 25)]

worksheet = add_worksheet("V_мера")
for p, r in zip(positions, rows):
    add_chart("V мера", "C", p, r, worksheet)

worksheet = add_worksheet("ARI")
for p, r in zip(positions, rows):
    add_chart("ARI", "D", p, r, worksheet)

worksheet = add_worksheet("Силуэт")
for p, r in zip(positions, rows):
    add_chart("Силуэт", "E", p, r, worksheet)

worksheet = add_worksheet("Экспертная_оценка")
for p, r in zip(positions, rows):
    add_chart("Экспертная оценка", "F", p, r, worksheet)


workbook.close()
