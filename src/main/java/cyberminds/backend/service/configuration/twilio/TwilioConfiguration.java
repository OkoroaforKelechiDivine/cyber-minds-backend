package cyberminds.backend.service.configuration.twilio;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@Data
@ConfigurationProperties(prefix = "twilio")
public class TwilioConfiguration {

    private String accountSid;
    private String authToken;
    private String phoneNumber;
}
