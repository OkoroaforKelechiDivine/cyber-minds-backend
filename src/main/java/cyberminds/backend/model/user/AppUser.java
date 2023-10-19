package cyberminds.backend.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUser {

    @Id
    private String id;

    private String displayName;

    private String profilePictureUrl;

    private String firstName;

    private Gender gender;

    private String lastName;

    private String password;

    private UserStatus status;

    private Role role;

    private String email;

    private String phoneNNumber;

    private Set<String> following = new HashSet<>();

    private Set<String> followers = new HashSet<>();

    private LocalDateTime lastActive;

    private String createdDate;

    private Boolean isVerified;
}
