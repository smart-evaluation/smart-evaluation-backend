
package com.smartexam.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncoderTest {

    @Test
    void testPasswordEncryption() {

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder(12);

        String rawPassword = "SmartExam@123";
        String encodedPassword =
                encoder.encode(rawPassword);
        System.out.println("Encoded: " + encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(
                encoder.matches(rawPassword, encodedPassword)
        );
        assertFalse(
                encoder.matches("WrongPassword", encodedPassword)
        );
    }
}
