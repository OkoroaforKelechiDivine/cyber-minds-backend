package cyberminds.backend.service.user;

import cyberminds.backend.dto.request.RegistrationDTO;
import cyberminds.backend.exception.AppException;
import cyberminds.backend.model.user.User;
public interface UserService {
    User createUser(RegistrationDTO blogger) throws AppException;
}
