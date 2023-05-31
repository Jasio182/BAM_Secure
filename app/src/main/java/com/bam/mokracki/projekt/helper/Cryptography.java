package com.bam.mokracki.projekt.helper;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Cryptography {

    private static final String TRANSFORMATION = KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7;
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String SEPARATOR = ",";

    private String keyName;
    private KeyStore keyStore;
    private SecretKey secretKey;


    public static String ToBase64(String login, String password)
    {
        StringBuilder concated = new StringBuilder();
        concated.append(login).append(':').append(password);
        byte[] data = concated.toString().getBytes();
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public Cryptography(String keyName) throws Exception {
        this.keyName = keyName;
        initKeystore();
        loadOrGenerateKey();
    }

    private void loadOrGenerateKey() throws Exception {
        getKey();
        if (secretKey == null) generateKey();
    }

    private void initKeystore() throws Exception {
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);
    }

    private void getKey() {
        try {
            final KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(keyName, null);
            if (secretKeyEntry != null) secretKey = secretKeyEntry.getSecretKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateKey() throws Exception {
        final KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
        final KeyGenParameterSpec keyGenParameterSpec =
                new KeyGenParameterSpec.Builder(
                        keyName,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build();
        keyGenerator.init(keyGenParameterSpec);
        secretKey = keyGenerator.generateKey();
    }

    public String encryptKeyStore(String toEncrypt) throws Exception {
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        String iv = Base64.encodeToString(cipher.getIV(), Base64.DEFAULT);
        String encrypted = Base64.encodeToString(cipher.doFinal(toEncrypt.getBytes(StandardCharsets.UTF_8)), Base64.DEFAULT);
        return encrypted + SEPARATOR + iv;
    }


    public String decryptKeyStore(String toDecrypt) throws Exception {
        String[] parts = toDecrypt.split(SEPARATOR);
        if (parts.length != 2)
            throw new AssertionError("String to decrypt must be of the form: 'BASE64_DATA" + SEPARATOR + "BASE64_IV'");
        byte[] encrypted = Base64.decode(parts[0], Base64.DEFAULT),
                iv = Base64.decode(parts[1], Base64.DEFAULT);
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        IvParameterSpec spec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
        return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
    }

    public static void encryptFile(File decryptedFile, String password, File encryptedFile) throws Exception {
        SecretKeySpec sks = new SecretKeySpec(password.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        byte[] iv = cipher.getIV();
        FileInputStream fis = new FileInputStream(decryptedFile);
        FileOutputStream fos = new FileOutputStream(encryptedFile.getPath());
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        fos.write(iv);
        int b;
        byte[] d = new byte[8];
        while ((b = fis.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        cos.flush();
        cos.close();
        fis.close();
    }

    public static void decryptFile(File decryptedFile, String password, File encryptedFile) throws Exception {
        SecretKeySpec sks = new SecretKeySpec(password.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        FileInputStream fis = new FileInputStream(encryptedFile);
        byte[] iv = new byte [16];
        fis.read(iv);
        IvParameterSpec spec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, sks, spec);
        FileOutputStream fos = new FileOutputStream(decryptedFile.getPath());
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        int b;
        byte[] d = new byte[8];
        while ((b = fis.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        cos.flush();
        cos.close();
        fis.close();
    }
}
