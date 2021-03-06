package p7gruppe.p7.offloading.data.local;

import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Service
public class JobFileManager {

    @Autowired
    PathResolver pathResolver;

    private static final String JOB_FILE_NAME = "job_file.zip";
    private static final String RESULT_FILE_NAME = "result_file.zip";
    private static final String INTERMEDIATE_RESULT_FILE_NAME = "result_file_";

    // Saves a job to a directory that is generated from the given username
    // Returns the directory where job files are located
    public String saveJob(String username, byte[] fileBytes) throws IOException {
        String directoryPath = pathResolver.generateNewJobFolder(username);
        File f = new File(directoryPath + File.separator + JOB_FILE_NAME);
        FileUtils.writeByteArrayToFile(f, fileBytes);
        return directoryPath;
    }

    public void saveFinalResultFromSpecificAssignment(long assignmentId, String jobPath){
        String resultDirectoryPath = pathResolver.generateNewResultFolder(jobPath);
        File f = new File(resultDirectoryPath + File.separator + INTERMEDIATE_RESULT_FILE_NAME + assignmentId + ".zip");

        File finalResultFile = new File(jobPath + File.separator + RESULT_FILE_NAME);

        try {
            FileUtils.copyFile(f, finalResultFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String saveResult(String path, byte[] fileBytes, long assignmentId, boolean testAssignment) throws IOException {
        String resultDirectoryPath = pathResolver.generateNewResultFolder(path);
        String resultpath = testAssignment
                ? resultDirectoryPath + File.separator + INTERMEDIATE_RESULT_FILE_NAME + assignmentId + "_testAssig" + ".zip"
                : resultDirectoryPath + File.separator + INTERMEDIATE_RESULT_FILE_NAME + assignmentId + ".zip";
        File f = new File(resultpath);
        FileUtils.writeByteArrayToFile(f, fileBytes);
        return resultDirectoryPath;
    }

    public void saveFinalResultFromIntermediateWithConfidence(String pathToBestFile, String jobPath){
        String resultDirectoryPath = pathResolver.generateNewResultFolder(jobPath);
        File bestFile = new File(pathToBestFile);

        File finalResultFile = new File(jobPath + File.separator + RESULT_FILE_NAME);

        try {
            FileUtils.copyFile(bestFile, finalResultFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFinalResultFromIntermediate(String jobPath) {
        String resultDirectoryPath = pathResolver.generateNewResultFolder(jobPath);
        File f = new File(resultDirectoryPath + File.separator);
        // Get first file, that is not a test assignment file
        File firstResult = (File) Arrays.stream(f.listFiles()).filter(it -> !it.getAbsolutePath().endsWith("_testAssig.zip")).toArray()[0];

        File finalResultFile = new File(jobPath + File.separator + RESULT_FILE_NAME);
        try {
            FileUtils.copyFile(firstResult, finalResultFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getResultFile(String jobDirectoryPath){
        File file = new File(jobDirectoryPath + File.separator + RESULT_FILE_NAME);
        if (!file.exists()) throw new RuntimeException("Job file does not exist : " + jobDirectoryPath);
        return file;
    }

    public File getJobFile(String jobDirectoryPath){
        File file = new File(jobDirectoryPath + JOB_FILE_NAME);
        if (!file.exists()) throw new RuntimeException("Job file does not exist : " + jobDirectoryPath);
        return file;
    }

    public void deleteDirectory(String jobDirectoryPath) throws IOException {
        File file = new File(jobDirectoryPath);
        for (File subFile : file.listFiles()) {
            if (subFile.isDirectory()) {
                deleteDirectory(subFile.getPath());
            } else {
                subFile.delete();
            }
        }
        file.delete();
    }

    public static byte[] encodeJobBytes(byte[] fileBytes){
        return Base64.encodeBase64(fileBytes);
    }

    public static byte[] decodeFromBase64(byte[] fileBytes){
        return Base64.decodeBase64(fileBytes);
    }

}
