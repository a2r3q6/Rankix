import com.neovisionaries.ws.client.*;
import models.Movie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utils.FileAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;

/**
 * Created by Shifar Shifz on 8/28/2015.
 */
public class Main {


    private static final String LOCAL_SOCKET = "ws://localhost:8080/RankixSocket";
    private static final String REMOTE_SOCKET = "ws://shifar-shifz.rhcloud.com:8000/Rankix/RankixSocket";
    private static Map<Integer, Movie> movieFileList;

    private static final String ERROR = "error";
    private static final String DATA = "data";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String FLAG_RANK = "-r";
    private static final String FLAG_UNDO = "-u";
    private static int ratingsReceived;
    private static int requestSent;
    private static int resultReceived;

    public static void main(String[] args) {


        /*if(true){
            for(File f : new File("DummyData").listFiles()){
                if(FileAnalyzer.isVideoFile(f)){
                    System.out.println("-------------------------");
                    System.out.println(f.getName());
                    System.out.println(FileAnalyzer.getMovieNameFromFile(f));
                    System.out.println("-------------------------");
                }
            }
            return;
        }*/

        //All command must contain two parts
        //(flag)(value)
        if (args.length == 2) {

            final String FLAG = args[0];
            final String VALUE = args[1];

            switch (FLAG) {

                //RANKIX
                case FLAG_RANK:
                    System.out.println("Rankix Rank Request Accepted");
                    String path = VALUE;
                    if (path.equals(".")) {
                        path = System.getProperty("user.dir");
                    }
                    if (FileAnalyzer.isValidDirectory(path)) {
                        try {
                            startRankix(path);
                        } catch (NoMovieFoundException e) {
                            System.out.println("No movie found under " + path);
                        } catch (Exception e) {
                            System.out.println("Network error occurred, Please check your connection");
                        }
                    } else {
                        System.out.println(path + " is an invalid directory");
                    }
                    break;

                //ROLLBACK
                case FLAG_UNDO:

                    String path2 = VALUE;
                    if (path2.equals(".")) {
                        path2 = System.getProperty("user.dir");
                    }
                    if (FileAnalyzer.isValidDirectory(path2)) {
                        try {
                            rollBack(path2);
                        } catch (Exception e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                    } else {
                        System.out.println(path2 + " is an invalid directory");
                    }
                    break;

                default:
                    System.out.println("Invalid flag passed");
                    break;

            }


        } else {

            String path = null;
            boolean isValidPath = false;
            Scanner scanner = new Scanner(System.in);

            do {
                System.out.print("Enter the path you want to Rankixed: ");

                path = scanner.nextLine();
                if (path != null) {
                    if (path.isEmpty()) {
                        System.out.println("Folder path must be specified");
                    } else if (path.equals(".")) {
                        path = System.getProperty("user.dir");
                        isValidPath = true;
                    } else {
                        if (FileAnalyzer.isValidDirectory(path)) {
                            isValidPath = true;
                        }else{
                            System.out.println(path+" is an invalid directory!");
                        }

                    }
                } else {
                    System.out.println("Folder path must be specified");
                }

            } while (!isValidPath);

            try {
                startRankix(path);
            } catch (NoMovieFoundException e) {
                System.out.println("No movie found under " + path);
            } catch (Exception e) {
                System.out.println("Network error occured, please check your connection");
            }
        }
    }

    private static void read(String s) {
        if(true){
            for(File f : new File(s).listFiles()){
                if(f.isDirectory()){
                    read(f.getAbsolutePath());
                }
                System.out.println(f.getAbsolutePath());
            }
        }
    }


    private static void startRankix(String path) throws JSONException, IOException, WebSocketException, NoMovieFoundException {
        ratingsReceived = 0;
        requestSent = 0;
        resultReceived = 0;

        System.out.println("Starting ranking in " + path);

        movieFileList = new HashMap<>();

        final File[] movieFiles = new File(path).listFiles();
        if (movieFiles != null) {

            int alreadyRankixedFile = 0;
            final int totalFiles = movieFiles.length;
            for (int i = 0; i < totalFiles; i++) {
                final File movieFile = movieFiles[i];
                if (FileAnalyzer.isVideoFile(movieFile)) {

                    if(FileAnalyzer.isRankixed(movieFile.getName())){
                        alreadyRankixedFile++;
                    }else{
                        final Movie movie = new Movie(i, movieFile);
                        movieFileList.put(i, movie);
                    }

                }
            }

            System.out.println("--------------------------------------");
            System.out.println("Files found : "+totalFiles );
            System.out.println("Already Rankixed Files : "+alreadyRankixedFile);
            System.out.println("UnRankixed Files : "+movieFileList.size());
            final int otherThanMovieFiles = totalFiles - (movieFileList.size() + alreadyRankixedFile);
            System.out.println("Other than movieFiles: "+otherThanMovieFiles);
            System.out.println("--------------------------------------");


            if (movieFileList.size() == 0) {
                throw new NoMovieFoundException(path);
            }

        } else {
            System.out.println("Something went wrong, Try again");
        }


        final JSONArray jaMovieNameId = new JSONArray();

        //Preparing JSONObject for Socket
        for (Map.Entry<Integer, Movie> entry : movieFileList.entrySet()) {

            final Movie movie = movieFileList.get(entry.getKey());
            final JSONObject jMovieNameId = new JSONObject();

            //Putting movie id which is file position
            jMovieNameId.put(ID, entry.getKey());

            String movieName = FileAnalyzer.getMovieNameFromFile(movie.getFile());

            //Final filtered moviename adding jsonArray (payload)
            jMovieNameId.put(NAME, movieName);
            movie.setFilteredMovieName(movieName);
            jaMovieNameId.put(jMovieNameId);
        }


        System.out.println("CONNECTING TO RANKIX SOCKET...");


        ratingsReceived = 0;

        new WebSocketFactory()
                .createSocket(REMOTE_SOCKET)
                .addListener(new WebSocketAdapter() {
                    public void onTextMessage(WebSocket websocket, String jsonData) {
                        try {
                            final JSONObject jData = new JSONObject(jsonData);
                            final boolean hasError = jData.getBoolean(ERROR);
                            final String data = jData.getString(DATA);
                            if (hasError) {
                                System.out.println("RankixSocket -> " + data);
                            } else {
                                //Final response
                                final int id = jData.getInt(ID);
                                Movie ratedMovie = movieFileList.get(id);
                                ratedMovie.setRating(data);
                                ratedMovie.setRankixedName(data);
                                System.out.println(String.format("<- %s has %s/10", ratedMovie.getFilteredMovieName(), data));
                                renameFile(ratedMovie);
                                ratingsReceived++;
                            }
                            showProgress(++resultReceived);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println("Error: " + e.getMessage());
                        }

                        if (requestSent == resultReceived) {
                            System.out.println("Requesting to close socket");
                            websocket.sendClose();
                        }

                    }

                    @Override
                    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
                        System.out.println("CONNECTED TO RANKIX SOCKET");
                        //Starting data transmission

                        for (int i = 0; i < jaMovieNameId.length(); i++) {
                            try {
                                final String movieName = jaMovieNameId.getJSONObject(i).toString();
                                websocket.sendText(movieName);
                                requestSent++;
                            } catch (JSONException e) {
                                e.printStackTrace();
                                System.out.println("Error: " + e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) {
                        System.out.println("RANKIX SOCKET CLOSED");
                        final int prog = (ratingsReceived) * 100 / requestSent;
                        System.out.println(String.format("%d%% ratings found", prog));
                    }
                })
                .connect();
    }

    private static void showProgress(int ratingsReceivedCount) {
        final int perc = (ratingsReceivedCount + 1) * 100 / requestSent;
        System.out.print(String.format("Progress: %d%%\r", perc));
    }


    private static void rollBack(String path) {
        File[] files = new File(path).listFiles();
        if (files != null) {
            for (File f : files) {
                if (FileAnalyzer.isVideoFile(f) && FileAnalyzer.isRankixed(f.getName())) {
                    final File destFile = new File(f.getParentFile().getAbsolutePath() + "/" + FileAnalyzer.getClearedRandixName(f.getName()));
                    if (!f.renameTo(destFile)) {
                        System.out.println("DestFile: "+destFile.getAbsolutePath());
                        System.out.println(f.getName() + " failed to rollback");
                    }
                }
            }

            System.out.println("Rollback finished");

        }else{
            System.out.println("ROLLBACK FAILED");
        }


    }

    private static void renameFile(Movie ratedMovie) {

        String fileName = ratedMovie.getFileName();

        //Checking if the file already manager
        final Matcher rankixedMatcher = FileAnalyzer.getRankixedPattern().matcher(fileName);

        //If old data exist - removing old data
        if (rankixedMatcher.find()) {
            fileName = rankixedMatcher.replaceFirst("$2$4");
            if (fileName.contains(".")) {
                final int lastDotIndex = fileName.lastIndexOf('.');
                String fileNameWithOutExt = fileName.substring(0, lastDotIndex);
                String fileExtension = fileName.substring(lastDotIndex, fileName.length());
                ratedMovie.setRankixedName(ratedMovie.getRating(), null, fileNameWithOutExt, fileExtension);
            } else {
                ratedMovie.setRankixedName(ratedMovie.getRating(), fileName, null, null);
            }
        }

        File movieFile = ratedMovie.getFile();

        try {
            final File renamedFile = new File(ratedMovie.getRankixedName());
            if (!movieFile.renameTo(renamedFile)) {
                System.out.println(renamedFile.getName() + " failed to rename !");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static final String[] testFileNames = {
            "The.Expendables.2.2012.1080.BluRay.x264.YIFY.mp4",
            "Johnny English 2003 720p HDTVRip H264 AAC-Rx(Phoenix-RG).mp4",
            "Johnny English Reborn [2011].avi",
            "Lets.Be.Cops.2014.720p.BluRay.x264.YIFY.mp4",
            "R3z1d3nt.3v1l.Ap0calps3.hdm0v13sp01nt.c0m.mp4",
            "3Resident Evil Extinction.mp4",
            "R3z1d3nt.3v1l.Aft3rl1f3.hdm0v13sp01nt.c0m.mp4",
            "Night.at.the.Museum.Secret.of.the.Tomb.2014.1080p.BluRay.x264.YIFY.mp4",
            "The Social Network (2010) 720p BrRip x264 - 700MB - YIFY.mp4",
            "The.Gold.Rush1925.720p.BrRip.x264.YIFY.mp4",
            "A Movie Folder 2010",
            "Another Movie Folder"
    };

    private static void createTestData() throws IOException {
        File f = new File("Movies");
        f.mkdirs();
        for (String fileName : testFileNames) {
            if (fileName.contains(".")) {
                new File("Movies/" + fileName).createNewFile();
            } else {
                new File("Movies/" + fileName).mkdirs();
            }
        }
    }

    static class NoMovieFoundException extends Exception {
        public NoMovieFoundException(String path) {
            super("No movie found under " + path);
        }
    }


}
