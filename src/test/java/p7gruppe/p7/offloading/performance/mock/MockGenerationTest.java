package p7gruppe.p7.offloading.performance.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import p7gruppe.p7.offloading.api.AssignmentsApiController;
import p7gruppe.p7.offloading.api.JobsApiController;
import p7gruppe.p7.offloading.api.UsersApiController;
import p7gruppe.p7.offloading.data.repository.UserRepository;
import p7gruppe.p7.offloading.performance.APISupplier;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest()
public class MockGenerationTest {

    @Autowired
    UsersApiController usersApiController;
    @Autowired
    JobsApiController jobsApiController;
    @Autowired
    AssignmentsApiController assignmentsApiController;

    @Autowired
    UserRepository userRepository;


    static final long RANDOM_SEED = 123456789L;
    private APISupplier apiSupplier;

    @BeforeEach
    void setup(){
        apiSupplier = new APISupplier(usersApiController, assignmentsApiController, jobsApiController);
    }

    @BeforeEach
    void resetRepositories(){
        userRepository.deleteAll();
    }

    @Test
    void generateUsers_proportionOfMaliciousUsers() throws Exception {
        double proportionMalicious = 0.15d;
        MockUserGenerator userGenerator = new MockUserGenerator(RANDOM_SEED, proportionMalicious);
        List<MockUser> users = userGenerator.generateUsers(100, apiSupplier);

        int amountOfMaliciousUsers = 0;
        for (MockUser mockUser : users) if (mockUser.isMalicious) amountOfMaliciousUsers += 1;

        assertEquals(100, users.size());
        assertEquals(15, amountOfMaliciousUsers);
    }

    @Test
    void registerUsers_usersExistsInUserRepository() throws Exception {
        double proportionMalicious = 0.15d;
        MockUserGenerator userGenerator = new MockUserGenerator(RANDOM_SEED, proportionMalicious);
        List<MockUser> users = userGenerator.generateUsers(100, apiSupplier);

        // Register users
        for (MockUser mockUser : users) mockUser.register();

        // Assert that they are now registered in the repository
        for (MockUser mockUser : users) assertTrue(userRepository.userExists(mockUser.userCredentials.getUsername()));
    }

    @Test
    void generateWorkers_allUsersHave2Devices() {
        MockWorkerGenerator workerGenerator = new MockWorkerGenerator();
        MockUserGenerator userGenerator = new MockUserGenerator();

        List<MockUser> users = userGenerator.generateUsers(100, apiSupplier);
        List<MockWorker> workers = workerGenerator.generateWorkers(200, users, apiSupplier);

        HashMap<String, Integer> userToDeviceCount = new HashMap<String, Integer>();
        for (MockWorker worker : workers) {
            String username = worker.owner.userCredentials.getUsername();
            if (userToDeviceCount.containsKey(username)) {
                userToDeviceCount.put(username, userToDeviceCount.get(username) + 1);
            }else {
                userToDeviceCount.put(username, 1);
            }
        }

        assertEquals(200, workers.size());
        assertEquals(100, userToDeviceCount.keySet().size());

        for (MockWorker worker : workers) {
            assertEquals(2, userToDeviceCount.get(worker.owner.userCredentials.getUsername()));
        }
    }

    @Test
    void mockJob_cpuTimeByteConversion() {
        long computationTime = 123789465L;
        MockJob mockJob = new MockJob(computationTime, 2);

        byte[] encodedCpuTimeBytes = mockJob.getComputationTimeAsBase64Bytes();
        long decodedComputationTime = MockJob.base64BytesToComputationTime(encodedCpuTimeBytes);
        assertEquals(computationTime, decodedComputationTime);
    }

}