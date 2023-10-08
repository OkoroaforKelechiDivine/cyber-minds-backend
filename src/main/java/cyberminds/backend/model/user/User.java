package cyberminds.backend.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    private String id;

    private String displayName;

    private String profilePictureUrl;

    private String firstName;

    private String lastName;

    private String password;

    private UserStatus status;

    private String email;

    private String phoneNNumber;

    private LocalDateTime lastActive;

    private LocalDateTime createdDate;

    private Boolean isVerified;
}
