package cyberminds.backend.repository.user;

import cyberminds.backend.model.user.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<AppUser, String> {
//    AppUser findByEmail(String email);

    AppUser findByUsername(String username);

    AppUser findByLastName(String input);

    AppUser findByFirstName(String input);

    AppUser findByPhoneNumber(String phoneNumber);

    Boolean existsByPhoneNumber(String phoneNumber);

//    AppUser findUserById(String userId);
}

