$(document).ready(function () {


    var isWorking = true;

    function showProgressBar() {
        $("#divProgress").css({'display': 'block'});
        $("#divProgress").slideDown(2000);
    }

    function hideProgress() {
        $("#divProgress").slideUp(2000);
    }

    function consoleData(data) {
        $("#console_data").prepend('<p>' + data + '</p>');
    }


    //webSocket = new WebSocket("ws://shifar-shifz.rhcloud.com:8000/Rankix/RankixSocket");
    webSocket = new WebSocket("ws://localhost:8080/RankixSocket");

    consoleData("CONNECTING...");

    $("#bRankix").removeClass("btn-primary").addClass("btn-disabled");

    webSocket.onopen = function (evt) {
        hideProgress();
        freeApp();
        consoleData("CONNECTED TO SOCKET :)");
    };


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


    function freezeApp() {
        isWorking = true;
        $("#bRankix").removeClass("btn-primary").addClass("btn-disabled");
    }

    function freeApp() {
        isWorking = false;
        $("#bRankix").addClass("btn-primary").removeClass("btn-disabled");
    }

    $("#bRankix").click(function () {

        postProgress(20, "Contacting TREE Manager...");

        if (webSocket == null || webSocket.readyState != 1) {
            consoleData("Reopening new socket...");
            //webSocket = new WebSocket("ws://shifar-shifz.rhcloud.com:8000/Rankix/RankixSocket");
            webSocket = new WebSocket("ws://localhost:8080/RankixSocket");
            isWorking = false;
        }

        if (isWorking) {
            consoleData("Work in progress...")
            return;
        }

        var treeData = $("#taTree").val();

        function showError(errorReason) {
            $("div#results").prepend('<p id="0" class="text-danger"><strong>Error: </strong>'+errorReason+'</p>');
        }

        if (treeData.trim().length == 0) {
            alert("Tree data can't be empty!");
        } else {

            showProgressBar();

            freezeApp();
            $.post("http://localhost:8080/Tree", {tree: treeData})
            //$.post("/Rankix/Tree", {tree: treeData})
                .done(function (data) {

                    postProgress(100,"TREE Managed!")


                    if (data.error) {
                        postProgress(0, "");
                        hideProgress();
                        freeApp();
                        showError(data.data);
                        consoleData(data.data);
                    } else {

                        $("#pbProgress")
                            .removeClass("progress-bar-success")
                            .addClass("progress-bar-info progress-bar-striped active");

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

                        var date = new Date();

                        var startTime = date.getTime();

                        consoleData("---------------------------------");
                        consoleData("Requested at " + date.toLocaleTimeString());
                        consoleData("Total elements count: " + totalElementsCount);
                        consoleData("Rankixed file count: " + rankixedFileCount);
                        consoleData("Ignored file count: " + ignoredFileCount);
                        consoleData("Fine file count: " + fineFileCount);


                        if (fineFileCount == 0) {
                            freeApp();
                            showError("No fine file found!");
                            return;
                        }


                        $("div#results").html("");

                        data.results.forEach(function (obj) {
                            movieNameAndId[obj.id] = obj.name;
                            webSocket.send(JSON.stringify(obj));
                        });


                        webSocket.onmessage = function (evt) {

                            var data = JSON.parse(evt.data);
                            var movieName = movieNameAndId[data.id];

                            function addResult(fontSize, movieName, data) {
                                $("div#results").prepend('<p id="' + data + '" style="font-size:' + fontSize + 'px;">' + movieName + '<small class="text-muted"> has ' + data + '</small></p>');
                            }

                            if (!data.error) {
                                var fontSize = data.data * 5;
                                addResult(fontSize, movieName, data.data);
                            } else {

                                var myRegexp = /^IMDB rating is null for (.+)$/;
                                var match = myRegexp.exec(data.data);
                                movieName = match[1];
                                $("div#results").prepend('<p id="0" class="text-danger"><strong>' + movieName + '</strong> has no rating</p>');
                            }

                            var scoreCount = $("#results p").length;

                            var perc = (scoreCount / movieNameAndId.length ) * 100;
                            postProgress(perc, parseInt(perc) + "% - Last rankixed : " + movieName);

                            if (perc == 100) {
                                var finishTime = new Date().getTime();
                                consoleData("Took " + Math.round(((finishTime - startTime) / 1000)) + "s");
                                consoleData("---------------------------------");
                                freeApp();

                                $("div#results p").sort(function (a, b) {
                                    console.log(a.id + " " + b.id);
                                    return parseFloat(a.id) > parseFloat(b.id);
                                }).each(function(){
                                    var elem = $(this);
                                    elem.remove();
                                    $(elem).prependTo("div#results");
                                });
                            }

                            //Sorting


                        };

                        webSocket.onclose = function (evt) {
                            consoleData("Socket closed");
                            freeApp();
                            $("div#results").prepend("<p id='0' class='text-info'>SOCKET Closed</p>");
                        };
                        webSocket.onerror = function (evt) {
                            freeApp();
                            $("div#results").prepend("<p id='0' class='text-danger'>" + evt.data + "</p>");
                        };
                    }
                })
                .fail(function () {
                    hideProgress();
                    freeApp();
                    showError("Network error occured, Please check your connection! ");
                })

        }

    });

});