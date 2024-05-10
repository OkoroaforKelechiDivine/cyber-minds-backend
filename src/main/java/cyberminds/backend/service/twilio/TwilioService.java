package cyberminds.backend.service.twilio;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import cyberminds.backend.service.auth.AuthServiceImplementation;
import cyberminds.backend.service.configuration.twilio.TwilioConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class TwilioService {

    static Map<String, String> otpMap = new HashMap<>();

    @Autowired
    private static TwilioConfiguration twilioConfiguration;
    static String otp = AuthServiceImplementation.generateVerificationCode();
    static String message = "Hello, Your OTP is " + otp + ". Use this 4 digits code to verify your account";

    public static void sendSMS(String phoneNumber) {
        Message.creator(
                new PhoneNumber(phoneNumber), // to
                new PhoneNumber(twilioConfiguration.getPhoneNumber()), // from
                message // message
        ).create();
        otpMap.put(phoneNumber, otp);
        log.info("SMS sent to {} successfully", phoneNumber);
    }

    public String validateOTP(String phoneNumber, String otp) throws Exception {
        if (otp.equals(otpMap.get(phoneNumber))){
            return "OTP IS CORRECT";
        }
        else{
            throw new Exception("OTP IS NOT CORRECT");
        }
    }

}
