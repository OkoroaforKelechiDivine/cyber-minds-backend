package cyberminds.backend.dto.request;

import lombok.Data;

@Data
public class ValidateOtpDTO {

    private String email;

    private String otp;
}
