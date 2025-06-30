# crypto1

Comparison of AES cipher modes: (Block size 128 bits, Key size 128, 192 or 256 bits)

Property                               | ECB   | CBC   | CFB   | OFB   | CTR   | GCM   | EAX
---------                            | :---: | :---: | :---: | :---: | :---: | :---: | :---:
Multi-block Semantic Security |:x:| :white_check_mark: &dagger; | :white_check_mark: &dagger; | :white_check_mark: &dagger; | :white_check_mark: &Dagger; | :white_check_mark: &Dagger; | :white_check_mark: &Dagger; |
No Padding Needed. Is a stream &ast; |:x:|:x:                | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: | 
Parallel Encrypt                     |:white_check_mark: |:x:| :x:                | :x:                | :white_check_mark: | :white_check_mark: | 
Parallel Decrypt                     |:white_check_mark:     | :white_check_mark: | :white_check_mark: | :x: |:white_check_mark:| :white_check_mark: | 
AEAD                                 |:x:|:x: | :x: | :x: | :x: | :white_check_mark: | :white_check_mark: |


&ast; Stream because plaintext block is not input to the Encrypt/Decrypt block function. Hence padding of plaintext to match blocksize is not needed.
Plaintext is XOR'ed to the output of the Encrypt/Decrypt block function. So output of the E/D block functions can be thought of as a stream cipher.

&dagger; When used with randomly chosen IV

&Dagger; When used with one-time chosen nonce

---

JCA/JCE Documentation:  
JAVA8 https://docs.oracle.com/javase/8/docs/technotes/guides/security/crypto/CryptoSpec.html   
JAVA9 https://docs.oracle.com/javase/9/security/java-cryptography-architecture-jca-reference-guide.htm   

JSSE Documentation:   
JAVA8 https://docs.oracle.com/javase/8/docs/technotes/guides/security/jsse/JSSERefGuide.html   
JAVA9 https://docs.oracle.com/javase/9/security/java-secure-socket-extension-jsse-reference-guide.htm

TODO: Add documentation of how to use JSSE and JCE in combination for various cases eg using non-default `TrustManager` that are:
1. Blind/naive/pass-thru, for accepting self-signed certificates, 
2. Use custom truststores, eg a custom certca,
3. Use conditional hierarchy of default keystore, custom keystore or blind, as required.

---

## Complexity of Factoring

Factoring is known to be in NP but not known (nor believed) to be in P or NP-complete.

Therefore, factoring is known to be no harder than an NP-complete problem.

---

## PKCS (Public Key Cryptography Standard)

- **PKCS #5**: Used in CBC padding for block ciphers.
- **PKCS #1**: Used to expand given key (AES key) from 128 to 1024 bits before sharing and sending via RSA. Because, sending 128 bits encrypted in RSA, is vulnerable to Meet-in-the-Middle attack.
  - **PKCS #1 v1.5**: v1.5 is still vulnerable to attack, since it gives quick error on not finding the 02 MSB in the RSA-encrypted ciphertext.
  - **PKCS #1 v2.0 = OAEP (Optimal Asymmetric Encryption Padding)**: Fixes the vulnerability of PKCS #1 v1.5.

## PKCS #1 vs ISO Key Expansion

The ISO way is to generate random 1024 bit value (called, say, X) and send X RSA-encrypted to the other party. So X will be secure from RSA MITM attack since it is long enough. Then both parties use Hash(X) ie SH128(X) as AES key K, which will be 128 bits.

But this is impractical, because most apps want fixed known 128 bits AES key K. So instead of generating large X and then doing K = H(X), we construct large X from a given 128 bit K itself, using PKCS #1.

---

## RSA constraints

Do not RSA-encrypt messages/keys of size much less than RSA modulus size. This will be open to MITM attack.

Do not encrypt entire large messages with RSA either. This will be too inefficient compared to AES. Asymmetric algorithms are inefficient compared to symmetric algorithms.

So, only RSA-encrypt values of size equal to RSA modulus. 

AES needs at least 128 bit key-size / block-size to be secure. But RSA needs at least 2048 bit modulus size to be secure.

If we encrypt a 128 bit value using RSA, MITM attack is feasible.
