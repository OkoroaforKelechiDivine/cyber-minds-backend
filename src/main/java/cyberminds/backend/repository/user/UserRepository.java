package cyberminds.backend.repository.user;

import cyberminds.backend.model.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);

    User findUserById(String id);
    Boolean existsByEmail(String email);
}
