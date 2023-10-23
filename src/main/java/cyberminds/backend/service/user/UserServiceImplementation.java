package cyberminds.backend.service.user;

import cyberminds.backend.dto.request.FriendsDTO;
import cyberminds.backend.exception.AppException;
import cyberminds.backend.model.user.AppUser;
import cyberminds.backend.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImplementation implements UserService {

    @Autowired
    private UserRepository userRepository;

    public Boolean existsById(String id) {
        return userRepository.existsById(id);
    }
    public boolean followFriend(FriendsDTO friendsDTO) throws AppException {
        String userId = friendsDTO.getUserId();
        String friendId = friendsDTO.getFriendId();

        AppUser currentUser = userRepository.findById(userId).orElseThrow(() -> new AppException("User not found"));
        AppUser friendToFollow = userRepository.findById(friendId).orElseThrow(() -> new AppException("Friend not found"));

        if (!currentUser.getFollowing().contains(friendId)) {
            currentUser.getFollowing().add(friendId);
            friendToFollow.getFollowers().add(userId);

            userRepository.save(currentUser);
            userRepository.save(friendToFollow);
        } else {
            throw new AppException("User is already following the friend.");
        }
        return true;
    }
    public void searchFriend(String input) throws AppException {
        AppUser friend = userRepository.findByFirstName(input);

        if (friend != null) {
            return;
        }
        friend = userRepository.findByLastName(input);
        if (friend != null) {
            return;
        }
        friend = userRepository.findByFirstName(input);

        if (friend != null) {
            return;
        }
        throw new AppException("Friend not found");
    }

    @Override
    public void unFollowFriend(String userId, String friendId) throws AppException {
        AppUser currentUser = userRepository.findById(userId).orElseThrow(() -> new AppException("User not found"));
        AppUser friendToUnfollow = userRepository.findById(friendId).orElseThrow(() -> new AppException("Friend not found"));

        if (currentUser.getFollowing().contains(friendId)) {
            currentUser.getFollowing().remove(friendId);
            friendToUnfollow.getFollowers().remove(userId);

            userRepository.save(currentUser);
            userRepository.save(friendToUnfollow);
        } else {
            throw new AppException("User is not following the friend.");
        }
    }

    @Override
    public void numberOfFollowers(int numberOfFriends) {

    }

    @Override
    public void numberOfFollowings(int numberOfFriends) {

    }

    @Override
    public void numberOfLikes(int numberOfLikes) {

    }

    @Override
    public void numberOfPosts(int numberOfPosts) {

    }

    @Override
    public void numberOfComments(int numberOfComments) {

    }

    @Override
    public void numberOfVideos(int numberOfVideos) {

    }
}
