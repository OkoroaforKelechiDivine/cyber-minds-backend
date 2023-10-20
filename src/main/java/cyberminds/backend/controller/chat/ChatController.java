package cyberminds.backend.controller.chat;

import cyberminds.backend.dto.request.ChatMessageDTO;
import cyberminds.backend.dto.response.ResponseDetails;
import cyberminds.backend.exception.AppException;
import cyberminds.backend.service.chat.ChatMessageServiceImplementation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/chats")
@Slf4j
@CrossOrigin(origins = "true", allowCredentials = "true")
public class ChatController {

    @Autowired
    private ChatMessageServiceImplementation chatService;

    @PostMapping("/send-message")
    public ResponseEntity<?> sendMessage(@RequestBody ChatMessageDTO messageDTO) {
        try {
            chatService.sendChatMessage(messageDTO);
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Message sent successfully.", HttpStatus.OK.toString());
            return ResponseEntity.ok(responseDetails);
        } catch (AppException ex) {
            ResponseDetails errorDetails = new ResponseDetails(LocalDateTime.now(), ex.getMessage(), HttpStatus.BAD_REQUEST.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
        }
    }
}
