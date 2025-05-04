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
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class CryptUtils {

    public static SecretKey createNewSharedKey() {
        try
        {
            KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
            keygenerator.init(128);
            return keygenerator.generateKey();
        }
        catch (NoSuchAlgorithmException nosuchalgorithmexception)
        {
            throw new Error(nosuchalgorithmexception);
        }
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

    public static byte[] signCode(String code, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(code.getBytes(StandardCharsets.UTF_8));
        return signature.sign();
    }

    public static boolean verifyCode(String code, byte[] signatureBytes, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(code.getBytes(StandardCharsets.UTF_8));
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
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
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
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }


    public static byte[] encryptData(Key key, byte[] data)
    {
        return cipherOperation(1, key, data);
    }

    public static byte[] decryptData(Key key, byte[] data)
    {
        return cipherOperation(2, key, data);
    }

    private static byte[] cipherOperation(int opMode, Key key, byte[] data)
    {
        try
        {
            return createTheCipherInstance(opMode, key.getAlgorithm(), key).doFinal(data);
        }
        catch (IllegalBlockSizeException illegalblocksizeexception)
        {
            illegalblocksizeexception.printStackTrace();
        }
        catch (BadPaddingException badpaddingexception)
        {
            badpaddingexception.printStackTrace();
        }

        return null;
    }
    private static Cipher createTheCipherInstance(int opMode, String transformation, Key key)
    {
        try
        {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(opMode, key);
            return cipher;
        }
        catch (InvalidKeyException invalidkeyexception)
        {
            invalidkeyexception.printStackTrace();
        }
        catch (NoSuchAlgorithmException nosuchalgorithmexception)
        {
            nosuchalgorithmexception.printStackTrace();
        }
        catch (NoSuchPaddingException nosuchpaddingexception)
        {
            nosuchpaddingexception.printStackTrace();
        }

        return null;
    }


    @NotNull
    public static PublicKey decodePublicKey(byte[] encodedKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        EncodedKeySpec encodedkeyspec = new X509EncodedKeySpec(encodedKey);
        KeyFactory keyfactory = KeyFactory.getInstance("RSA");
        return keyfactory.generatePublic(encodedkeyspec);
    }

    public static @NotNull SecretKey decryptSharedKey(PrivateKey key, byte[] secretKeyEncrypted)
    {
        return new SecretKeySpec(decryptData(key, secretKeyEncrypted), "AES");
    }


    public static Cipher createNetCipherInstance(int opMode, Key key)
    {
        try
        {
            Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            cipher.init(opMode, key, new IvParameterSpec(key.getEncoded()));
            return cipher;
        }
        catch (GeneralSecurityException generalsecurityexception)
        {
            throw new RuntimeException(generalsecurityexception);
        }
    }

}
