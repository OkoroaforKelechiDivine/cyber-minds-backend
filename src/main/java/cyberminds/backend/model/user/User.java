package cyberminds.backend.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
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

    private Boolean isVerified;
}
