import boom3k.Zip3k;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.lingala.zip4j.exception.ZipException;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;


public class Tests {
    @Test
    public void zipperTest() throws ZipException {
        String sourceZipFilePath = "encryptedFile";
        String password = "deezinyoutees";
        Map<String, InputStream> inputStreamMap = Zip3k.getAllZippedFiles(sourceZipFilePath, password);
        inputStreamMap.keySet().forEach(key ->{
            System.out.println("Filename: " + key);
        });
        JsonObject encryptedFile = (JsonObject) new JsonParser().parse(new InputStreamReader(inputStreamMap.get("encryptedFile.json")));
        System.out.println(encryptedFile.get("type").toString().replace("\"",""));
    }
}
