package cyberminds.backend.repository.user;

import cyberminds.backend.model.user.User;
import cyberminds.backend.model.user.UserStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Slf4j
public class UserRepositoryTest {

    User user;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void setUp() {
       user = new User();
    }

    @Test
    public void test_createUserAccount(){
        user.setStatus(UserStatus.ONLINE);
        user.setDisplayName("Death");
        assertDoesNotThrow(() -> userRepository.save(user));
        assertThat(user).isNotNull();
        log.info("User saved successfully -->{}", user);
    }
}