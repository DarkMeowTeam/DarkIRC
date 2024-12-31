package net.darkmeow.irc.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SuppressWarnings("SpellCheckingInspection")
public class DeviceUtils {

    private DeviceUtils() {

    }

    /**
     * 获取本机设备码
     * 算法不严谨 只能在 Windows 上使用
     *
     * @return 设备码
     */
    public static String getDeviceId() {
        try {
            final String base = System.getenv("COMPUTERNAME") +
                System.getProperty("user.name") +
                System.getenv("PROCESSOR_IDENTIFIER") +
                System.getenv("PROCESSOR_LEVEL");

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(base.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : md.digest()) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return "Error";
        }
    }
}
