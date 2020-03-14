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


