package net.darkmeow.irc.utils;

import org.jetbrains.annotations.NotNull;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class CryptUtils {

    public static SecretKey createNewSharedKey() throws NoSuchAlgorithmException {
        KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
        keygenerator.init(128);
        return keygenerator.generateKey();
    }

    /**
     * 生成密钥对
     *
     * @return 密钥对
     */
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024);
        return generator.generateKeyPair();
    }

    public static byte[] signData(byte[] data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    public static boolean verifyData(byte[] data, byte[] signatureBytes, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(signatureBytes);
    }

    /**
     * 导出私钥到 PEM 文件（PKCS#8）
     *
     * @param privateKey 私钥本体
     * @param file 文件位置
     */
    public static void exportPrivateKeyToPEM(PrivateKey privateKey, File file) throws IOException {
        byte[] encoded = privateKey.getEncoded(); // PKCS#8
        String base64 = Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(encoded);
        String pem = "-----BEGIN PRIVATE KEY-----\n" + base64 + "\n-----END PRIVATE KEY-----";
        Files.write(file.toPath(), pem.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 导出公钥到 PEM 文件（X.509）
     *
     * @param publicKey 公钥本体
     * @param file 文件位置
     */
    public static void exportPublicKeyToPEM(PublicKey publicKey, File file) throws IOException {
        byte[] encoded = publicKey.getEncoded(); // X.509
        String base64 = Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(encoded);
        String pem = "-----BEGIN PUBLIC KEY-----\n" + base64 + "\n-----END PUBLIC KEY-----";
        Files.write(file.toPath(), pem.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 从 PEM 文件加载私钥
     *
     * @param file 文件位置
     * @return 私钥本体
     */
    public static PrivateKey loadPrivateKeyFromPEM(File file) throws Exception {
        String pem = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        pem = pem.replaceAll("-----\\w+ PRIVATE KEY-----", "").replaceAll("\\s+", "");
        byte[] decoded = Base64.getDecoder().decode(pem);
        return loadPrivateKeyFromByte(decoded);
    }

    /**
     * 从 PEM 文件加载公钥
     *
     * @param file 文件位置
     * @return 公钥本体
     */
    public static PublicKey loadPublicKeyFromPEM(File file) throws Exception {
        String pem = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        pem = pem.replaceAll("-----\\w+ PUBLIC KEY-----", "").replaceAll("\\s+", "");
        byte[] decoded = Base64.getDecoder().decode(pem);
        return loadPublicKeyFromByte(decoded);
    }

    /**
     * 从内存字节集加载私钥
     *
     * @param data 数据
     * @return 私钥本体
     */
    public static PrivateKey loadPrivateKeyFromByte(byte[] data) throws Exception {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(data);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 从内存字节集加载公钥
     *
     * @param data 数据
     * @return 公钥本体
     */
    public static PublicKey loadPublicKeyFromByte(byte[] data) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(data);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }


    public static byte[] encryptData(Key key, byte[] data) {
        return cipherOperation(1, key, data);
    }

    public static byte[] decryptData(Key key, byte[] data) {
        return cipherOperation(2, key, data);
    }

    private static byte[] cipherOperation(int opMode, Key key, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(key.getAlgorithm());
            cipher.init(opMode, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static PublicKey decodePublicKey(byte[] encodedKey) {
        try {
            EncodedKeySpec encodedkeyspec = new X509EncodedKeySpec(encodedKey);
            KeyFactory keyfactory = KeyFactory.getInstance("RSA");
            return keyfactory.generatePublic(encodedkeyspec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static @NotNull SecretKey decryptSharedKey(PrivateKey key, byte[] secretKeyEncrypted) {
        try {
            return new SecretKeySpec(decryptData(key, secretKeyEncrypted), "AES");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Cipher createNetCipherInstance(int opMode, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            cipher.init(opMode, key, new IvParameterSpec(key.getEncoded()));
            return cipher;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
