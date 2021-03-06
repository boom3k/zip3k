package boom3k; /**
 * Zip4j documentation
 * http://javadox.com/net.lingala.zip4j/zip4j/1.3.1/net/lingala/zip4j/core/ZipFile.html
 */

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.*;
import java.util.*;

public class Zip3k {

    static final String EXTENSION = ".zip";

    /**
     * @param zipFileName Path where the zipped files should be placed
     * @param password    Password that will be used to encrypt the files
     * @param files       List of file objects to zip
     */
    public static void zip(String zipFileName, List<File> files, String password) throws ZipException {
        final String EXTENSION = "zip";
        ZipParameters zipParameters = setZipParameters(password);
        String baseFileName = new File(zipFileName).getName();
        String destinationZipFilePath = baseFileName + "." + EXTENSION;

        ZipFile zipFile = new ZipFile(destinationZipFilePath);
        files.forEach(file -> {
            try {
                zipFile.addFile(file, zipParameters);
            } catch (ZipException e) {
                e.printStackTrace();
            }
        });
    }

    public static void zip(File file, String password) throws ZipException {

        zip(file.getPath(), new ArrayList<>(Collections.singleton(file)), password);
    }

    public static File getFileFromZip(String zipFileName, String password, String targetFileName) throws ZipException, IOException {
        InputStream is = getInputStreamFromZip(zipFileName, targetFileName, password);
        File file = new File("");
        OutputStream os = new FileOutputStream(file);
        int read;
        byte[] bytes = new byte[1024];
        while ((read = is.read(bytes)) != -1){
            os.write(bytes,0,read);
        }
        return file;
    }

    /**
     * @param sourceZipFilePath Path of the zip File to unzip
     * @param password          Password that will decrypt the source Zip file
     * @return A ZipFile object that is used by the Zip4j dependency
     */
    public static ZipFile getZipFile(String sourceZipFilePath, String password) throws ZipException {
        final String EXTENSION = ".zip";

        if (!sourceZipFilePath.contains(EXTENSION)) {
            sourceZipFilePath += EXTENSION;
        }

        ZipFile zipFile = new ZipFile(sourceZipFilePath);
        if (zipFile.isEncrypted()) {
            zipFile.setPassword(password);
        }

        return zipFile;

    }

    /**
     * @param sourceZipFilePath Path of the zip File to unzip
     * @param password          Password that will decrypt the source Zip file
     * @return HashMap, Key:String(FileName), Value:BufferedReader(Data used for Input/Output streams)
     */
    public static Map<String, InputStream> getAllZippedFiles(String sourceZipFilePath, String password) throws ZipException {
        Map<String, InputStream> readerMap = new HashMap<>();
        ZipFile zipFile = getZipFile(sourceZipFilePath, password);
        List<FileHeader> fileHeaderList = zipFile.getFileHeaders();

        fileHeaderList.forEach(fileHeader -> {
            try {
                readerMap.put(fileHeader.getFileName(),
                        getInputStreamFromZip(sourceZipFilePath, fileHeader.getFileName(), password));
            } catch (ZipException e) {
                e.printStackTrace();
            }
        });

        return readerMap;
    }

    /**
     * @param sourceZipFilePath Path of the zip File to unzip
     * @param fileName          Name of the file to get data from
     * @param password          Password that will decrypt the source Zip file
     * @return BufferedReader
     */
    public static BufferedReader getBufferedReaderFromZip(String sourceZipFilePath, String fileName, String password) throws ZipException, IOException {
        return new BufferedReader(getInputStreamReaderFromZip(sourceZipFilePath, fileName, password));
    }

    /**
     * @param sourceZipFilePath Path of the zip File to unzip
     * @param fileName          Name of the file to get data from
     * @param password          Password that will decrypt the source Zip file
     * @return InputStreamReader
     */
    public static InputStreamReader getInputStreamReaderFromZip(String sourceZipFilePath, String fileName, String password) throws ZipException, IOException {
        return new InputStreamReader(new ByteArrayInputStream(getByteArrayFromZip(sourceZipFilePath, fileName, password)));
    }

    /**
     * @param sourceZipFilePath Path of the zip File to unzip
     * @param fileName          Name of the file to get data from
     * @param password          Password that will decrypt the source Zip file
     * @return byte[]
     */
    public static byte[] getByteArrayFromZip(String sourceZipFilePath, String fileName, String password) throws IOException, ZipException {
        //ZipInputStream extends a type java.io.inputStream that will hold the fileHeader dataStream
        ZipInputStream is = (ZipInputStream) getInputStreamFromZip(sourceZipFilePath, fileName, password);

        //OutputStream extents object that will hold the byteArray
        OutputStream os = new ByteArrayOutputStream();

        //Integer of total bytes read
        int bytesRead;
        //byte Array size for reading
        byte[] buffer = new byte[4096];

        //While while the total bytes read from the inputstream is not equal to -1 (or none) write to the outputStream
        while ((bytesRead = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }

        //Close inputStream for memory leaks
        is.close();

        //Send os (outputStream) to a byteArray object
        byte[] uncompressedBytes = ((ByteArrayOutputStream) os).toByteArray();
        //Close outputStream for memory leaks
        os.close();

        return uncompressedBytes;
    }

    /**
     * @param sourceZipFilePath Path of the zip File to unzip
     * @param fileName          Name of the file to get data from
     * @param password          Password that will decrypt the source Zip file
     * @return inputStream
     */
    public static InputStream getInputStreamFromZip(String sourceZipFilePath, String fileName, String password) throws ZipException {
        //Get the zip file
        ZipFile zipFile = getZipFile(sourceZipFilePath, password);

        //FileHeader extends a type Object so it can hold the file from the zip
        FileHeader fileHeader = zipFile.getFileHeader(fileName);

        //ZipInputStream extends a type java.io.inputStream that will hold the fileHeader dataStream
        return zipFile.getInputStream(fileHeader);
    }

    /**
     * @param sourceZipFilePath    Path of the zip File to unzip
     * @param extractedZipFilePath Path where the unzipped files should be placed
     * @param password             Password that will decrypt the source Zip file
     */
    public static void unzipFile(String sourceZipFilePath, String extractedZipFilePath, String password) throws ZipException {
        ZipFile zipFile = new ZipFile(sourceZipFilePath + EXTENSION);

        if (zipFile.isEncrypted()) {
            zipFile.setPassword(password);
        }

        zipFile.extractAll(extractedZipFilePath);
    }

    public static void extractFileFromZip(String sourceZipFilePath, String fullFileName, String password, String extractedFilePath) throws ZipException {
        ZipFile zipFile = new ZipFile(sourceZipFilePath + EXTENSION);
        if (zipFile.isEncrypted()) {
            zipFile.setPassword(password);
        }
        zipFile.extractFile(fullFileName, extractedFilePath);
    }

    public static void insertFileToZip(String targetZipFolderPath, File file, String password) throws ZipException {
        getZipFile(targetZipFolderPath, password).addFile(file, setZipParameters(password));
    }

    public static void insertFilesToZip(String targetZipFolderPath, List<File> files, String password) {
        files.forEach(file -> {
            try {
                insertFileToZip(targetZipFolderPath, file, password);
            } catch (ZipException e) {
                e.printStackTrace();
            }
        });
    }

    static ZipParameters setZipParameters(String password) {
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        zipParameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);
        zipParameters.setEncryptFiles(true);
        zipParameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
        zipParameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
        zipParameters.setPassword(password);
        return zipParameters;
    }
}
