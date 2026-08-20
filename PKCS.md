# PKCS

| PKCS         | Full name                              | Purpose / key idea                                      | Common examples                  |
| ------------ | -------------------------------------- | ------------------------------------------------------- | -------------------------------- |
| **PKCS #1**  | RSA Cryptography Standard              | RSA encryption, signatures, and key formats             | RSA-PSS, RSA-OAEP, RSA keys      |
| **PKCS #3**  | Diffie–Hellman Key Agreement           | Establishes a shared secret using Diffie–Hellman        | DH key exchange                  |
| **PKCS #5**  | Password-Based Cryptography            | Derives cryptographic keys from passwords[^1]           | PBKDF2                           |
| **PKCS #7**  | Cryptographic Message Syntax (CMS)     | Encapsulates encrypted/signed data[^2]                  | S/MIME, certificates             |
| **PKCS #8**  | Private-Key Information Syntax         | Standard format for storing private keys                | RSA, EC, EdDSA private keys      |
| **PKCS #9**  | Selected Attribute Types               | Defines attributes used with other PKCS standards       | Email/name attributes            |
| **PKCS #10** | Certification Request Syntax           | Format for certificate signing requests (CSRs)          | `.csr` files submitted to CAs    |
| **PKCS #11** | Cryptographic Token Interface          | API for cryptographic hardware/tokens                   | HSMs, smart cards, USB tokens    |
| **PKCS #12** | Personal Information Exchange Syntax   | Packages private keys and certificates                  | `.p12` / `.pfx` files            |
| **PKCS #13** | Elliptic Curve Cryptography            | Proposed standard for ECC                               | Largely obsolete/not widely used |
| **PKCS #15** | Cryptographic Token Information Format | Standardizes information stored on cryptographic tokens | Smart-card token data            |


> [!TIP]
> * #1 = RSA
> * #5 = passwords
> * #7 = messages
> * #8 = private keys
> * #10 = CSR
> * #11 = crypto tokens/HSMs
> * #12 = certificate + private-key bundle.

## PKCS#5

> [!NOTE]
> Historically, PKCS #5 is associated with password-based cryptography and the padding scheme often called “PKCS#5 padding.”
>

| Aspect                    | PKCS #5                                                                                                                                                  |
| ------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Original purpose**      | Password-Based Encryption (PBE), originally defined for 64-bit block ciphers such as DES                                                                 |
| **Padding**               | Defines the padding method commonly called **PKCS#5 padding**                                                                                            |
| **Padding rule**          | Add **N bytes**, where `N = block_size − (data_length mod block_size)`; every added byte has value `N`                                                   |
| **Example**               | For an 8-byte block cipher, if 5 bytes are needed, append `05 05 05 05 05`                                                                               |
| **Modern status**         | PKCS #5 v2.x focuses on password-based cryptography (e.g. PBKDF2); the padding convention is commonly generalized to larger block sizes                  |
| **Important distinction** | **PKCS#5 padding is not specifically “CBC padding.”** It is a padding scheme used with block-cipher encryption; CBC is one common mode where it appears. |

Also, PKCS #7 defines the generalized padding mechanism for block sizes up to 255 bytes. 
That's why for AES (16-byte blocks), people technically refer to the padding as PKCS#7 padding, even though many libraries/documentation casually call it PKCS#5 padding.

#### Key Distinction Between PKCS#5 and PKCS#7: 

PKCS #5's original padding is for 8-byte block ciphers, whereas PKCS #7 generalizes the same padding style to block sizes up to 255 bytes—which is why AES uses PKCS#7-style padding.

[^1]: **PKCS#5** includes 8-byte-block padding scheme traditionally called **PKCS#5 padding**.
[^2]: **PKCS#7** also defines the **generalized block-cipher padding scheme (upto 255-bytes)** commonly called **PKCS#7 padding**.
