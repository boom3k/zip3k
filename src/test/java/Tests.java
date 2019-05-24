import boom3k.zip3kUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.lingala.zip4j.exception.ZipException;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class Tests {
    @Test
    public void zipperTest() throws ZipException {
        String sourceZipFilePath = "encryptedFile";
        String password = "deezinyoutees";
        Map<String, InputStream> inputStreamMap = zip3kUtil.getAllInputStreamsInSize(sourceZipFilePath, password);
        JsonObject encryptedFile = (JsonObject) new JsonParser().parse(new InputStreamReader(inputStreamMap.get("encryptedFile.json")));
        System.out.println(encryptedFile.get("type").toString().replace("\"",""));
    }
}
