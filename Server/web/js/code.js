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

    $("#bRankix").click(function () {

        if (isWorking) {
            return;
        }

        var treeData = $("#taTree").val();

        if (treeData.trim().length == 0) {
            alert("Tree data can't be empty!");
        } else {

            postProgress(10, "Contacting Tree Manager...");

            $.post(
                "http://localhost:8080/Tree",
                {tree: treeData},
                function (data) {

                    isWorking = true;
                    $("#bRankix").removeClass("btn-primary").addClass("btn-disabled");

                    if (data.error) {
                        alert(data.data);
                        postProgress(0, "");
                    } else {

                        postProgress(10, "Opening socket ...");

                        var movieNameAndId = [];

                        $("div#results").html("");

                        webSocket = new WebSocket("ws://shifar-shifz.rhcloud.com:8000/Rankix/RankixSocket");

                        webSocket.onopen = function (evt) {

                            data.results.forEach(function (obj) {
                                movieNameAndId[obj.id] = obj.name;
                                webSocket.send(JSON.stringify(obj));
                            });
                        };


                        webSocket.onmessage = function (evt) {
                            var data = JSON.parse(evt.data);
                            if (!data.error) {

                                var fontSize = data.data * 5;
                                var movieName = movieNameAndId[data.id];
                                $("div#results").append('<p style="font-size:' + fontSize + 'px;">' + movieName + "<small class='text-muted'> has " + data.data + "</small><p>");

                                var scoreCount = $("#results").children().size() / 2;
                                console.log("ScoreCount:" + scoreCount);
                                console.log("TotalLength:" + movieNameAndId.length);
                                var perc = (scoreCount / movieNameAndId.length ) * 100;
                                console.log("Percentage" + perc);
                                postProgress(perc, parseInt(perc) + "% - Last rankixed : " + movieName);

                                if (perc == 100) {
                                    webSocket.close();
                                }

                            } else {
                                $("div#results").append("<p class='text-danger'>" + data.data + "</p>");
                            }
                        };
                        webSocket.onclose = function (evt) {
                            isWorking = false;
                            $("#bRankix").addClass("btn-primary").removeClass("btn-disabled");
                            $("div#results").append("<p class='text-info'>SOCKET Closed</p>");
                        };
                        webSocket.onerror = function (evt) {
                            isWorking = false;
                            $("#bRankix").addClass("btn-primary").removeClass("btn-disabled");
                            $("div#results").append("<p class='text-danger'>" + evt.data + "</p>");
                        };


                    }
                }
            )


        }

    });

});