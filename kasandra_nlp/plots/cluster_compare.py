#######################################################################
#
# An example of creating Excel Line charts with Python and XlsxWriter.
#
# Copyright 2013-2017, John McNamara, jmcnamara@cpan.org
#
import xlsxwriter

workbook = xlsxwriter.Workbook('cluster_compare.xlsx')
worksheet = workbook.add_worksheet()
bold = workbook.add_format({'bold': 1})

# Add the worksheet data that the charts will refer to.
headings = ['Имя', 'Время', 'V мера', 'ARI', 'Силуэт']
data = [
    ("K-means", "tf_idf", 130, 396, 0.754, 0.374, 0.049),
    ("K-means", "tf_idf_ngram_2", 130, 1880, 0.717, 0.276, 0.017),
    ("K-means", "sem_group", 130, 273, 0.742, 0.376, 0.048),
    ("K-means", "sem_group_2", 130, 1838, 0.698, 0.185, 0.012),
    ("K-means", "lda_10k", 130, 203, 0.613, 0.085, 0.067),
    ("K-means", "lda_20k", 130, 318, 0.603, 0.064, 0.053),

    ("DbScan", "tf_idf", 227, 6, 0.457, 0.014, 0.003),
    ("DbScan", "tf_idf_ngram_2", 145, 7, 0.328, 0.015, 0.002),
    ("DbScan", "sem_group", 227, 7, 0.501, 0.026, 0.009),
    ("DbScan", "sem_group_2", 214, 8, 0.440, 0.013, 0.007),
    ("DbScan", "lda_10k", 182, 3, 0.434, 0.023, 0.003),
    ("DbScan", "lda_20k", 203, 5, 0.425, 0.031, 0.009),

    ("Affinity Propagation", "tf_idf", 1164, 96, 0.706, 0.122, 0.022),
    ("Affinity Propagation", "tf_idf_ngram_2", 1336, 138, 0.698, 0.105, 0.010),
    ("Affinity Propagation", "sem_group", 1088, 89, 0.704, 0.126, 0.034),
    ("Affinity Propagation", "sem_group_2", 1255, 97, 0.695, 0.110, 0.013),
    ("Affinity Propagation", "lda_10k", 1052, 43, 0.645, 0.094, 0.055),
    ("Affinity Propagation", "lda_20k", 1231, 79, 0.612, 0.101, 0.023),

    ("Agglomerative Clustering", "tf_idf", 130, 1816, 0.710, 0.311, 0.032),
    ("Agglomerative Clustering", "tf_idf_ngram_2", 130, 11343, 0.690, 0.303, 0.028),
    ("Agglomerative Clustering", "sem_group", 130, 851, 0.697, 0.291, 0.047),
    ("Agglomerative Clustering", "sem_group_2", 130, 9343, 0.667, 0.264, 0.036),
    ("Agglomerative Clustering", "lda_10k", 130, 416, 0.569, 0.069, 0.049),
    ("Agglomerative Clustering", "lda_20k", 130, 641, 0.543, 0.061, 0.038),

    ("BIRCH", "tf_idf", 8479, 51, 0.672, 0.005, 0.007),
    ("BIRCH", "tf_idf_ngram_2", 9434, 73, 0.667, 0.007, 0.010),
    ("BIRCH", "sem_group", 7994, 32, 0.676, 0.009, 0.010),
    ("BIRCH", "sem_group_2", 9135, 69, 0.671, 0.007, 0.009),
    ("BIRCH", "lda_10k", 7168, 20, 0.676, 0.020, 0.014),
    ("BIRCH", "lda_20k", 7542, 29, 0.673, 0.012, 0.012)
]

names_alg = list(map(lambda x: x[0] + " " + x[1], data))
time_alg = list(map(lambda x: x[3], data))
mesure_alg = list(map(lambda x: x[4], data))
ari_alg = list(map(lambda x: x[5], data))
silhouette_alg = list(map(lambda x: x[6], data))

worksheet.write_row('A1', headings, bold)
worksheet.write_column('A2', names_alg)
worksheet.write_column('B2', time_alg)
worksheet.write_column('C2', mesure_alg)
worksheet.write_column('D2', ari_alg)
worksheet.write_column('E2', silhouette_alg)

# Create a new chart object. In this case an embedded chart.
chart1 = workbook.add_chart({'type': 'scatter'})

# http://xlsxwriter.readthedocs.io/working_with_charts.html#chart-series-option-marker
for i in range(2, len(data) + 2):
    # Configure the first series.
    chart1.add_series({
        'name':       '=Sheet1!$A$%s' % i,
        'categories': '=Sheet1!$B$%s' % i,
        'values':     '=Sheet1!$C$%s' % i,
        'marker': {'type': 'circle'},
        'data_labels': {'series_name': True}
    })

# Add a chart title and some axis labels.
chart1.set_title ({'name': 'Аназиз качества кластеризакции'})
chart1.set_x_axis({'name': 'Время (сек)'})
chart1.set_y_axis({'name': 'V мера'})

# Insert the chart into the worksheet (with an offset).
worksheet.insert_chart('D2', chart1, {'x_offset': 25, 'y_offset': 10})

# Create a new chart object. In this case an embedded chart.
chart2 = workbook.add_chart({'type': 'scatter'})

# http://xlsxwriter.readthedocs.io/working_with_charts.html#chart-series-option-marker
for i in range(2, len(data) + 2):
    # Configure the first series.
    chart2.add_series({
        'name':       '=Sheet1!$A$%s' % i,
        'categories': '=Sheet1!$B$%s' % i,
        'values':     '=Sheet1!$D$%s' % i,
        'marker': {'type': 'circle'},
        'data_labels': {'series_name': True}
    })

# Add a chart title and some axis labels.
chart2.set_title ({'name': 'Аназиз качества кластеризакции'})
chart2.set_x_axis({'name': 'Время (сек)'})
chart2.set_y_axis({'name': 'ARI'})

# Insert the chart into the worksheet (with an offset).
worksheet.insert_chart('E2', chart2, {'x_offset': 25, 'y_offset': 10})

# Create a new chart object. In this case an embedded chart.
chart3 = workbook.add_chart({'type': 'scatter'})

# http://xlsxwriter.readthedocs.io/working_with_charts.html#chart-series-option-marker
for i in range(2, len(data) + 2):
    # Configure the first series.
    chart3.add_series({
        'name':       '=Sheet1!$A$%s' % i,
        'categories': '=Sheet1!$B$%s' % i,
        'values':     '=Sheet1!$E$%s' % i,
        'marker': {'type': 'circle'},
        'data_labels': {'series_name': True}
    })

# Add a chart title and some axis labels.
chart3.set_title ({'name': 'Аназиз качества кластеризакции'})
chart3.set_x_axis({'name': 'Время (сек)'})
chart3.set_y_axis({'name': 'Силуэт'})

# Insert the chart into the worksheet (with an offset).
worksheet.insert_chart('F2', chart3, {'x_offset': 25, 'y_offset': 10})

workbook.close()
