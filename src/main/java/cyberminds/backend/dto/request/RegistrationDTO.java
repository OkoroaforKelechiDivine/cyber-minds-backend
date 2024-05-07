package cyberminds.backend.dto.request;

import cyberminds.backend.model.user.Gender;
import lombok.Data;

@Data
public class RegistrationDTO {

    private String firstName;

    private String lastName;

    private String gender;

    private String password;

    private String phoneNumber;
}
