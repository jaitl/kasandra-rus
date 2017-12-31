function loadNews(alias) {
    $.get("/news/" + alias, function (data) {
        content = _.reduce(data, function (res, n) {
            if (res.length === 0) {
                return n
            } else {
                return res + "\n" + n
            }
        }, "")

        $('#news-preview').val(content)
    });
}

// init
$(document).ready(function () {
    loadNews($('#news-corpus').val())
});

$(document).ready(function () {
    $('#news-corpus').change(function () {
        loadNews($(this).val())
    });

    $('#vectorization').submit(function (e) {
        e.preventDefault();

        algorithm = $('#vect-alg .active input').attr('id')

        news_id = $('#news-corpus').val()

        url = "/vectorization/" + algorithm + "/" + news_id

        $.get(url, function(data) {
            $("#vec_res").text(data)
        });
    })
});
