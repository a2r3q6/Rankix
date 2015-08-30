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
    private static final String MESSAGE = "message";
    private static final String TYPE = "type";
    private static final String TYPE_PERC = "perc";
    private static final String DATA = "data";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String RATING = "rating";
    private static final String TYPE_MSG = "msg";
    private static final String FLAG_RANK = "-r";
    private static final String FLAG_UNDO = "-u";
    private static WebSocket ws;

    public static void main(String[] args) {

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
                            System.out.println("No movie found under "+path);
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
            do{
                System.out.print("Enter the path you want to Rankixed: ");
                path = scanner.nextLine();
                if(path!=null){
                    if(path.isEmpty()){
                        System.out.println("Folder path must be specified");
                    }else if(path.equals(".")){
                        path = System.getProperty("user.dir");
                        isValidPath = true;
                    }else{
                        if(FileAnalyzer.isValidDirectory(path)){
                            isValidPath = true;
                        }
                    }
                }else{
                    System.out.println("Folder path must be specified");
                }

            }while(!isValidPath);

            try {
                startRankix(path);
            } catch (NoMovieFoundException e) {
                System.out.println("No movie found under "+path);
            } catch (Exception e){
                System.out.println("Network error occured, please check your connection");
            }
        }
    }


    private static void startRankix(String path) throws JSONException, IOException, WebSocketException, NoMovieFoundException {

        System.out.println("Starting ranking in "+path);

        movieFileList = new HashMap<>();

        final File[] movieFiles = new File(path).listFiles();
        if (movieFiles != null) {

            for (int i = 0; i < movieFiles.length; i++) {
                final File movieFile = movieFiles[i];
                if (FileAnalyzer.isVideoFile(movieFile)) {
                    final Movie movie = new Movie(i, movieFile);
                    movieFileList.put(i, movie);
                }
            }

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

            String fileName = FileAnalyzer.getMovieNameFromFileName(movie.getFileName());

            //Final filtered moviename adding jsonArray (payload)
            jMovieNameId.put(NAME, fileName);
            jaMovieNameId.put(jMovieNameId);
        }


        System.out.println("Connecting to RankixSocket, This may take some time.");

        ws = new WebSocketFactory()
                .createSocket(LOCAL_SOCKET)
                .addListener(new WebSocketAdapter() {
                    public void onTextMessage(WebSocket websocket, String jsonData) {

                        try {
                            final JSONObject jData = new JSONObject(jsonData);
                            final boolean hasError = jData.getBoolean(ERROR);
                            if (hasError) {
                                final String errorReason = jData.getString(MESSAGE);
                                System.out.println("Error : " + errorReason);
                                System.out.println("RankixSocket Closed");
                            } else {

                                //Valid response
                                final String dataType = jData.getString(TYPE);
                                final String data = jData.getString(DATA);

                                //Checking if the message is about progress
                                switch (dataType) {
                                    case TYPE_PERC:
                                        System.out.print("Progress: " + data + "%\r");
                                        break;
                                    case TYPE_MSG:
                                        System.out.println("RankixSocket : " + data);
                                        break;
                                    case RATING:

                                        //Final response
                                        final int id = jData.getInt(ID);
                                        Movie ratedMovie = movieFileList.get(id);
                                        ratedMovie.setRating(data);
                                        ratedMovie.setRankixedName(data);
                                        renameFile(ratedMovie);

                                        break;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println(jsonData);
                        }

                    }

                    @Override
                    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
                        System.out.println("Connected to RankixSocket");
                        //Sending all film name
                        websocket.sendText(jaMovieNameId.toString());
                    }

                    @Override
                    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) {
                        System.out.println("RANKIX SOCKET DISCONNECTED");
                        System.exit(0);
                    }
                })
                .connect();


    }

    private static void rollBack(String path) {
        File[] files = new File(path).listFiles();
        if (files != null) {
            for (File f : files) {
                final File destFile = new File(f.getParentFile().getAbsolutePath() + "/" + FileAnalyzer.clearRandixName(f.getName()));
                if (!f.renameTo(destFile)) {
                    System.out.println(f.getName() + " failed to rollback");
                }
            }
        }

        System.out.println("Rollback success");
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
                System.out.println("File rename failed!");
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
