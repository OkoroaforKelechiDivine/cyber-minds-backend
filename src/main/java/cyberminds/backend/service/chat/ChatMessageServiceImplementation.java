
package cyberminds.backend.service.chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cyberminds.backend.dto.request.ChatMessageDTO;
import cyberminds.backend.exception.AppException;
import cyberminds.backend.model.chat.ChatMessage;
import cyberminds.backend.model.user.AppUser;
import cyberminds.backend.repository.chat.ChatMessageRepository;
import cyberminds.backend.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
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
    public void deleteChatMessage(String messageId) {
    }
    public void deleteMessageAndUser(ChatMessageDTO messageDTO) {
        if (messageDTO != null) {
            try {
                ChatMessage message = chatMessageRepository.findBySenderIdAndContent(messageDTO.getSenderId(), messageDTO.getContent());
                if (message != null) {
                    AppUser user = userRepository.findById(message.getSenderId()).orElse(null);
                    chatMessageRepository.deleteById(message.getId());
                    if (user != null) {
                        userRepository.deleteById(user.getId());
                        log.info("User with ID " + user.getId() + " deleted.");
                    }
                    log.info("Message with ID " + message.getId() + " deleted.");
                } else {
                    log.error("Message not found based on senderId and content.");
                }
            } catch (Exception e) {
                log.error("Error deleting message and user: " + e.getMessage());
            }
        } else {
            log.error("Invalid messageDTO provided for deletion.");
        }
    }



}