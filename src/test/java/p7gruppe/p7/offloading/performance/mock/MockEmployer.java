package p7gruppe.p7.offloading.performance.mock;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import p7gruppe.p7.offloading.api.JobsApi;
import p7gruppe.p7.offloading.data.enitity.JobEntity.JobStatus;
import p7gruppe.p7.offloading.model.Job;
import p7gruppe.p7.offloading.model.JobFiles;
import p7gruppe.p7.offloading.performance.APISupplier;
import p7gruppe.p7.offloading.performance.JobStatistic;

import java.util.*;

public class MockEmployer implements Simulatable {

    public final MockUser mockUser;

    private final JobSpawner jobSpawner;
    private final APISupplier apiSupplier;

    private JobsApi jobsApi;

    private final long REQUEST_INTERVAL_MILLIS = 800L;
    private long nextRequestTime = 0L;

    private int jobsPosted = 0;
    private final List<JobStatistic> postedJobs = new ArrayList<>();

    private final HashMap<String, Boolean> hasDownloadedResult = new HashMap<>();

    public MockEmployer(MockUser mockUser, APISupplier apiSupplier, JobSpawner jobSpawner) {
        this.mockUser = mockUser;
        this.apiSupplier = apiSupplier;
        this.jobSpawner = jobSpawner;
        this.jobsApi = apiSupplier.jobsApi;
    }

    @Override
    public void start() {
    }

    public void update() {
        Optional<MockJob> optionalJob = jobSpawner.pollJob();
        optionalJob.ifPresent(this::uploadJob);

        if (System.currentTimeMillis() > nextRequestTime) {
            getJobStatuses(); // todo : performance tweak: only getJobStatuses if employer has undownloaded jobs
            nextRequestTime = System.currentTimeMillis() + REQUEST_INTERVAL_MILLIS;
        }
    }

    @Override
    public void stop() {
        getJobStatuses();
        for (JobStatistic jobStatistic : postedJobs) {
            if (!jobStatistic.isJobCompleted()) jobStatistic.registerAsUnCompleted(System.currentTimeMillis());
        }
    }

    private void uploadJob(MockJob mockJob) {
        String jobName = String.valueOf(jobsPosted);
        hasDownloadedResult.put(jobName, false);

        ResponseEntity<Long> responseEntity = apiSupplier.jobsApi.postJob(mockUser.userCredentials, mockJob.answersNeeded, String.valueOf(jobsPosted), ((mockJob.computationTimeMillis / 1000) / 60) * 2, mockJob.getComputationTimeAsBase64Bytes());
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Could not upload job from mock employer : " + mockUser.userCredentials);
        }

        JobStatistic jobStatistic = new JobStatistic(jobName, mockJob.computationTimeMillis, this.mockUser, responseEntity.getBody());

        jobStatistic.registerUpload(System.currentTimeMillis());
        postedJobs.add(jobStatistic);
        jobsPosted++;
    }

    private void getJobStatuses() {
        ResponseEntity<List<Job>> responseEntity = apiSupplier.jobsApi.getJobsForUser(mockUser.userCredentials);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Got error when retrieving job statuses from the server");
        }

        for (Job job : responseEntity.getBody()) {
            JobStatistic jobStat = getJobStat(job.getName());
            JobStatus updatedJobStatus = JobStatus.valueOf(job.getStatus());
            jobStat.registerStatus(updatedJobStatus, System.currentTimeMillis());

            boolean finishedProcessing = (updatedJobStatus.equals(JobStatus.DONE) || updatedJobStatus.equals(JobStatus.DONE_CONFLICTING_RESULTS));
            if (finishedProcessing && !hasDownloadedResult.get(job.getName())) {
                downloadResult(job.getId(), jobStat);
            }
        }
    }

    private JobStatistic getJobStat(String name) {
        for (JobStatistic jobStatistic : getJobsStatistics()) {
            if (jobStatistic.jobName.equals(name)) return jobStatistic;
        }
        throw new RuntimeException("Job " + name + " missing from JobStatistics");
    }

    private void downloadResult(long jobID, JobStatistic jobStatistic) {
        ResponseEntity<JobFiles> response = apiSupplier.jobsApi.getJobResult(jobID, mockUser.userCredentials);

        if (response.getStatusCode() != HttpStatus.OK)
            throw new RuntimeException("Got error when attempting to download the result files. Jobid: " + jobID);

        byte[] result = response.getBody().getData();
        if (Arrays.equals(result, MockResultData.getCorrectResultBytes())) {
            jobStatistic.registerResultCorrectness(true);
        } else if (MockResultData.equalsAnyMalicious(result)) {
            jobStatistic.registerResultCorrectness(false);
        } else {
            System.out.println(Arrays.toString(result));
            throw new RuntimeException("Job result does not match correct/malicious test format");
        }

        hasDownloadedResult.put(jobStatistic.jobName, true);
    }

    public List<JobStatistic> getJobsStatistics() {
        return postedJobs;
    }
}