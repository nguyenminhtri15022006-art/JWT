package vn.iotstar;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import vn.iotstar.models.LoginResponse;
import vn.iotstar.models.LoginUserModel;
import vn.iotstar.models.RegisterUserModel;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test API Đăng ký người dùng mới /auth/signup")
    void testRegisterUser() throws Exception {
        RegisterUserModel registerUser = new RegisterUserModel(
                "tester@gmail.com",
                "123456",
                "Nguyen Van Test"
        );

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("tester@gmail.com"))
                .andExpect(jsonPath("$.fullName").value("Nguyen Van Test"));
    }

    @Test
    @DisplayName("Test API Đăng nhập /auth/login trả về Nimbus JWT Token")
    void testLoginUser() throws Exception {
        LoginUserModel loginUser = new LoginUserModel(
                "24110359@student.hcmute.edu.vn",
                "123456"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.expiresIn").value(3600000));
    }

    @Test
    @DisplayName("Test bảo vệ endpoint /users/me: Không có token bị chặn, có token hợp lệ thành công")
    void testAccessProtectedEndpoint() throws Exception {
        // 1. Khong co Bearer token -> 403 Forbidden
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isForbidden());

        // 2. Dang nhap de lay token
        LoginUserModel loginUser = new LoginUserModel(
                "24110359@student.hcmute.edu.vn",
                "123456"
        );

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUser)))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), LoginResponse.class);
        String token = response.getToken();

        // 3. Gui request kem Bearer token -> 200 OK va lay duoc thong tin
        mockMvc.perform(get("/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("24110359@student.hcmute.edu.vn"))
                .andExpect(jsonPath("$.fullName").value("Nguyễn Minh Trí"));
    }
}
