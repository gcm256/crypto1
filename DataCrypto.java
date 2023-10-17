import java.security.Security;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.GCMParameterSpec;

class DataCrypto {

  public static final String PROVIDER = "SunJCE";

  private static final String ALGO_AES = "AES";
  private static final String CIPHER_AES_TRANSFORMATION = "AES/GCM/NoPadding";

  private static final int AES_KEY_SIZE = 128; // In bits. Equals 16 bytes.
  private static final int NONCE_SIZE = 12; // In bytes.
  private static final int TAG_SIZE = 16; // In bytes.

  /**
   * Main. Demonstrates use of AEAD in Java using AES-GCM encryption.
   * Ref: https://docs.oracle.com/javase/7/docs/api/javax/crypto/Cipher.html
   */
  public static void main(String[] args) {
    System.out.println("Hello world!");
    //listCryptoProvidersInfo();
    //Provider p = Security.getProvider("SunJCE");
    try {
      // AES-GCM (AES encrypt/decrypt in GCM mode, which includes a TAG/MAC for authentication at the end of the ciphertext) is one of the AEAD that are CCA-secure.
      // CCA-security is more secure than CPA(Chosen Plaintext Attack)-security, as the former also includes a verifiable tag/mac.
      // Ref: https://docs.oracle.com/javase/7/docs/api/javax/crypto/Cipher.html
      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "SunJCE");
    }
    catch(Exception e) {
      System.out.println("ERROR: [Algorithm not Supported]" + e.getMessage());
    }
    String headerAAD = "[header = xyz]"; // Additional Data (AD) to be used in AEAD.
    String plaintext = "This is a plaintxt. I got it done and then some to do there...";
    System.out.println("Plaintext = " + plaintext);
    System.out.println("Header = " + headerAAD);
    try {
      // Note: encrypt() method itself creates the key, so each call to encrypt() will use new AES key.
      System.out.println("Encoded = " + encrypt(plaintext, headerAAD));
    }
    catch(Exception e) {
      System.out.println("ERROR During Encryption: " + e.getMessage());
    }
  }

  public static String encrypt(String plainData, String headerAAD) throws Exception {
    //Generate 16 byte session key
    SecureRandom random = new SecureRandom(); //SecureRandom.getInstance();
    KeyGenerator keygen = KeyGenerator.getInstance(ALGO_AES, PROVIDER);
    keygen.init(AES_KEY_SIZE, random);
    SecretKey sessionKey = keygen.generateKey();

    byte[] sessionKeyRaw = sessionKey.getEncoded();

    byte[] nonce = new byte[NONCE_SIZE];
    random.nextBytes(nonce);

    System.out.println("Keyyy = " + bytesToHex(sessionKeyRaw));
    System.out.println("Nonce = " + bytesToHex(nonce));
    String s2 = aesEncryptAndHexify(headerAAD,sessionKeyRaw, plainData, nonce);
    return s2;
  }

  private static String aesEncryptAndHexify(String headerAAD, byte[] key, String plainData, byte[] nonce) throws Exception {
    SecretKeySpec keySpec = new SecretKeySpec(key, ALGO_AES);
    GCMParameterSpec paramSpec = new GCMParameterSpec(TAG_SIZE*8, nonce);
    //IvParameterSpec paramSpec = new IvParameterSpec(nonce);
    Cipher cipher = Cipher.getInstance(CIPHER_AES_TRANSFORMATION, PROVIDER);
    cipher.init(Cipher.ENCRYPT_MODE, keySpec, paramSpec);
    cipher.updateAAD(headerAAD.getBytes()); // Add the optional Additional Associated Data for AEAD. Note: AAD will be checked during decrypt().
    byte[] ciphertext = cipher.doFinal(plainData.getBytes()); // The ciphertext[] inclused the 16-byte tag at the end.
    return bytesToHex(nonce) + bytesToHex(ciphertext);//Hex.encodeHexString(ciphertext);
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

  private static String bytesToHex2(byte[] hashInBytes) {

        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();

    }

  
  private static byte[] hexStringToByteArray(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                             + Character.digit(s.charAt(i+1), 16));
    }
    return data;
}


  private static void listCryptoProvidersInfo() {
    for(Provider provider : Security.getProviders()) {

      System.out.println(provider.getName() + " : " + provider.getInfo());
      for(Provider.Service s : provider.getServices()) {
        String algo = s.getAlgorithm();
        String ss = s.toString();
        //if(algo.toUpperCase().contains("GCM"))
        if(provider.getName().equals("SunJCE"))
        System.out.println("Algo:" + algo + "::\t" + s.getType() + " : " + s.toString());
      }
      System.out.println("============\n\n");
    }
  }

}
