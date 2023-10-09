package cyberminds.backend.service.user;

import cyberminds.backend.dto.request.RegistrationDTO;
import cyberminds.backend.exception.AppException;
import cyberminds.backend.model.user.AppUser;
public interface UserService {
    AppUser createUser(RegistrationDTO blogger) throws AppException;
}
