package xyz.chener.zp.common.utils;


import com.google.common.io.BaseEncoding;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.List;

import static xyz.chener.zp.common.utils.Auth2FAUtils.TwoFactorAuthenticator.*;

public class Auth2FAUtils {



    public static class TwoFactorAuthenticator {

        private static final int DIGITS = 6;
        private static final int TIME_STEP_SECONDS = 30;
        private static final String HMAC_ALGORITHM = "HmacSHA1";


        public static String generateSecretKey() {
            byte[] bytes = new byte[32];
            new java.security.SecureRandom().nextBytes(bytes);
            BaseEncoding base32 = BaseEncoding.base32().omitPadding().upperCase();
            return base32.encode(bytes);
        }


        public static String generateTOTP(String secretKey,Integer timeOffset) throws NoSuchAlgorithmException, InvalidKeyException {
            long timeStepMillis = TIME_STEP_SECONDS * 1000;
            long currentTimeMillis = Instant.now().toEpochMilli() + timeOffset;
            long counter = currentTimeMillis / timeStepMillis;

            StringBuilder counterHex = new StringBuilder(Long.toHexString(counter));
            while (counterHex.length() < 16) {
                counterHex.insert(0, "0");
            }

            byte[] keyBytes = BaseEncoding.base32().decode(secretKey);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, HMAC_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(secretKeySpec);
            byte[] hmacResult = mac.doFinal(hexStr2Bytes(counterHex.toString()));

            int offset = hmacResult[hmacResult.length - 1] & 0xF;
            int truncatedHash = hashToInt(hmacResult, offset) & 0x7FFFFFFF;
            int pinValue = truncatedHash % (int) Math.pow(10, DIGITS);
            return String.format("%0" + DIGITS + "d", pinValue);
        }

        private static byte[] hexStr2Bytes(String hex) {
            byte[] bArray = new byte[hex.length() / 2];
            for (int i = 0; i < hex.length() / 2; i++) {
                int index = i * 2;
                String subStr = hex.substring(index, index + 2);
                int byteValue = Integer.parseInt(subStr, 16);
                bArray[i] = (byte) byteValue;
            }
            return bArray;
        }

        private static int hashToInt(byte[] bytes, int start) {
            return ((bytes[start] & 0x7f) << 24) |
                    ((bytes[start + 1] & 0xff) << 16) |
                    ((bytes[start + 2] & 0xff) << 8) |
                    (bytes[start + 3] & 0xff);
        }


        public static boolean verifyCode(String code,String secretKey){
            try {
                return generateTOTP(secretKey,0).equals(code)
                        || generateTOTP(secretKey,-1000*30).equals(code)
                        || generateTOTP(secretKey,-2000*30).equals(code)
                        || generateTOTP(secretKey,1000*30).equals(code);
            }catch (Exception exception){
                return false;
            }
        }

        public static String generateQrCode(String key){
            //otpauth://totp.chener.xyz?secret=6HPH5373NXGO6M7K&issuer=zjwlgr
            return "otpauth://chener.xyz?secret="+key+"&issuer=zpadmin";
        }
    }

}
