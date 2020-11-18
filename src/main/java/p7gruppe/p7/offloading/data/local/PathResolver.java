package p7gruppe.p7.offloading.data.local;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PathResolver {

    static final String DATA_PREFIX = System.getProperty("user.dir") + File.separator + "data" + File.separator;
    private static final String JOBS_PREFIX = DATA_PREFIX + File.separator + "jobs" + File.separator;

    private static List<String> temporaryReservedPaths = new ArrayList<>();

    public static synchronized String generateNewJobFolder(String userName){
        String pathPrefix = JOBS_PREFIX + File.separator + userName + File.separator;

        File rootDirectory = new File(pathPrefix);
        if (!rootDirectory.exists())
            rootDirectory.mkdirs(); // todo Throw exception if false

        int i = 0;
        File file;
        String jobName;
        do {
            jobName = generateJobName(i++);
            file = new File(pathPrefix + jobName);
        }while (file.exists());

        file.mkdir(); // todo Throw exception if false
        return pathPrefix + jobName;
    }

    private static synchronized String generateJobName(int i) {
        return "job_" + i;
    }



}
