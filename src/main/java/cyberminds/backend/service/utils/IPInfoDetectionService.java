package cyberminds.backend.service.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IPInfoDetectionService {
    @Value("${ipinfo.api.url}")
    private String ipInfoApiUrl;

    public boolean isSuspiciousUrl(String url) {
        String apiUrl = ipInfoApiUrl + "/" + url;
        RestTemplate restTemplate = new RestTemplate();
        IPInfoResponse response = restTemplate.getForObject(apiUrl, IPInfoResponse.class);

        // Check if the IP address is associated with known malicious activity.
        if (response != null && response.threat() != null) {
            return response.threat().isMalicious();
        }

        return false;
    }
}

class IPInfoResponse {
    private ThreatInfo threat;

    public ThreatInfo threat() {
        return threat;
    }
}

class ThreatInfo {
    private boolean is_malicious;

    public boolean isMalicious() {
        return is_malicious;
    }
}
