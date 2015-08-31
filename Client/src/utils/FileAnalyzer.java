package utils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shifar Shifz on 8/16/2015.
 */
public class FileAnalyzer {

    private static final String[] videoExts = "webm,mkv,flv,vob,ogv,ogg,drc,mng,avi,mov,qt,wmv,rm,rmvb,asf,mp4,m4p,m4v,mpg,mp2,mpeg,mpe,mpv,mpg,mpeg,m2v,m4v,svi,3gp,3g2,mxf,roq,nsv".split(",");

    public static boolean isVideoFile(final File file) {
        final String fileName = file.getName();
        if (file.isFile()) {
            final String fileExt = fileName.substring(fileName.lastIndexOf('.')+1, fileName.length());
            for (final String okExt : videoExts) {
                if (okExt.equals(fileExt)) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    private static final String movieNameRegEx = "^([\\w ]+)(\\d{4})\\s+";
    private static final Pattern moviePattern = Pattern.compile(movieNameRegEx);
    private static final String rankixedFileNameRegEx = "([\\d]\\.[\\d]\\s#\\s)(.+)(\\s[\\-]\\sRankix)(\\.\\w+)?";

    public static Pattern getRankixedPattern() {
        return rankixedPattern;
    }

    private static final Pattern rankixedPattern = Pattern.compile(rankixedFileNameRegEx);

    public static String getMovieNameFromFile(File file) {

        String fileName = file.getName();
        fileName = clearRandixName(fileName);

        if(file.isFile()){

            if (fileName.contains(".")) {
                fileName = fileName.substring(0, fileName.lastIndexOf('.')) + " ";
            }
        }

        //Removing junk symbols and junk texts
        fileName = fileName.replaceAll("(\\.|_)", " ");
        fileName = fileName.replaceAll("(720p|720|1080p|1080|\\(|\\)|\\[|\\])", "");

        final Matcher movieNameMatcher = moviePattern.matcher(fileName);

        //Checking if the the filename has proper movieName and year
        if (movieNameMatcher.find()) {
            final String movieName = movieNameMatcher.group(1);
            final String year = movieNameMatcher.group(2);
            fileName = movieName + year;
        }

        return fileName;
    }

    public static String clearRandixName(String fileName) {
        //Checking if the file already manager
        final Matcher rankixedMatcher = rankixedPattern.matcher(fileName);
        //If old data exist - removing old data
        if (rankixedMatcher.find()) {
            fileName = rankixedMatcher.replaceFirst("$2$4");
        }
        return fileName;
    }

    public static boolean isValidDirectory(String path) {
        File f = new File(path);
        return f.exists() && f.isDirectory();
    }
}