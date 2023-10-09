package cyberminds.backend.repository.user;

import cyberminds.backend.model.user.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<AppUser, String> {
    AppUser findByEmail(String email);

    AppUser findUserById(String id);
    Boolean existsByEmail(String email);
}
