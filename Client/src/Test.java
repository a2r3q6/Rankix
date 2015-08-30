import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shifar Shifz on 8/29/2015.
 */
public class Test {
    public static void main(String[] args){
        final String regEx = "^([\\w ]+)(\\d{4}(?:(?!MB)))";
        final Pattern pattern = Pattern.compile(regEx);
        final String text = "Johnny English Reborn 2011CB ";
        final Matcher matcher = pattern.matcher(text);
        System.out.println(matcher.find());
    }
}
