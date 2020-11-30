package p7gruppe.p7.offloading.data.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import p7gruppe.p7.offloading.data.enitity.AssignmentEntity;
import p7gruppe.p7.offloading.data.enitity.DeviceEntity;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.data.enitity.UserEntity;

import java.util.Optional;

public interface AssignmentRepository extends CrudRepository<AssignmentEntity, Long> {

    @Query(value = "SELECT * FROM assignment_entity WHERE worker_device_id = ?1 AND status = 'PROCESSING'", nativeQuery = true)
    Optional<AssignmentEntity> getProcessingAssignmentForDevice(int workerDeviceID);

}
