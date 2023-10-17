package com.kstechnologies.bmibeta;

import android.util.Base64;
import android.util.Log;


import org.apache.commons.codec.binary.Hex;

import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by pparida on 12/3/17.
 */

public class MuacDataCrypto
{

    public static final String SSL_PROVIDER = "AndroidOpenSSL";
    public static final String PROVIDER = "BC"; //BouncyCastle

    public static final String TMF_ALGORITHM = "PKIX";
    //public static final String KEY_FACTORY_ALGORITHM = "RSA";
    //public static final String KEY_GENERATOR_ALGORITHM = "AES";
    public static final String KEYSTORE_TYPE = "PKCS12";
    public static final String KEYSTORE_FILE_NAME = "trustedCerts";
    public static final String KEYSTORE_PASSPHRASE = "passphrase";

    private static final String ALGO_RSA = "RSA";
    private static final String ALGO_AES = "AES";

    private static final String CIPHER_AES_TRANSFORMATION = "AES/GCM/NoPadding"; //AES/EAX/NoPadding not available on Android.
    private static final String CIPHER_RSA_TRANSFORMATION = "RSA/ECB/OAEPWithSHA1AndMGF1Padding";
    private static final int AES_KEY_SIZE = 128; // In bits. Equals 16 bytes.
    private static final int NONCE_SIZE = 16; // In bytes.
    private static final int TAG_SIZE = 16; // In bytes.

    private static final String SERVER_RSA_PUBLIC_KEY =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApvcyZCpb5T1cF1o8J/XN" +
            "z8z/JBFiLzxT818MgflmWcqpxh3o4vlFfgVkPVf6wJK4szh3my8U+CUuNXdySDko" +
            "oVJ8y4KCnfZJwJWPPQiOx3w6axxgepMqI3vu2+/IPHMACcyLTLV6epCzarDTqZhi" +
            "z4hgLBlYK/x9OkPFJzxAkvhGJqkHVr1iJRqtp61Pjcdjb1ZAI4+lsvYekQvfPU4m" +
            "uXSaPaeQDfJnaOW0vXT7MPslISkb8HV3roCiSeffUFrykz2PURvnYiRcLZO+ZE1v" +
            "6bBIxp6VyQHdbbcq0izAUSS/5CfZRbuVdjrngWddZSzN83lQheI++F502/JXfZ6N" +
            "gwIDAQAB";

    private static void listCryptoProvidersInfo() {
        /*
        for(Provider provider : Security.getProviders()) {
            Log.d("LIST_CRYPTO_PROVIDERS",
                    provider.getName() + " : " + provider.getInfo());
            for(String key : provider.stringPropertyNames()) {
                Log.d("CRYPTO_PROPERTY",
                        "\t" + key + " :\t" + provider.getProperty(key));
            }
            for(Provider.Service s : provider.getServices()) {
                Log.d("CRYPTO_SERVICE",
                        "\t Algo:" + s.getAlgorithm() + " ::\t" + s.getType() + " : " + s.toString());
            }
        }*/

        try {
            Log.d("********PPP********", "Begin Cipher Check");
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");

            Log.d("********PPP********", "Begin KeyGen Check");
            KeyGenerator keygen = KeyGenerator.getInstance("AES", "BC");

            Log.d("********PPP********", "Begin KeyStore Check");
            KeyStore ks = KeyStore.getInstance("PKCS12", "BC");
            Log.d("***** KeyStore *****", ks.getProvider().getInfo());

            Log.d("********PPP********", "Begin TMF Check");
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX", "AndroidNSSP");
            Log.d("***** TMF *****", tmf.getProvider().getInfo());

            Log.d("********PPP********", "Begin SSLContext Check");
            SSLContext sslContext = SSLContext.getInstance("SSL", "AndroidOpenSSL");
            Log.d("***** SSL *****", sslContext.getProvider().getInfo());

            Log.d("********PPP********", "End. All Checks passed.");
        }
        catch (NoSuchPaddingException | NoSuchAlgorithmException | NoSuchProviderException e) {
            Log.e("NO_CIPHER_INSTANCE", e.getMessage() + " : " + e.toString());
        }
        catch (Exception e) {
            Log.e("NO_CIPHER", e.getMessage() + " :: " + e.toString());
        }

    }

    public static void checksForDebug() {
        //listCryptoProvidersInfo(); Uncomment to check for Security Providers info.
        try {
            //MuacDataCrypto.rsaEncryptAndHexify("I am a string".getBytes());
            encrypt("I am a string");
        } catch(Exception e){Log.e("***** PPP ERROR *******", e.toString());}

    }

    /**
     *
     * @param plainData
     * @return
     * @throws Exception
     */
    public static String encrypt(String plainData) throws Exception {
        //Generate 16 byte session key
        SecureRandom random = new SecureRandom(); //SecureRandom.getInstance();
        KeyGenerator keygen = KeyGenerator.getInstance(ALGO_AES, PROVIDER);
        keygen.init(AES_KEY_SIZE, random);
        SecretKey sessionKey = keygen.generateKey();

        byte[] sessionKeyRaw = sessionKey.getEncoded();

        //Encrypt session key using RSA
        String s1 = rsaEncryptAndHexify(sessionKeyRaw);

        //Encrypt plainData with session key using AES
        final byte[] nonce = new byte[NONCE_SIZE];
        random.nextBytes(nonce);
        String s2 = aesEncryptAndHexify(sessionKeyRaw, plainData, nonce);

        Log.d("**** ENCRYPTED ****", "s1 = " + s1);
        Log.d("**** ENCRYPTED ****", "s2 = " + s2);
        return s1+s2;
    }

    private static String aesEncryptAndHexify(byte[] key, String plainData, byte[] nonce) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key, ALGO_AES);
        GCMParameterSpec paramSpec = new GCMParameterSpec(TAG_SIZE*8, nonce);
        //IvParameterSpec paramSpec = new IvParameterSpec(nonce);
        Cipher cipher = Cipher.getInstance(CIPHER_AES_TRANSFORMATION, PROVIDER);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, paramSpec);
        cipher.updateAAD("xyz".getBytes());
        byte[] ciphertext = cipher.doFinal(plainData.getBytes());
        return bytesToHex(nonce) + bytesToHex(ciphertext);//Hex.encodeHexString(ciphertext);
    }

    private static String rsaEncryptAndHexify(byte[] sessionKeyToBeEncrypted) throws Exception {
        byte[] publicKeyBytes = Base64.decode(SERVER_RSA_PUBLIC_KEY, Base64.DEFAULT);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGO_RSA, PROVIDER);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        Log.d("***** PPP *****", publicKey.getFormat() + " :: " + publicKey.getAlgorithm());
        Cipher cipher = Cipher.getInstance(CIPHER_RSA_TRANSFORMATION, PROVIDER);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] ciphertext = cipher.doFinal(sessionKeyToBeEncrypted);
        Log.d("***** PPP *****", "In = " + bytesToHex(sessionKeyToBeEncrypted) + " :: Out = " + bytesToHex(ciphertext));
        return bytesToHex(ciphertext);//Hex.encodeHexString(ciphertext);
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
