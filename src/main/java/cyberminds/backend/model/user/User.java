package cyberminds.backend.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String id;

    private String username;

    private String displayName;

    private String profilePictureUrl;

    private UserStatus status;

    private LocalDateTime lastActive;
}
