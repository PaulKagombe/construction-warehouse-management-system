package util;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordUtil {

    // Hash a plain text password
    public static String hashPassword(String plainPassword) {
        return BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray());
    }

    // Verify a plain text password against a hashed one
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        BCrypt.Result result = BCrypt.verifyer().verify(
                plainPassword.toCharArray(),
                hashedPassword
        );
        return result.verified;
    }

}