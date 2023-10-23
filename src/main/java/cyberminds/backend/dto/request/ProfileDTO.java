package cyberminds.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {

    private String username;

    private Date dateOfBirth;

    private String bio;

    private String profileImageUrl;

    private String hobbies;
}
