package cyberminds.backend.controller.chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cyberminds.backend.dto.request.ChatMessageDTO;
import cyberminds.backend.dto.response.ResponseDetails;
import cyberminds.backend.exception.AppException;
import cyberminds.backend.service.chat.ChatMessageServiceImplementation;
import cyberminds.backend.service.user.UserServiceImplementation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/chats")
@Slf4j
public class ChatController {

    @Autowired
    private ChatMessageServiceImplementation chatService;

    @Autowired
    private UserServiceImplementation userServiceImplementation;

    private int maliciousCount = 0;

    private static final String VIRUS_TOTAL_API_KEY = "c93fbb4e9e35f87a82ce02e276b7ab4ae85004327c3dc5b7a61b51b95110061a";

    private HttpPost getHttpPost(String url) {
        HttpPost httpPost = new HttpPost("https://www.virustotal.com/api/v3/urls");
        httpPost.setHeader("x-apikey", VIRUS_TOTAL_API_KEY);
        httpPost.setHeader("accept", "application/json");
        httpPost.setHeader("content-type", "application/x-www-form-urlencoded");

        String requestBody = "url=" + url;
        StringEntity entity = new StringEntity(requestBody, ContentType.APPLICATION_FORM_URLENCODED);
        httpPost.setEntity(entity);
        return httpPost;
    }

    private int scanURL(String url) throws AppException {
        String id = null;
        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = getHttpPost(url);
            HttpResponse response = httpClient.execute(httpPost);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            log.info("VirusTotal Response: " + result.toString());
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(result.toString());
            JsonNode dataNode = jsonNode.get("data");

            if (dataNode != null && dataNode.has("id")) {
                id = dataNode.get("id").asText();
                getURLInformation(id);
                return 200;
            } else {
                throw new AppException("The 'id' key is not present in the JSON response or is null.");
            }
        } catch (Exception e) {
            throw new AppException("Error scanning URL with VirusTotal: " + e.getMessage());
        }
    }

    private HttpGet getHttpGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("x-apikey", VIRUS_TOTAL_API_KEY);
        httpGet.setHeader("accept", "application/json");
        return httpGet;
    }

    private void getURLInformation(String id) throws AppException {
        String apiUrl = "https://www.virustotal.com/api/v3/analyses/{id}";
        apiUrl = apiUrl.replace("{id}", id);
        HttpClient httpClient = HttpClients.createDefault();
        try {
            HttpGet httpGet = getHttpGet(apiUrl);
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                String responseBody = EntityUtils.toString(response.getEntity());
                log.info("Response from VirusTotal API:\n" + responseBody);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                if (jsonNode.has("data") && jsonNode.get("data").has("attributes") && jsonNode.get("data").get("attributes").has("stats")) {
                    JsonNode stats = jsonNode.get("data").get("attributes").get("stats");
                    if (stats.has("malicious")) {
                        maliciousCount = stats.get("malicious").asInt();
                        log.info("Malicious" +  maliciousCount);
                    }
                }
            } else {
                throw new AppException("Request to VirusTotal API failed with status code: " + response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            throw new AppException("Error making GET request: " + e.getMessage());
        }
    }

    @PostMapping("/send-message")
    public ResponseEntity<?> sendMessage(@RequestBody ChatMessageDTO messageDTO) throws AppException {
        if (!userServiceImplementation.existsById(messageDTO.getSenderId())) {
            ResponseDetails errorResponseSender = new ResponseDetails(LocalDateTime.now(), "Sender is not found. Message not sent.", HttpStatus.NOT_FOUND.toString());
            return ResponseEntity.status(404).body(errorResponseSender);
        }
        if (!userServiceImplementation.existsById(messageDTO.getReceiverId())) {
            ResponseDetails errorResponseReceiver = new ResponseDetails(LocalDateTime.now(), "Receiver is not found. Message not sent.", HttpStatus.NOT_FOUND.toString());
            return ResponseEntity.status(404).body(errorResponseReceiver);
        }
        if (messageDTO.getSenderId().equals(messageDTO.getReceiverId())) {
            ResponseDetails invalidResponse = new ResponseDetails(LocalDateTime.now(), "Receiver and Sender are the same.", HttpStatus.CONFLICT.toString());
            return ResponseEntity.status(409).body(invalidResponse);
        }
        if (!userServiceImplementation.usersAreFollowingEachOther(messageDTO.getSenderId(), messageDTO.getReceiverId())) {
            ResponseDetails invalidResponse = new ResponseDetails(LocalDateTime.now(), "Users are not following each other, so they can not chat.", HttpStatus.NOT_ACCEPTABLE.toString());
            return ResponseEntity.status(406).body(invalidResponse);
        }
        try {
            if (chatService.containsURL(messageDTO.getContent())) {
                int scanResult = scanURL(messageDTO.getContent());
                if (scanResult == 200) {
                    if (maliciousCount > 0) {
                        chatService.deleteUser(messageDTO.getSenderId());
                        ResponseDetails deleteResponse = new ResponseDetails(LocalDateTime.now(), "Malicious content detected. User has been deleted.", HttpStatus.ALREADY_REPORTED.toString());
                        return ResponseEntity.status(208).body(deleteResponse);
                    }
                }
                else {
                    chatService.sendMessage(messageDTO);
                }
            }
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Message processed successfully.", HttpStatus.OK.toString());
            return ResponseEntity.ok(responseDetails);
        } catch (AppException ex) {
            ResponseDetails errorDetails = new ResponseDetails(LocalDateTime.now(), ex.getMessage(), HttpStatus.BAD_REQUEST.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
        }
    }
}
