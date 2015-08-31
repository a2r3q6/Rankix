package models;

import java.io.File;

/**
 * Created by Shifar Shifz on 8/29/2015.
 */
public class Movie {


    private final int id;
    private final File file;

    private final String fileName, filePath;
    private String fileExtension,fileNameWithOutExt;
    private String rating,rankixedName;
    private String filteredMovieName;

    public Movie(int id, File file) {

        this.id = id;
        this.file = file;
        this.fileName = file.getName();
        this.filePath = file.getParentFile().getAbsolutePath();

        //Checking if the file is not a directory and contains extension
        if (!file.isDirectory() && fileName.contains(".")) {
            final int lastDotIndex = this.fileName.lastIndexOf('.');
            this.fileExtension = this.fileName.substring(lastDotIndex,this.fileName.length());
            this.fileNameWithOutExt = this.fileName.substring(0,lastDotIndex);
        }else{
            this.fileNameWithOutExt = this.fileName;
        }
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", file=" + file +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileExtension='" + fileExtension + '\'' +
                ", fileNameWithOutExt='" + fileNameWithOutExt + '\'' +
                ", rating='" + rating + '\'' +
                ", rankixedName='" + rankixedName + '\'' +
                '}';
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    private static final String RANKIXED_FILE_NAME_FORMAT = "%s%s%s # %s - Rankix%s";
    private static final String RANKIXED_FOLDER_NAME_FORMAT = "%s%s%s # %s - Rankix";

    /**
     * Setting new filename
     * @param rating
     */
    public void setRankixedName(String rating){
        if(file.isDirectory()){
            this.rankixedName  = String.format(RANKIXED_FOLDER_NAME_FORMAT,filePath,System.getProperty("file.separator"),rating,fileName);
        }else{
            this.rankixedName  = String.format(RANKIXED_FILE_NAME_FORMAT,filePath,System.getProperty("file.separator"),rating,fileNameWithOutExt,fileExtension);
        }
    }

    /**
     * Setting new filename
     * @param rating
     */
    public void setRankixedName(String rating,String fileName,String fileNameWithOutExt,String fileExtension){
        if(fileName!=null){
            this.rankixedName  = String.format(RANKIXED_FOLDER_NAME_FORMAT,filePath,System.getProperty("file.separator"),rating,fileName);
        }else if(fileNameWithOutExt!=null && fileExtension!=null){
            this.rankixedName  = String.format(RANKIXED_FILE_NAME_FORMAT,filePath,System.getProperty("file.separator"),rating,fileNameWithOutExt,fileExtension);
        }
    }

    public String getRankixedName(){
        return this.rankixedName;
    }

    public File getFile() {
        return file;
    }

    public String getRating() {
        return rating;
    }

    public void setFilteredMovieName(String filteredMovieName) {
        this.filteredMovieName = filteredMovieName;
    }

    public String getFilteredMovieName() {
        return filteredMovieName;
    }
}
