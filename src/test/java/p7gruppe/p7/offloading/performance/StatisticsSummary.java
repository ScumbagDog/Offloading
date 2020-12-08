package p7gruppe.p7.offloading.performance;

import org.mockito.internal.util.collections.ListUtil;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.model.Job;
import p7gruppe.p7.offloading.performance.mock.MockEmployer;
import p7gruppe.p7.offloading.performance.mock.UserBase;
import p7gruppe.p7.offloading.performance.statistics.DataPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class StatisticsSummary {

    private UserBase userBase;
    private RepositorySupplier repositorySupplier;

    public StatisticsSummary(UserBase userBase, RepositorySupplier repositorySupplier) {
        this.userBase = userBase;
        this.repositorySupplier = repositorySupplier;
    }

    public long getMaximumTimeFromUploadTillProcessedMillis() {
        long maximumProcessingTime = 0L;
        for (MockEmployer employer : userBase.getEmployers()) {
            List<JobStatistic> jobs = employer.getJobsStatistics();
            for (JobStatistic jobStat : jobs) {
                maximumProcessingTime = Math.max(maximumProcessingTime, jobStat.getProcessingTime());
            }
        }
        return maximumProcessingTime;
    }

    public long getAverageJobTimeForFinishedJobsMillis() {
        long totalJobTime = 0L;
        int totalJobs = 0;
        for (MockEmployer employer : userBase.getEmployers()) {
            List<JobStatistic> jobs = employer.getJobsStatistics();
            for (JobStatistic jobStat : jobs) {
                if (jobStat.isJobCompleted()) {
                    totalJobTime += jobStat.getProcessingTime();
                    totalJobs += 1;
                }
            }
        }
        if (totalJobs == 0) return 0;
        return (long) ((double) totalJobTime / (double) totalJobs);
    }

    public int getAmountOfResults() {
        int count = 0;
        for (MockEmployer employer : userBase.getEmployers()) {
            List<JobStatistic> jobs = employer.getJobsStatistics();
            for (JobStatistic jobStat : jobs) {
                if (jobStat.isJobCompleted()) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getAmountOfMaliciousResults() {
        int count = 0;
        for (MockEmployer employer : userBase.getEmployers()) {
            List<JobStatistic> jobs = employer.getJobsStatistics();
            for (JobStatistic jobStat : jobs) {
                if (jobStat.isJobCompleted() && !jobStat.isResultCorrect()) {
                    count++;
                }
            }
        }
        return count;
    }

    private List<JobStatistic> allJobs() {
        List<JobStatistic> jobs = new ArrayList<>();
        for (MockEmployer employer : userBase.getEmployers()) {
            jobs.addAll(employer.getJobsStatistics());
        }
        return jobs;
    }

    private List<JobStatistic> allCompletedJobs(){
        return allJobs().stream().filter(JobStatistic::isJobCompleted).collect(Collectors.toList());
    }

    public List<DataPoint<Double>> confidenceDataPoints() {
        List<DataPoint<Double>> dataPoints = new ArrayList<>();

        for(JobStatistic job : allCompletedJobs()){
            Optional<JobEntity> jobEntityOptional = repositorySupplier.jobRepository.findById(job.jobID);
            if (!jobEntityOptional.isPresent())
                throw new RuntimeException("Job with id " + job.jobID + " present in statistics not server database");

            dataPoints.add(new DataPoint<Double>(job.getUploadTime(), jobEntityOptional.get().confidenceLevel));
        }

        return dataPoints;
    }

    public double averageConfidence() {
        double total = 0;
        int dataPoints = 0;

        for(JobStatistic job : allCompletedJobs()){
            Optional<JobEntity> jobEntityOptional = repositorySupplier.jobRepository.findById(job.jobID);
            if (!jobEntityOptional.isPresent())
                throw new RuntimeException("Job with id " + job.jobID + " present in statistics not server database");

            dataPoints++;
            total += jobEntityOptional.get().confidenceLevel;
        }

        return total / (double) dataPoints;
    }

}
