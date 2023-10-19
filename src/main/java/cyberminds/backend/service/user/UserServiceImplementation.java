package cyberminds.backend.service.user;

import cyberminds.backend.dto.request.FriendsDTO;
import cyberminds.backend.exception.AppException;
import cyberminds.backend.model.user.AppUser;
import cyberminds.backend.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//http://localhost:9090/api/users/follow
@Service
@Slf4j
public class UserServiceImplementation implements UserService {

    @Autowired
    private UserRepository userRepository;
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

    @Override
    public void searchForFriends(AppUser user) {

    }

    @Override
    public void unFollowFriend(AppUser user) {

    }

    @Override
    public void acceptFriendRequest(AppUser user) {

    }

    @Override
    public void deleteFriendRequest() {

    }

    @Override
    public void numberOfFriends(int numberOfFriends) {

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
