package cyberminds.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ForgotPasswordRequestDTO {

    private String phoneNumber;
}
