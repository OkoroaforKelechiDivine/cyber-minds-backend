package cyberminds.backend.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import cyberminds.backend.BackendApplication;
import cyberminds.backend.dto.request.FriendsDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(classes = BackendApplication.class)
@AutoConfigureMockMvc
@WebAppConfiguration
@Slf4j
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void test_followFriendSuccess() throws Exception {
        FriendsDTO friendsDTO = new FriendsDTO();
        friendsDTO.setFriendId("");
        friendsDTO.setUserId("");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/follow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(friendsDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        log.info("Started Following --> {}", friendsDTO.getFriendId());
    }


}
