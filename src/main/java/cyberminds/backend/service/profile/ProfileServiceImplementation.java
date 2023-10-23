package cyberminds.backend.service.profile;

import cyberminds.backend.dto.request.ProfileDTO;
import cyberminds.backend.exception.AppException;
import cyberminds.backend.model.profile.Profile;
import cyberminds.backend.model.user.AppUser;
import cyberminds.backend.repository.profile.ProfileRepository;
import cyberminds.backend.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ProfileServiceImplementation implements ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    UserRepository userRepository;

    public Boolean existByUsername(String username){
        return profileRepository.existsByUsername(username);
    }

    @Override
    @Transactional
    public void createProfile(ProfileDTO profileDTO, String userId) throws AppException {
        if (profileDTO.getDateOfBirth() == null) {
            throw new AppException("You must provide a date of birth");
        }
        Profile profile = new Profile();
        profile.setDateOfBirth(profileDTO.getDateOfBirth());
        profile.setProfileImageUrl(profileDTO.getProfileImageUrl());
        profile.setBio(profileDTO.getBio());
        profile.setHobbies(profileDTO.getHobbies());
        profile.setUsername(profileDTO.getUsername());
        modelMapper.map(profile, profileDTO);
        AppUser appUser = userRepository.findUserById(userId);

        if (appUser == null) {
            throw new AppException("User not found");
        }
        profileRepository.save(profile);
        appUser.setProfile(profile);
        userRepository.save(appUser);
    }
}
