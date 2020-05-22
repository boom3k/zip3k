import boom3k.Zip3k;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;


public class Tests {
    @Test
    public void zipperTest() throws ZipException, FileNotFoundException {
        String sourceZipFilePath = "encryptedFile";
        String password = "deezinyoutees";
        String dynamicFile = "dummy.json";
        System.out.println("Removing file [" + dynamicFile + "]");
        Zip3k.getZipFile(sourceZipFilePath, password).removeFile(dynamicFile);
        System.out.println("File [" + dynamicFile + "] removed successfully\n");

        System.out.println("Inserting File [" + dynamicFile + "]");
        Zip3k.insertFileToZip(sourceZipFilePath, new File(dynamicFile), password);
        System.out.println("File [" + dynamicFile + "] inserted successfully\n");


        System.out.println("Gathering files from [" + sourceZipFilePath + "] with password [" + password + "]");
        Map<String, InputStream> inputStreamMap = Zip3k.getAllZippedFiles(sourceZipFilePath, password);
        inputStreamMap.keySet().forEach(key -> {
            JsonObject currentFile = (JsonObject) new JsonParser().parse(new InputStreamReader(inputStreamMap.get(key)));
            System.out.println(key + " contains:: " + currentFile);
        });

    }
}
