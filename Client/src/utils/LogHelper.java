package utils;

import models.Movie;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.*;
import java.util.Map;

/**
 * Created by Shifar Shifz on 8/29/2015.
 */
public class LogHelper {
    private static final String LOG_FILE_NAME = System.getProperty("file.separator") + ".Rankix.log";

    /**
     *
     * @param rootPath
     * @param movieFileList
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public static Map<Integer, Movie> compareLog(String rootPath,Map<Integer, Movie> movieFileList) throws IOException, JSONException {

        final File logFile = new File(rootPath+LOG_FILE_NAME);
        if(logFile.exists()){
            //The directory already managed, so remove managed filenames
            final BufferedReader br = new BufferedReader(new FileReader(logFile));
            final StringBuilder logBuilder = new StringBuilder("[");
            String line  = null;
            while((line = br.readLine())!=null){
                logBuilder.append(line);
            }
            logBuilder.append("]");
            br.close();
            final JSONArray jaLog = new JSONArray(logBuilder.toString());
            for(int i=0;i<jaLog.length();i++){
                //Removing already logged file
                final String fileName = jaLog.getString(i);
                for(Map.Entry<Integer, Movie> entry:movieFileList.entrySet()){
                    String newFileName = entry.getValue().getFileName();
                    System.out.println("f1: "+fileName+"\nf2: "+newFileName);
                    if(newFileName.equals(fileName)){
                        //The file already managed
                        System.out.println("Skipping : "+fileName);
                        final int newFileKey = entry.getKey();
                        movieFileList.remove(newFileKey);
                    }
                }
            }
        }
        return movieFileList;
    }

    public static void saveLog(String path, String jsonData) throws IOException {
        File f = new File(path+LOG_FILE_NAME);
        if(!f.exists()){
            f.createNewFile();
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        jsonData = jsonData.substring(1,jsonData.length()-1);
        bw.write(jsonData);
        bw.flush();
        bw.close();
    }
}
