package p7gruppe.p7.offloading.data.repository;

import org.springframework.data.repository.CrudRepository;
import p7gruppe.p7.offloading.data.enitity.UserEntity;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
}