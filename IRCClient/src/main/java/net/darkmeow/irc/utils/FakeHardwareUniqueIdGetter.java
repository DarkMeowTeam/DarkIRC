package net.darkmeow.irc.utils;

import java.io.File;
import java.nio.file.Files;
import java.util.UUID;

public final class FakeHardwareUniqueIdGetter {

    public static final File FILE_ID = new File(System.getProperty("user.home"), ".hardware_unique_id");

    private FakeHardwareUniqueIdGetter() {

    }

    /**
     * 获取本机设备码
     * 算法不严谨 只能在 Windows 上使用
     *
     * @return 设备码
     */
    public static String get() {
        try {
            if (FILE_ID.exists()) {
                return new String(Files.readAllBytes(FILE_ID.toPath())).trim();
            } else {
                String newId = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                Files.write(FILE_ID.toPath(), newId.getBytes());
                return newId;
            }
        } catch (Exception ignored) {
            return "error";
        }
    }
}
