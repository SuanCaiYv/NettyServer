import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * @author SuanCaiYv
 * @time 2020/2/13 下午2:14
 */
public class Main
{
    public static void main(String[] args) throws UnsupportedEncodingException
    {
        File file = new File("template/index.html");
        System.out.println(file.length());
        System.out.println("run1");
    }
}
