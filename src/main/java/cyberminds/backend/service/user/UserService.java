package cyberminds.backend.service.user;

import cyberminds.backend.model.user.User;
import cyberminds.backend.model.user.UserStatus;
import cyberminds.backend.repository.user.UserRepository;
import cyberminds.backend.service.utility.UserValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserValidationService userValidationService;
    public User createUser(User user) {
        userValidationService.validateUser(user);
        user.setStatus(UserStatus.ONLINE);
        userRepository.save(user);
        return user;
    }
}
