package cyberminds.backend.service.user;

import cyberminds.backend.model.user.AppUser;

public interface UserService {

    void followFriend(AppUser user);
    void searchForFriends(AppUser user);
    void unFollowFriend(AppUser user);
    void  acceptFriendRequest(AppUser user);
    void deleteFriendRequest();
    void numberOfFriends(int numberOfFriends);
    void numberOfLikes(int numberOfLikes);
    void numberOfPosts(int numberOfPosts);
    void numberOfComments(int numberOfComments);
    void numberOfVideos(int numberOfVideos);
}
