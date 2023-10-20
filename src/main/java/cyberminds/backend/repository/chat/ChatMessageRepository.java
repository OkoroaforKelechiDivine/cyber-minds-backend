package cyberminds.backend.repository.chat;

import cyberminds.backend.model.chat.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
//    List<ChatMessage> findBySenderIdAndReceiverId(String senderId, String receiverId);
//
//    List<ChatMessage> getMessagesBetweenUsers(String senderId, String receiverId);
//
//    List<ChatMessage> getMessagesForUser(String userId);
//
//    void deleteMessagesBySenderId(String userId);
//
//    ChatMessage findByContent(String content);

    ChatMessage findBySenderIdAndContent(String senderId, String content);
}
