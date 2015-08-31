import java.io.File;

/**
 * Created by user on 8/31/2015.
 */
public class DataCollector2 {
    public static void main(String[] args){
        for(File f : new File("DummyData").listFiles()){
            System.out.println("--------------------------------");
            System.out.println(f.getName());
            System.out.println("--------------------------------");

        }
    }
}
