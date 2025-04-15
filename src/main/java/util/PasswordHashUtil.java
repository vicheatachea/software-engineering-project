package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHashUtil {

	private static final Logger logger = LoggerFactory.getLogger(PasswordHashUtil.class);

	private PasswordHashUtil() {
		// Prevent instantiation
	}

	public static String generateSalt() {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[16];
		random.nextBytes(salt);
		return Base64.getEncoder().encodeToString(salt);
	}

	public static String hashPassword(String password, String salt) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(salt.getBytes());
			byte[] hashedPassword = md.digest(password.getBytes());
			return Base64.getEncoder().encodeToString(hashedPassword);
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	public static Boolean verifyPassword(String inputPassword, String storedHash, String salt) {
		String hashedInputPassword = hashPassword(inputPassword, salt);
		assert hashedInputPassword != null;
		return hashedInputPassword.equals(storedHash);
	}
}
