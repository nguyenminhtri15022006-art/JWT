package vn.iotstar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import vn.iotstar.entity.User;
import vn.iotstar.services.JwtService;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User testUser;
    private final String secretKey = "3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b";
    private final long expirationTime = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", expirationTime);

        testUser = new User();
        testUser.setId(1);
        testUser.setFullName("Nguyễn Minh Trí");
        testUser.setEmail("24110359@student.hcmute.edu.vn");
        testUser.setPassword("encodedPassword");
    }

    @Test
    @DisplayName("Test tạo Token với Nimbus JOSE+JWT thành công")
    void testGenerateToken() {
        String token = jwtService.generateToken(testUser);
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3, "Token JWT phai co dung 3 phan header.payload.signature");
    }

    @Test
    @DisplayName("Test trích xuất username từ Nimbus JWT Token")
    void testExtractUsername() {
        String token = jwtService.generateToken(testUser);
        String username = jwtService.extractUsername(token);
        assertEquals("24110359@student.hcmute.edu.vn", username);
    }

    @Test
    @DisplayName("Test kiểm tra Token hợp lệ")
    void testIsTokenValid() {
        String token = jwtService.generateToken(testUser);
        boolean isValid = jwtService.isTokenValid(token, testUser);
        assertTrue(isValid, "Token hop le phai tra ve true");
    }

    @Test
    @DisplayName("Test kiểm tra Token không hợp lệ với User khác")
    void testTokenInvalidForDifferentUser() {
        String token = jwtService.generateToken(testUser);

        User anotherUser = new User();
        anotherUser.setEmail("other@student.hcmute.edu.vn");

        boolean isValid = jwtService.isTokenValid(token, anotherUser);
        assertFalse(isValid, "Token khong hop le neu username khong khop");
    }

    @Test
    @DisplayName("Test Token hết hạn trả về false")
    void testExpiredToken() {
        // Set thoi gian het han la -1000ms (da het han tu qua khu)
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L);
        String expiredToken = jwtService.generateToken(testUser);

        boolean isValid = jwtService.isTokenValid(expiredToken, testUser);
        assertFalse(isValid, "Token da het han phai tra ve false");
    }
}
