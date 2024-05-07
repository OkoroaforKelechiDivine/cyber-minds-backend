package cyberminds.backend.dto.request;

import lombok.Data;

@Data
public class RegistrationDTO {

    private String firstName;

    private String lastName;

    private String password;

    private String phoneNumber;
}
