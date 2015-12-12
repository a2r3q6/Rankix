import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by user on 8/31/2015.
 */
public class DataCollector {
    public static void main(String[] args) throws IOException {
        File dir = new File(System.getProperty("user.dir"));
        File dummyDir = new File("dummyDir");

        if(dummyDir.mkdirs()){
            File[] filesAndFolders = dir.listFiles();
            if(filesAndFolders!=null){
                for(File fileOrFolder:filesAndFolders){
                    if(fileOrFolder.getName().equals("dummyDir")){
                        continue;
                    }
                    File dummyFileOrFolder = new File(dir.getAbsolutePath()+"/dummyDir/"+fileOrFolder.getName());
                    if(fileOrFolder.isDirectory()){
                        System.out.println(fileOrFolder.getName()+" is a directory");
                        if(dummyFileOrFolder.mkdirs()){
                            System.out.println("Created dummy path DummyPath : "+dummyFileOrFolder.getAbsolutePath());
                        }else{
                            System.out.println("Failed to create DummyPath : "+dummyFileOrFolder.getAbsolutePath());
                        }
                    }else if(fileOrFolder.isFile()){
                        System.out.println(fileOrFolder.getName()+" is a file");
                        if(dummyFileOrFolder.createNewFile()){
                            System.out.println("Created DummyFile : "+dummyFileOrFolder.getAbsolutePath());
                        }else{
                            System.out.println("Failed to create DummyFile : "+dummyFileOrFolder.getAbsolutePath());
                        }

                    }else{
                        System.out.println("This shouldn't happn");
                    }
                }
            }else{
                System.out.println("FilesAndFolder is null");
            }
        }else{
            System.out.println("Failed to create dummyDir");
        }
    }

}
