package p7gruppe.p7.offloading.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import p7gruppe.p7.offloading.data.local.PathResolver;
import p7gruppe.p7.offloading.data.repository.AssignmentRepository;
import p7gruppe.p7.offloading.data.repository.DeviceRepository;
import p7gruppe.p7.offloading.data.repository.JobRepository;
import p7gruppe.p7.offloading.data.repository.UserRepository;
import p7gruppe.p7.offloading.scheduling.FIFOJobScheduler;
import p7gruppe.p7.offloading.scheduling.JobScheduler;
import p7gruppe.p7.offloading.scheduling.SampleScheduler;

@Configuration
@Profile("fifo-test")
@EnableJpaRepositories("p7gruppe.p7.offloading.data.repository")
public class FifoSchedulerConfiguration {

    @Autowired
    JobRepository jobRepository;
    @Autowired
    DeviceRepository deviceRepository;
    @Autowired
    AssignmentRepository assignmentRepository;
    @Autowired
    UserRepository userRepository;


    @Bean
    PathResolver getPathResolver() {
        return new PathResolver("test_data");
    }

    @Bean
    JobScheduler getJobScheduler(){
        return new FIFOJobScheduler(assignmentRepository, jobRepository);
    }
}
