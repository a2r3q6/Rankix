$(document).ready(function () {

    function postProgress(perc, sentance) {
        if (perc == 100) {
            $("#pbProgress")
                .removeClass("progress-bar-info progress-bar-striped active")
                .addClass("progress-bar-success")
                .attr('aria-valuenow', 100)
                .css({width: "100%"})
                .html("Finished (100%)");
        } else if (perc == 10) {
            $("#pbProgress")
                .removeClass("progress-bar-success")
                .addClass("progress-bar-info progress-bar-striped active")
                .attr('aria-valuenow', 10)
                .css({width: "10%"})
                .html("Initializing...");
        } else {
            $("#pbProgress")
                .attr('aria-valuenow', perc)
                .css({width: perc + "%"})
                .html(sentance);
        }
    }

    var isWorking = false;

    function showProgressBar() {
        $("#divProgress").css({'display': 'block'});
        $("#divProgress").slideDown(2000);
    }

    function hideProgress() {
        $("#divProgress").slideUp(2000);
    }

    function consoleData(data){
        $("#console_data").prepend('<p>'+ data + '</p>');
    }

    $("#bRankix").click(function () {

        if (isWorking) {
            return;
        }

        var treeData = $("#taTree").val();

        if (treeData.trim().length == 0) {
            alert("Tree data can't be empty!");
        } else {

            showProgressBar();

            postProgress(10, "Contacting Tree Manager...");

            isWorking = true;
            $("#bRankix").removeClass("btn-primary").addClass("btn-disabled");

            $.post("/Rankix/Tree", {tree: treeData})
                .done(function (data) {

                    consoleData(data);

                    if (data.error) {
                        postProgress(0, "");
                        hideProgress();
                        $("#bRankix").addClass("btn-primary").removeClass("btn-disabled");
                        $("div#results").prepend("<div class='alert alert-danger'><strong>Error:</strong>" + data.data + "</div>");
                        isWorking = false;
                        consoleData(data.data);
                    } else {

                        postProgress(10, "Opening socket ...");

                        var movieNameAndId = [];

                        /*{
                         "ignored_element_count":14,
                         "total_elements_found":18,
                         "rankixed_file_count":0,
                         "movie_file_count":4}*/

                        var totalElementsCount = data.total_elements_found;
                        var rankixedFileCount = data.rankixed_file_count;
                        var ignoredFileCount = data.ignored_element_count;
                        var fineFileCount = data.movie_file_count;

                        consoleData("Total elements count: "+totalElementsCount);
                        consoleData("Rankixed file count: "+rankixedFileCount);
                        consoleData("Ignored file count: "+ignoredFileCount);
                        consoleData("Fine file count: "+fineFileCount);

                        if(fineFileCount==0){
                            isWorking = false;
                            $("#bRankix").addClass("btn-primary").removeClass("btn-disabled");
                            $("div#results").prepend("<p class='text-info'>No fine file found!</p>");
                            return;
                        }


                        $("div#results").html("");

                        webSocket = new WebSocket("ws://shifar-shifz.rhcloud.com:8000/Rankix/RankixSocket"  );

                        webSocket.onopen = function (evt) {

                            data.results.forEach(function (obj) {
                                movieNameAndId[obj.id] = obj.name;
                                webSocket.send(JSON.stringify(obj));
                            });
                        };


                        webSocket.onmessage = function (evt) {


                            var data = JSON.parse(evt.data);
                            var movieName = movieNameAndId[data.id];

                            if (!data.error) {
                                var fontSize = data.data * 5;
                                $("div#results").prepend('<p style="font-size:' + fontSize + 'px;">' + movieName + '<small class="text-muted"> has ' + data.data + '</small></p>');

                            } else {

                                var myRegexp = /^IMDB rating is null for (.+)$/;
                                var match = myRegexp.exec(data.data);
                                movieName = match[1];
                                $("div#results").prepend('<p class="text-danger"><strong>' + movieName + '</strong> has no rating</p>');
                            }

                            var scoreCount = $("#results p").length;

                            //console.log("ScoreCount:" + scoreCount);
                            //console.log("TotalLength:" + movieNameAndId.length);
                            var perc = (scoreCount / movieNameAndId.length ) * 100;
                            //console.log("Percentage" + perc);
                            postProgress(perc, parseInt(perc) + "% - Last rankixed : " + movieName);

                            if (perc == 100) {
                                hideProgress();
                                webSocket.close();
                            }
                        };
                        webSocket.onclose = function (evt) {
                            isWorking = false;
                            $("#bRankix").addClass("btn-primary").removeClass("btn-disabled");
                            $("div#results").prepend("<p class='text-info'>SOCKET Closed</p>");
                        };
                        webSocket.onerror = function (evt) {
                            isWorking = false;
                            $("#bRankix").addClass("btn-primary").removeClass("btn-disabled");
                            $("div#results").prepend("<p class='text-danger'>" + evt.data + "</p>");
                        };
                    }
                })
                .fail(function () {
                    isWorking = false;
                    hideProgress();
                    $("#bRankix").addClass("btn-primary").removeClass("btn-disabled");
                    $("div#results").prepend("<p class='text-danger'>Network error occurred, Please check your connection</p>");
                })

        }

    });

});