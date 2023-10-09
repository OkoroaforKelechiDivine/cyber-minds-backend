package cyberminds.backend.service.user;

import cyberminds.backend.dto.request.RegistrationDTO;
import cyberminds.backend.exception.AppException;

public interface UserService {
    void createUser(RegistrationDTO blogger) throws AppException;
    void resetPassword(String email, String newPassword, String confirmPassword) throws AppException;
}
