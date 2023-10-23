package cyberminds.backend.repository.profile;

import cyberminds.backend.model.profile.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends MongoRepository<Profile, String> {
    Boolean existsByUsername(String username);
}
