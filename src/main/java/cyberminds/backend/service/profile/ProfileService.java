package cyberminds.backend.service.profile;

import cyberminds.backend.dto.request.ProfileDTO;
import cyberminds.backend.exception.AppException;
public interface ProfileService {

    void createProfile(ProfileDTO profileDTO, String userId) throws AppException;
}
