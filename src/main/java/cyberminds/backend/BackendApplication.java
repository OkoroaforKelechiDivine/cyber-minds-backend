package cyberminds.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twilio.Twilio;
import cyberminds.backend.service.configuration.twilio.TwilioConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@SpringBootApplication
//@Profile("deployment")
@Profile("local")
@Slf4j
@Configuration
public class BackendApplication {

    @Autowired
    private TwilioConfiguration twilioConfiguration;

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
        log.info("Starting Backend Application");
    }

    @PostConstruct
    public void initTwilio(){
        Twilio.init(twilioConfiguration.getAccountSid(), twilioConfiguration.getAuthToken(), twilioConfiguration.getPhoneNumber());
    }

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
