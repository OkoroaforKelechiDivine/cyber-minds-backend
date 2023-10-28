package cyberminds.backend.repository.chat;

import cyberminds.backend.model.chat.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {}