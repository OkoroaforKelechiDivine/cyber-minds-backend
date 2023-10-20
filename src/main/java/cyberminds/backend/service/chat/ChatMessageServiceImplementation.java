package cyberminds.backend.service.chat;

import cyberminds.backend.dto.request.ChatMessageDTO;
import cyberminds.backend.exception.AppException;
import cyberminds.backend.model.chat.ChatMessage;
import cyberminds.backend.repository.chat.ChatMessageRepository;
import cyberminds.backend.service.utils.IPInfoDetectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class ChatMessageServiceImplementation implements ChatMessageService {

    @Autowired
    ChatMessageRepository chatMessageRepository;

    @Autowired
    IPInfoDetectionService ipInfoDetectionService;

    private ChatMessage mapChatMessageDTOToChatMessage(ChatMessageDTO messageDTO) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSenderId(messageDTO.getSenderId());
        chatMessage.setReceiverId(messageDTO.getReceiverId());
        chatMessage.setContent(messageDTO.getContent());
        return chatMessage;
    }

    @Override
    public ChatMessage sendChatMessage(ChatMessageDTO message) throws AppException {
        if (message != null && !message.getContent().isEmpty()) {
            if (ipInfoDetectionService.isSuspiciousUrl(message.getContent())) {
                throw new AppException("Suspicious URL detected in the message.");
            }
            ChatMessage chatMessage = mapChatMessageDTOToChatMessage(message);
            return chatMessageRepository.save(chatMessage);
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
}
