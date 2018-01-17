function documentTerminMatrix(title, matrix) {
    start = "<table class='table'>"
    end = "</table>"

    tableTitle = _.reduce(title, function (res, n) {
        return res + "<th scope='col'>" + n + "</th>"
    }, "<th scope='col'>№ документа</th>")

    tableTitleRes = "<thead><tr>" + tableTitle + "</tr></thead>"

    tableBody = _.reduce(matrix, function (res, row, num) {
        columns = _.reduce(row, function (r, c) {
            return r + "<td>" + c + "</td>"
        }, "<th scope='row'>" + (num + 1) + "</th>")
        return res + "<tr>" + columns + "</tr>"
    }, "")

    tableBodyRes = "<tbody>" + tableBody + "</tbody>"

    return start + tableTitleRes + tableBodyRes + end
}

function createClusterAnalysisTab(elem) {
    id = "cluster" + elem.label

    hurst_png = 'static/tmp/' + elem.hirst_img
    spect_png = 'static/tmp/' + elem.spect_img

    content = ""

    content += '<h4>График спектра кластера:</h4>'
    content += '<img id="spec" src="' + spect_png + '" width="800"/><br/>'

    content += '<br/><h4>График тренда самоподобия:</h4>'
    content += '<img id="hurst" src="' + hurst_png + '" width="800"/><br/>'
    content += '<p><b>Линейное уравнение:</b> ' + elem.y + '</p>'
    content += '<p><b>Коэффициент корреляции:</b> ' + elem.correlation + '</p>'
    content += '<p><b>Коэффициент Хёрста:</b> ' + elem.h + '</p>'


    content += '<br/><h4>Список новостей в кластере:</h4>'
    content += '<div class="cluster_titles_alisys">' + documentTerminMatrix(['Заголовок новости', 'Дата публикации'], elem.titles) + '</div>'

    panelStart = '<div class="panel panel-default">';
    panelEnd = '</div>'

    tapHeader = '<div class="panel-heading" role="tab" id="' + "head_" + id + '"><h4 class="panel-title"><a role="button" data-toggle="collapse" data-parent="#accordion" href="#' + id + '" aria-expanded="true" aria-controls="' + id + '">' + elem.label_name + '</a></h4></div>'
    tabBody = '<div id="' + id + '" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="' + "head_" + id + '"><div class="panel-body">' + content + '</div></div>'

    return panelStart + tapHeader + tabBody + panelEnd
}

function loadNews(alias) {
    $.get("/news/" + alias, function (data) {

        news = _.map(data, function (a) {
            return [a]
        })

        $('#news-count').empty()
        $('#news-count').append("<br/>Количество новостей в коллекции: " + data.length)

        $('#news-table').empty()
        $('#news-table').append(documentTerminMatrix(['Заголовок новости'], news))
    });
}

function getWordCount() {
    words_count = $("#dict-count").val()

    if (limit_words) {
        if (words_count.length == 0 || !$.isNumeric(words_count) || parseInt(words_count) <= 0) {
            console.log("wrong format: " + words_count)
            throw "wrong word count"
        } else {
            return parseInt(words_count)
        }
    }

    return null
}

function getClusterCount(limit_cluster) {
    cluster_count = $("#clust-count").val()

    if (limit_cluster) {
        if (cluster_count.length == 0 || !$.isNumeric(cluster_count) || parseInt(cluster_count) <= 0) {
            console.log("wrong format: " + cluster_count)
            throw "wrong cluster count"
        } else {
            return parseInt(cluster_count)
        }
    }

    return null
}

function addThreeListner() {
    $('#jtree_clusters').on("select_node.jstree", function (e, data) {
        data.instance.toggle_node(data.node);
    });
}

// init
$(document).ready(function () {
    loadNews($('#news-corpus').val())
    $("#vect_res_block").hide()
});

// common
$(document).ready(function () {
    $('#news-corpus').change(function () {
        loadNews($(this).val())
    });

    $("#dict-count-checkbox").change(function () {
        if (this.checked) {
            $("#dict-count").removeAttr('disabled');
            $("#dict-count").prop('disabled', false)
        } else {
            $("#dict-count").val("")
            $("#dict-count").prop('disabled', true)
        }
    })

    $("#clust-from-group input[name=options]").change(function () {
        if ($(this).attr('id') == "aff_prop") {
            $("#clust-count").val("")
            $("#clust-count").prop('disabled', true)
        } else {
            $("#clust-count").val("")
            $("#clust-count").prop('disabled', false)
        }
    })
})

// vectorization
$(document).ready(function () {
    $('#vectorization').submit(function (e) {
        e.preventDefault();
        $("#res_info").empty()
        $("#res_matrix").empty()
        $("#vect_res_block").hide()

        limit_words = $("#dict-count-checkbox").is(':checked')

        request = {
            "vect_algorithm": $('#vect-alg .active input').attr('id'),
            "news_id": $('#news-corpus').val(),
            "limit_words": limit_words
        }

        words_count = getWordCount(limit_words)

        if (words_count != null) {
            request['words_count'] = words_count
        }

        $.post("/vectorization/compute", JSON.stringify(request),
            function (data) {
                columns = "Количество слов в векторе: " + data.columns
                rows = "Количество документов: " + data.rows
                $("#res_info").append("<div>" + columns + "</div>")
                $("#res_info").append("<div>" + rows + "</div>")

                $("#res_matrix").append(documentTerminMatrix(data.title, data.matrix))

                $("#vect_res_block").show()
            });
    })
});

// clustering
$(document).ready(function () {
    $('#clustering_res_block').hide()

    $('#clustering').submit(function (e) {
        e.preventDefault();
        $('#clustering_res_block').hide()
        $('#jtree_clusters').jstree("destroy").empty();
        $('#clustering_res_metrics').empty()

        limit_words = $("#dict-count-checkbox").is(':checked')
        cluster_algorithm = $('#clust-alg .active input').attr('id')

        request = {
            "vect_algorithm": $('#vect-alg .active input').attr('id'),
            "news_id": $('#news-corpus').val(),
            "limit_words": limit_words,
            "cluster_algorithm": cluster_algorithm
        }

        words_count = getWordCount(limit_words)

        if (words_count != null) {
            request['words_count'] = words_count
        }

        cluster_count = getClusterCount(cluster_algorithm == "kmeans")

        if (cluster_count != null) {
            request['cluster_count'] = cluster_count
        }

        $.post("/clustering/compute", JSON.stringify(request),
            function (data) {
                console.log(data)
                $('#jtree_clusters').jstree({
                    'core': {
                        'data': data['clusters_three']
                    }
                })
                addThreeListner()

                $("#clustering_res_metrics").append("<div>Количество кластеров: " + data['cluster_count'] + "</div>")
                $("#clustering_res_metrics").append("<div>Метрика силуэт: " + data['silhouette_score'] + "</div>")
                $("#clustering_res_metrics").append("<br />")

                $('#clustering_res_block').show()
            })
    })
});


// analysis
$(document).ready(function () {

    $('#analysis_res_block').hide();

    $('#analysis').submit(function (e) {
        e.preventDefault();
        $('#analysis_res_block').hide();
        $("#accordion").empty()

        limit_words = $("#dict-count-checkbox").is(':checked')
        cluster_algorithm = $('#clust-alg .active input').attr('id')

        request = {
            "vect_algorithm": $('#vect-alg .active input').attr('id'),
            "news_id": $('#news-corpus').val(),
            "limit_words": limit_words,
            "cluster_algorithm": cluster_algorithm
        }

        words_count = getWordCount(limit_words)

        if (words_count != null) {
            request['words_count'] = words_count
        }

        cluster_count = getClusterCount(cluster_algorithm == "kmeans")

        if (cluster_count != null) {
            request['cluster_count'] = cluster_count
        }

        $.ajax({
            type:    "POST",
            url:     "/analysis/compute",
            data:    JSON.stringify(request),
            success: function(data) {
                console.log(data)

                accordion_data = _.reduce(data.result, function (total, elem, numb) {
                    return total += createClusterAnalysisTab(elem)
                }, "");

                $("#accordion").html(accordion_data)

                $('.panel-collapse').collapse('toggle');

                $('#analysis_res_block').show();
            },
            error:   function(jqXHR, textStatus, errorThrown) {
                console.log("Error, status = " + textStatus + ", " +
                        "error thrown: " + errorThrown
                  );
            }
          });
    })
});
