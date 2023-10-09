package cyberminds.backend.service.utils;

import java.util.Random;
public class OTPGenerator {
    public static String generateOTP() {
        Random random = new Random();
        int otp = 1000 + random.nextInt(9000);
        return Integer.toString(otp);
    }
}