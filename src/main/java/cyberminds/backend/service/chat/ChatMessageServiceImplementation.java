
package cyberminds.backend.service.chat;

import cyberminds.backend.dto.request.ChatMessageDTO;
import cyberminds.backend.exception.AppException;
import cyberminds.backend.model.chat.ChatMessage;
import cyberminds.backend.model.user.AppUser;
import cyberminds.backend.repository.chat.ChatMessageRepository;
import cyberminds.backend.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ChatMessageServiceImplementation implements ChatMessageService {

    @Autowired
    ChatMessageRepository chatMessageRepository;

    @Autowired
    UserRepository userRepository;

    private ChatMessage mapChatMessageDTOToChatMessage(ChatMessageDTO messageDTO) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSenderId(messageDTO.getSenderId());
        chatMessage.setReceiverId(messageDTO.getReceiverId());
        chatMessage.setContent(messageDTO.getContent());
        chatMessage.setTimestamp(LocalDateTime.now());
        return chatMessage;
    }
    public boolean containsURL(String text) {
        String regex = "(?i)\\b((https?|ftp|file)://[-A-Z0-9+&@#/%=~_|$?!:,.]*[-A-Z0-9+&@#/%=~_|$])";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }
    @Override
    public ChatMessage sendMessage(ChatMessageDTO message) throws AppException {
        if (message != null && !message.getContent().isEmpty()) {
            ChatMessage chatMessage = mapChatMessageDTOToChatMessage(message);
            chatMessageRepository.save(chatMessage);
            return chatMessage;
        } else {
            throw new AppException("Chat message cannot be empty.");
        }
    }

    @Override
    public List<ChatMessage> getChatMessages(String senderId, String receiverId) {
        return null;
    }

    @Override
    public List<ChatMessage> getChatMessagesForUser(String userId) {
        return null;
    }

    @Override
    public void deleteUser(String userId) throws AppException {
        Optional<AppUser> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            userRepository.deleteById(userId);
        } else {
            throw new AppException("User with ID " + userId + " not found");
        }
    }
}