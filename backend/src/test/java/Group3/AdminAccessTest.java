package Group3;

import Group3.model.User;
import Group3.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
@ActiveProfiles("test")
public class AdminAccessTest {

    @Autowired private WebApplicationContext context;
    @Autowired private UserRepository userRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = webAppContextSetup(context).build();
    }

    @Test
    void nonAdminGets403_adminGets200() throws Exception {
        // non-admin
        User u1 = new User();
        u1.setEmail("user@test.com");
        u1.setFullName("Normal User");
        u1.setAdmin(false);
        u1 = userRepository.save(u1);

        // admin
        User u2 = new User();
        u2.setEmail("admin@test.com");
        u2.setFullName("Admin User");
        u2.setAdmin(true);
        u2 = userRepository.save(u2);

        mockMvc.perform(get("/admin/ping").header("X-User-Id", u1.getId().toString()))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/admin/ping").header("X-User-Id", u2.getId().toString()))
                .andExpect(status().isOk());
    }
}