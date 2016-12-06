$(document).ready(function () {

    $("#bSort").hide();
    $("#bShare").hide();

    var movieCache = new Array();
    //var movieRatingCache = new Array();

    var isDebugMode = false;
    var webSocketAddress, imdbServletUrl, treeUrl, sortUrl, shareServletUrl, sharedDataUrl;

    if (isDebugMode) {
        webSocketAddress = "ws://localhost:8080/RankixSocket";
    } else {
        webSocketAddress = "ws://theapache64.xyz:8080/rankix/RankixSocket";
    }

    imdbServletUrl = "/rankix/imdbServlet";
    treeUrl = "/rankix/Tree";
    sortUrl = "/rankix/sortServlet";
    shareServletUrl = "/rankix/shareServlet";
    sharedDataUrl = "/rankix/shared/";

    var sharedLink = null;

    var isWorking = true;

    function showProgressBar() {
        $("#divProgress").css({'display': 'block'});
        $("#divProgress").slideDown(2000);
    }

    function getParameterByName(name) {
        name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
        var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
            results = regex.exec(location.search);
        return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
    }

    function hideProgress() {
        $("#divProgress").slideUp(2000);
    }

    function consoleData(data) {
        $("#console_data").prepend('<p>' + data + '</p>');
    }


    webSocket = new WebSocket(webSocketAddress);

    consoleData("CONNECTING...");

    $("#bRankix").removeClass("btn-primary").addClass("btn-disabled");

    webSocket.onopen = function (evt) {
        hideProgress();
        freeApp();
        consoleData("CONNECTED TO SOCKET :)");
    };

    //Checking if the intent is to access shared data
    var params = window.location.search;
    var key = getParameterByName('key');
    if (key.length == 10) {

        postProgress(70, "Loading shared data... ")

        //Valid key
        $.ajax({
            url: sharedDataUrl + key,
            type: "get",
            success: function (response) {
                hideProgress();
                if (response.error) {
                    alert(response.message);
                } else {
                    $("div#results").html(response.data);
                }
            },
            error: function (xhr) {
                console.log(xhr);
                consoleData("Error " + xhr.data);
                consoleData("Error " + xhr.data);
            }
        });
    }


    $("#bShare").click(function () {

        if (sharedLink == null) {

            var shareData = $("#results").html();

            postProgress(70, "Sharing... This may take some time.")

            $.ajax({
                url: shareServletUrl,
                xhr: function () {
                    var xhr = new window.XMLHttpRequest();
                    xhr.upload.addEventListener("progress", function (evt) {
                        if (evt.lengthComputable) {
                            var percentComplete = evt.loaded / evt.total;
                            //Do something with upload progress here
                            postProgress(percentComplete, "Saving result...");
                        }
                    }, false);

                    xhr.addEventListener("progress", function (evt) {
                        if (evt.lengthComputable) {
                            var percentComplete = evt.loaded / evt.total;
                            //Do something with download progress
                            postProgress(percentComplete, "Downloading shared link...");
                        }
                    }, false);

                    return xhr;
                },
                type: "post",
                data: {share_data: shareData},
                success: function (response) {

                    console.log(response);
                    hideProgress();
                    if (response.error) {
                        consoleData("Error :" + response.message);
                    } else {
                        sharedLink = response.shared_data_url;
                        window.prompt("Shared! Press Control+C to copy the link", sharedLink);
                        consoleData(response.shared_data_url);
                    }
                },
                error: function (xhr) {
                    console.log(xhr);
                }
            });

        } else {
            window.prompt("Press Control + C to copy the shareable link.", sharedLink);
        }

    });

    $("#bSort").click(function () {

        sharedLink = null;

        var resultHtml = $("#results").html();

        $.ajax({

            url: sortUrl,
            xhr: function () {
                var xhr = new window.XMLHttpRequest();
                xhr.upload.addEventListener("progress", function (evt) {
                    if (evt.lengthComputable) {
                        var percentComplete = evt.loaded / evt.total;
                        //Do something with upload progress here
                        postProgress(percentComplete, "Sorting (UP)...");
                    }
                }, false);

                xhr.addEventListener("progress", function (evt) {
                    if (evt.lengthComputable) {
                        var percentComplete = evt.loaded / evt.total;
                        //Do something with download progress
                        postProgress(percentComplete, "Sorting (DOWN)...");
                    }
                }, false);

                return xhr;
            },
            type: "post",
            data: {results: resultHtml},
            success: function (response) {

                postProgress(100, "Finished");
                hideProgress();

                console.log(response);

                if (!response.error) {
                    $("#bSort").fadeOut(1000);
                    $("#bShare").fadeIn(1000);
                    $("#results").html(response.results);
                } else {
                    alert(response.message);
                }

            },
            error: function (xhr) {
                hideProgress();
                console.log(xhr);
                consoleData("Error while " + xhr.data);
            }
        });

    });


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

        if (isWorking) {
            consoleData("Work in progress...")
            return;
        }

        sharedLink = null;

        $("#bSort").hide();
        $("#bShare").hide();

        var movieCache = new Array();
        //TODO: X
        //var movieRatingCache = new Array();

        postProgress(20, "Contacting TREE Manager...");

        if (webSocket == null || webSocket.readyState != 1) {
            consoleData("Reopening new socket...");

            webSocket = new WebSocket(webSocketAddress);
            isWorking = false;
        }


        var treeData = $("#taTree").val();

        function showError(errorReason) {
            $("div#results").prepend('<p r="0" class="text-danger"><strong>Error: </strong>' + errorReason + '</p>\n');
        }


        if (treeData.trim().length == 0) {
            alert("Tree data can't be empty!");
        } else {

            showProgressBar();

            freezeApp();

            $.ajax({
                url: treeUrl,
                xhr: function () {
                    var xhr = new window.XMLHttpRequest();
                    xhr.upload.addEventListener("progress", function (evt) {
                        if (evt.lengthComputable) {
                            var percentComplete = evt.loaded / evt.total;
                            //Do something with upload progress here
                            postProgress(percentComplete, "Scanning tree ...");
                        }
                    }, false);

                    xhr.addEventListener("progress", function (evt) {
                        if (evt.lengthComputable) {
                            var percentComplete = evt.loaded / evt.total;
                            //Do something with download progress
                            postProgress(percentComplete, "Downloading scan result...");
                        }
                    }, false);

                    return xhr;
                },
                type: "post",
                data: {tree: treeData},
                success: function (data) {

                    postProgress(100, "Tree scan completed, please wait...")


                    if (data.error) {
                        postProgress(0, "");
                        hideProgress();
                        freeApp();
                        showError(data.message);
                        consoleData(data.message);
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

                        function handleData(data) {

                            var movieName = movieNameAndId[data.id];

                            console.log(data);


                            function addResult(fontSize, movieName, imdbId, imdbRating) {
                                $("div#results").prepend('<p r="' + imdbRating + '" data-toggle="modal" data-target="#myModal" class="movieRow" id="' + imdbId + '" style="font-size:' + fontSize + 'px;">' + movieName + '<small class="text-muted"> has ' + imdbRating + '</small></p>\n');
                            }

                            if (!data.error) {
                                var fontSize = data.data * 5;
                                addResult(fontSize, movieName, data.imdb_id, data.data);
                            } else {

                                console.log('Data is ' + data.message);

                                var myRegexp = /^Invalid movie name (.+)$/;
                                var match = myRegexp.exec(data.message);
                                movieName = match[1];
                                $("div#results").prepend('<p r="0" class="text-danger"><strong>' + movieName + '</strong> has no rating</p>\n');
                            }

                            var scoreCount = $("#results p").length;

                            var perc = (scoreCount / movieNameAndId.length ) * 100;
                            postProgress(perc, parseInt(perc) + "%");

                            if (perc == 100) {

                                $("#bSort").fadeIn(1000);
                                $("#bShare").fadeIn(1000);

                                var finishTime = new Date().getTime();
                                consoleData("Took " + Math.round(((finishTime - startTime) / 1000)) + "s");
                                consoleData("---------------------------------");
                                freeApp();

                                /*
                                 //SORTING REMOVED
                                 $("div#results p").sort(function (a, b) {
                                 console.log(a.id + " " + b.id);
                                 return parseFloat(a.id) > parseFloat(b.id);
                                 }).each(function(){
                                 var elem = $(this);
                                 elem.remove();
                                 $(elem).prependTo("div#results");
                                 });*/
                            }
                        }


                        data.results.forEach(function (obj) {

                            movieNameAndId[obj.id] = obj.name;
                            webSocket.send(JSON.stringify(obj));

                            /*
                             //Cacheing concept must be improved
                             if (obj.id in movieRatingCache) {

                             consoleData("Downloading from cache : " + obj.name);
                             handleData(movieRatingCache[obj.id]);

                             } else {


                             }*/

                        });


                        webSocket.onmessage = function (evt) {

                            var data = JSON.parse(evt.data);

                            //Adding to cache
                            //movieRatingCache[data.id] = data;


                            handleData(data);

                        };

                        webSocket.onclose = function (evt) {
                            consoleData("Socket closed");
                            freeApp();
                            $("div#results").prepend("<p r='0' class='text-info'>SOCKET Closed</p>\n");
                        };
                        webSocket.onerror = function (evt) {
                            freeApp();
                            $("div#results").prepend("<p r='0' class='text-danger'>" + evt.data + "</p>\n");
                        };
                    }
                },
                error: function () {
                    hideProgress();
                    freeApp();
                    showError("Network error occured, Please check your connection! ");
                }
            });

        }

    });

    $('div#results').on('click', 'p.movieRow', function () {
        var id = $(this).attr('id');

        //Set loading
        $("h4.modal-title").html("Loading...");
        $("div.modal-body").hide();
        $("#imgPoster").html("");

        if (id in movieCache) {

            consoleData("Data available in cache  for " + movieCache[id].name);
            showMovieDetailedDialog(movieCache[id]);

        } else {


            //Not available in cache so download
            $.ajax({
                url: imdbServletUrl,
                type: "get",
                data: {imdbId: id},
                success: function (data) {

                    movieCache[id] = data;

                    consoleData("Movie loaded " + data.name);

                    showMovieDetailedDialog(data);


                },
                error: function (xhr) {
                    $("#bDismissDialog").click();
                    consoleData("Error occurred!");
                }
            });
        }

        function showMovieDetailedDialog(data) {
            var img = $('<img  />').load(function () {
                $("#imgPoster").html("");
                $("#imgPoster").append(img);
            }).error(function () {
                consoleData("Failed to load image");
            }).attr('src', data.poster_url);

            $("b#bRating").text(data.rating);
            $("b#bGender").text(data.gender);
            $("p#pPlot").text(data.plot);

            $("h4.modal-title").text(data.name);
            $("div.modal-body").slideDown(500);
        }

    });

});