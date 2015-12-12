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

    private static final String movieNameRegEx = "(?<MovieName>.*)(?<Year>\\d{4}[^\\w])";
    private static final Pattern moviePattern = Pattern.compile(movieNameRegEx);
    private static final String rankixedFileNameRegEx = "\\d+(?:\\.\\d)?\\s#\\s(?<FileName>.*)\\s\\-\\sRankix(?<Extension>\\.\\w+)?";

    public static Pattern getRankixedPattern() {
        return rankixedPattern;
    }

    private static final Pattern rankixedPattern = Pattern.compile(rankixedFileNameRegEx);

    private static final String[] JUNK_WORDS = {
            "dvdrip",
            "xvid",
            "720",
            "720p",
            "1080",
            "1080p",
            "yify",
            "x264",
            "bluray",
            "diamond",
            "axxo",
            "((?:www\\s)?(?:.*)\\s(?:com|net|in|mobi)\\s)"
    };

    private static String junkWordRegEx;

    public static String getMovieNameFromFile(File file) {

        String fileName = file.getName();
        fileName = getClearedRandixName(fileName);

        if(file.isFile()&& fileName.contains(".")){
            fileName = fileName.substring(0,fileName.lastIndexOf('.'));
        }

        fileName = fileName.replaceAll("\\W+", " ");
        fileName = fileName.toLowerCase();

        if(junkWordRegEx==null){
            junkWordRegEx = getJunkWordRegEx();
        }

        fileName = fileName.replaceAll(junkWordRegEx,"");

        final Matcher movieNameMatcher = moviePattern.matcher(fileName);

        //Checking if the the filename has proper movieName and year
        if (movieNameMatcher.find()) {
            final String movieName = movieNameMatcher.group("MovieName");
            final String year = movieNameMatcher.group("Year");
            fileName = movieName + year;
        }

        fileName = fileName.replaceAll("(\\s{2,}|\\s+$|^\\s)","");

        return fileName;
    }

    public static String getClearedRandixName(String fileName) {
        //Checking if the file already manager
        final Matcher rankixedMatcher = rankixedPattern.matcher(fileName);
        //If old data exist - removing old data
        if (rankixedMatcher.find()) {
            fileName = rankixedMatcher.replaceFirst("$1$2");
        }
        return fileName;
    }

    public static boolean isValidDirectory(String path) {
        File f = new File(path);
        return f.exists() && f.isDirectory();
    }

    public static String getJunkWordRegEx() {
        StringBuilder sb = new StringBuilder("(");
        for(int i=0;i<JUNK_WORDS.length;i++){
            sb.append(JUNK_WORDS[i]+"|");
        }
        sb = sb.deleteCharAt(sb.length()-1);
        sb.append(")");
        return sb.toString();
    }

    public static boolean isRankixed(String name) {
        //Checking if the file already manager
        final Matcher rankixedMatcher = rankixedPattern.matcher(name);
        //If old data exist - removing old data
        return rankixedMatcher.matches();
    }
}