package cyberminds.backend.service.user;

import cyberminds.backend.dto.request.FriendsDTO;
import cyberminds.backend.exception.AppException;
import cyberminds.backend.model.user.AppUser;

import java.util.List;

public interface UserService {

    boolean followFriend(FriendsDTO friendsDTO) throws AppException;
    void searchFriend(String username) throws AppException;
    void unFollowFriend(String userId, String friendId) throws AppException;

    List<AppUser> getAllUsers();
    void numberOfFollowers(int numberOfFriends);

    void numberOfFollowings(int numberOfFriends);
    void numberOfLikes(int numberOfLikes);
    void numberOfPosts(int numberOfPosts);
    void numberOfComments(int numberOfComments);
    void numberOfVideos(int numberOfVideos);
}
