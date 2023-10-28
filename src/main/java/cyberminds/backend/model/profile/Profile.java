package cyberminds.backend.model.profile;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Profile {

    @Id
    private String id;

    private String username;

    private Date dateOfBirth;

    private String bio;

    private String userId;

    private String profileImageUrl;

    private String hobbies;

}
