// init
$(document).ready(function() {
    $.get( "/news/" + $('#news-corpus').val(), function(data) {
        $('#news-preview').val(data)
    });
});

$(document).ready(function() {
    $('#news-corpus').change(function() {
    value = $(this).val()

        $.get( "/news/" + value, function(data) {
            $('#news-preview').val(data)
        });
    });

    $('#vectorization').submit(function(e) {
        e.preventDefault();

        algorithm = $('#vect-alg .active input').attr('id')

        news_id = $('#news-corpus').val()

        url = "/vectorization/" + algorithm + "/" + news_id

        $.get(url);
    })
});
