package p7gruppe.p7.offloading.fileutils;

import kotlin.Pair;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import p7gruppe.p7.offloading.api.dataclasses.ConfidenceResult;
import p7gruppe.p7.offloading.data.local.PathResolver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FileUtilsTest {

    String pathToStartingData = this.getClass().getResource("/resultComparisonStartingData").getPath();
    // String pathToStartingData = System.getProperty("user.dir") + File.separator + "test_data" + File.separator + "resultComparisonStartingData";
    String pathToWorkingDir = System.getProperty("user.dir") + File.separator + "test_data" + File.separator + "result_test";

    @Autowired
    PathResolver pathResolver;

    @AfterEach
    public void cleanup(){
        File resultDirFile = new File(pathToWorkingDir);
        try {
            FileUtils.deleteDirectory(resultDirFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getConfidenceLevelTest01(){
        // Result dir to put the zipped files
        String resultDir = pathResolver.generateNewResultFolder(pathToWorkingDir);

        // Get File handle for test files
        File folderToZip = new File(pathToStartingData + File.separator + "identical");
        File folderToZip2 = new File(pathToStartingData + File.separator + "notidentical");

        File zipFile1 = new File(resultDir + File.separator + "result_file_15.zip");
        File zipFile2 = new File(resultDir + File.separator + "result_file_10.zip");

        // Zip both files
        FileUtilsKt.zipDir(folderToZip.getAbsolutePath(), zipFile1.getAbsolutePath());
        FileUtilsKt.zipDir(folderToZip2.getAbsolutePath(), zipFile2.getAbsolutePath());

        ArrayList<File> zipFiles = new ArrayList<>();
        zipFiles.add(zipFile1);
        zipFiles.add(zipFile2);

        ConfidenceResult result = FileUtilsKt.getConfidenceLevel(zipFiles);

        double delta = 0.001;

        System.out.println("PHILIP TEST: " + result.toString());

        assertTrue(Math.abs(result.getConfidenceLevel() - 0.5) < delta);
    }

    @Test
    public void getConfidenceLevelTest02(){
        // Result dir to put the zipped files
        String resultDir = pathResolver.generateNewResultFolder(pathToWorkingDir);

        // Get File handle for test files
        File folderToZip = new File(pathToStartingData + File.separator + "identical");
        File folderToZip2 = new File(pathToStartingData + File.separator + "identical");

        File zipFile1 = new File(resultDir + File.separator + "result_file_15.zip");
        File zipFile2 = new File(resultDir + File.separator + "result_file_10.zip");

        // Zip both files
        FileUtilsKt.zipDir(folderToZip.getAbsolutePath(), zipFile1.getAbsolutePath());
        FileUtilsKt.zipDir(folderToZip2.getAbsolutePath(), zipFile2.getAbsolutePath());

        ArrayList<File> zipFiles = new ArrayList<>();
        zipFiles.add(zipFile1);
        zipFiles.add(zipFile2);

        ConfidenceResult result = FileUtilsKt.getConfidenceLevel(zipFiles);

        double delta = 0.001;

        assertTrue(Math.abs(result.getConfidenceLevel() - 1.0) < delta);
    }

    @Test
    public void getConfidenceLevelTest03(){
        // Result dir to put the zipped files
        String resultDir = pathResolver.generateNewResultFolder(pathToWorkingDir);

        // Get File handle for test files
        File folderToZip = new File(pathToStartingData + File.separator + "identical");

        File zipFile1 = new File(resultDir + File.separator + "result_file_15.zip");

        // Zip both files
        FileUtilsKt.zipDir(folderToZip.getAbsolutePath(), zipFile1.getAbsolutePath());

        ArrayList<File> zipFiles = new ArrayList<>();
        zipFiles.add(zipFile1);

        ConfidenceResult result = FileUtilsKt.getConfidenceLevel(zipFiles);

        double delta = 0.001;

        assertTrue(Math.abs(result.getConfidenceLevel() - 1.0) < delta);
    }

    @Test
    public void getConfidenceLevelTest04(){
        // Result dir to put the zipped files
        String resultDir = pathResolver.generateNewResultFolder(pathToWorkingDir);

        // Get File handle for test files
        File folderToZip = new File(pathToStartingData + File.separator + "identical");

        // Test that having a lot of test assignments, does not affect the confidence level
        File zipFile1 = new File(resultDir + File.separator + "result_file_15.zip");
        File zipFile2 = new File(resultDir + File.separator + "result_file_12_testAssig.zip");
        File zipFile3 = new File(resultDir + File.separator + "result_file_8_testAssig.zip");
        File zipFile4 = new File(resultDir + File.separator + "result_file_5_testAssig.zip");

        // Zip all files
        FileUtilsKt.zipDir(folderToZip.getAbsolutePath(), zipFile1.getAbsolutePath());
        FileUtilsKt.zipDir(folderToZip.getAbsolutePath(), zipFile2.getAbsolutePath());
        FileUtilsKt.zipDir(folderToZip.getAbsolutePath(), zipFile3.getAbsolutePath());
        FileUtilsKt.zipDir(folderToZip.getAbsolutePath(), zipFile4.getAbsolutePath());

        ArrayList<File> zipFiles = new ArrayList<>();
        zipFiles.add(zipFile1);
        zipFiles.add(zipFile2);
        zipFiles.add(zipFile3);
        zipFiles.add(zipFile4);

        ConfidenceResult result = FileUtilsKt.getConfidenceLevel(zipFiles);


        double delta = 0.001;

        assertTrue(Math.abs(result.getConfidenceLevel() - 1.0) < delta);
    }

    @Test
    public void checkZipFilesEquality1(){
        // Zip two identical files
        String resultDir = pathResolver.generateNewResultFolder(pathToWorkingDir);

        // Folder to zip (it is the same for both, then the result should be, that they are equal
        File folderToZip = new File(pathToStartingData + File.separator + "identical");

        File zipFile1 = new File(resultDir + File.separator + "zipfile1.zip");
        File zipFile2 = new File(resultDir + File.separator + "zipfile2.zip");

        // Zip both files
        FileUtilsKt.zipDir(folderToZip.getAbsolutePath(), zipFile1.getAbsolutePath());
        FileUtilsKt.zipDir(folderToZip.getAbsolutePath(), zipFile2.getAbsolutePath());

        assertEquals(true, FileUtilsKt.checkZipFilesEquality(zipFile1, zipFile2));
    }

    @Test
    public void checkZipFilesEquality2(){
        // Result dir to put the zipped files
        String resultDir = pathResolver.generateNewResultFolder(pathToWorkingDir);

        // Get File handle for test files
        File folderToZip = new File(pathToStartingData + File.separator + "identical");
        File folderToZip2 = new File(pathToStartingData + File.separator + "notidentical");

        // Create file handle for zipped files
        File zipFile1 = new File(resultDir + File.separator + "zipfile1.zip");
        File zipFile2 = new File(resultDir + File.separator + "zipfile2.zip");

        // Zip both files
        FileUtilsKt.zipDir(folderToZip.getAbsolutePath(), zipFile1.getAbsolutePath());
        FileUtilsKt.zipDir(folderToZip2.getAbsolutePath(), zipFile2.getAbsolutePath());

        // Assert that the checkZipFilesEquality throws exception, that they are not equal.
        assertThrows(ZipFilesNotEqualException.class, () -> FileUtilsKt.checkZipFilesEquality(zipFile1, zipFile2));
    }

    @Test
    public void checkZipFilesEquality3(){
        // Zip two identical files
        String resultDir = pathResolver.generateNewResultFolder(pathToWorkingDir);

        // Folder to zip (it is the same for both, then the result should be, that they are equal
        File folderToZip = new File(pathToStartingData + File.separator + "identicalinner");

        File zipFile1 = new File(resultDir + File.separator + "zipfile1.zip");
        File zipFile2 = new File(resultDir + File.separator + "zipfile2.zip");

        // Zip both files
        FileUtilsKt.zipDir(folderToZip.getAbsolutePath(), zipFile1.getAbsolutePath());
        FileUtilsKt.zipDir(folderToZip.getAbsolutePath(), zipFile2.getAbsolutePath());

        assertTrue(FileUtilsKt.checkZipFilesEquality(zipFile1, zipFile2));
    }

    @Test
    public void checkZipFilesEquality4(){
        // Result dir to put the zipped files
        String resultDir = pathResolver.generateNewResultFolder(pathToWorkingDir);

        // Get File handle for test files
        File folderToZip = new File(pathToStartingData + File.separator + "identicalinner");
        File folderToZip2 = new File(pathToStartingData + File.separator + "notidenticalinner");

        // Create file handle for zipped files
        File zipFile1 = new File(resultDir + File.separator + "zipfile1.zip");
        File zipFile2 = new File(resultDir + File.separator + "zipfile2.zip");

        // Zip both files
        FileUtilsKt.zipDir(folderToZip.getAbsolutePath(), zipFile1.getAbsolutePath());
        FileUtilsKt.zipDir(folderToZip2.getAbsolutePath(), zipFile2.getAbsolutePath());

        // Assert that the files differ, since the path to the two files in the zip files are switched.
        assertThrows(ZipFilesNotEqualException.class, () -> FileUtilsKt.checkZipFilesEquality(zipFile1, zipFile2));
    }
}
