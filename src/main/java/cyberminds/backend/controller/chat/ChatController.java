package cyberminds.backend.controller.chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cyberminds.backend.dto.request.ChatMessageDTO;
import cyberminds.backend.dto.response.ResponseDetails;
import cyberminds.backend.exception.AppException;
import cyberminds.backend.service.chat.ChatMessageServiceImplementation;
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
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

@RestController
@RequestMapping("/api/chats")
@Slf4j
@CrossOrigin(origins = "true", allowCredentials = "true")
public class ChatController {

    @Autowired
    private ChatMessageServiceImplementation chatService;
    private static HttpPost getHttpPost(String url) {
        HttpPost httpPost = new HttpPost("https://www.virustotal.com/api/v3/urls");
        httpPost.setHeader("x-apikey", "c93fbb4e9e35f87a82ce02e276b7ab4ae85004327c3dc5b7a61b51b95110061a");
        httpPost.setHeader("accept", "application/json");
        httpPost.setHeader("content-type", "application/x-www-form-urlencoded");

        String requestBody = "url=" + url;
        StringEntity entity = new StringEntity(requestBody, ContentType.APPLICATION_FORM_URLENCODED);
        httpPost.setEntity(entity);
        return httpPost;
    }

    public int scanURL(String url) {
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
                String id = dataNode.get("id").asText();
                log.info("Extracted ID: " + id);
                byte[] idBytes = id.getBytes(StandardCharsets.UTF_8);
                String encodedId = Base64.getEncoder().withoutPadding().encodeToString(idBytes);
                log.info("Encoded ID (without padding): " + encodedId);
            } else {
                log.warn("The 'id' key is not present in the JSON response or is null.");
            }

        } catch (Exception e) {
            log.error("Error scanning URL with VirusTotal: " + e.getMessage());
        }
        return -1;
    }

    private static HttpGet getHttpGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("x-apikey", "c93fbb4e9e35f87a82ce02e276b7ab4ae85004327c3dc5b7a61b51b95110061a");
        httpGet.setHeader("accept", "application/json");
        return httpGet;
    }

    public void getURLInformation(String url) {
        String apiUrl = "https://www.virustotal.com/api/v3/urls/id";
        HttpClient httpClient = HttpClients.createDefault();

        try {
            HttpGet httpGet = getHttpGet(apiUrl);
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println("Response from VirusTotal API:\n" + responseBody);
            } else {
                System.err.println("Request to VirusTotal API failed with status code: " + response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            // Handle exceptions
            System.err.println("Error making GET request: " + e.getMessage());
        }
    }

    @PostMapping("/send-message")
    public ResponseEntity<?> sendMessage(@RequestBody ChatMessageDTO messageDTO) {
        try {
            if (chatService.containsURL(messageDTO.getContent())) {
                log.info("Message contains a URL. Scanning URL with VirusTotal...");
                int scanResult = scanURL(messageDTO.getContent());

                if (scanResult == 200) {
                    log.info("URL scan returned 200. Deleting the message and user.");
                    chatService.deleteMessageAndUser(messageDTO);
                    ResponseDetails deleteResponse = new ResponseDetails(LocalDateTime.now(), "Message and user deleted.", HttpStatus.OK.toString());
                    return ResponseEntity.ok(deleteResponse);
                } else {
                    log.info("URL scan did not return 200. Message not deleted.");
                }
            } else {
                log.info("Message does not contain a URL. Saving the message...");
                chatService.sendMessage(messageDTO);
            }
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Message processed successfully.", HttpStatus.OK.toString());
            return ResponseEntity.ok(responseDetails);
        } catch (AppException ex) {
            ResponseDetails errorDetails = new ResponseDetails(LocalDateTime.now(), ex.getMessage(), HttpStatus.BAD_REQUEST.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
        }
    }

}
