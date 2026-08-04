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

## Public-Key Crypto-Systems (PKC-Systems)

Classification of public-key cryptography is by the underlying hard mathematical problem.

| Family                      | Hard Problem                            | Examples                                |
| --------------------------- | --------------------------------------- | --------------------------------------- |
| Integer Factorization       | Integer Factorization Problem (IFP)     | RSA, Rabin                              |
| Discrete Logarithm          | DLP                                     | Diffie-Hellman, ElGamal, DSA            |
| Elliptic Curve Discrete Log | ECDLP                                   | ECDH, EC ElGamal, ECDSA, EdDSA          |
| Lattice-based               | Shortest/Closest Vector Problems        | Kyber, Dilithium                        |
| Code-based                  | Syndrome Decoding Problem               | McEliece                                |
| Multivariate                | Solving Multivariate Polynomial Systems | Various PQC schemes (many experimental) |
| Hash-based                  | Hash function security                  | XMSS, LMS, SPHINCS+ (signatures only)   |

### Families of PKC-Systems

```
Public-Key Cryptography
│
├── Factorization
│     ├── RSA
│     └── Rabin
│
├── Discrete Logarithm
│     ├── Diffie-Hellman
│     ├── ElGamal
│     ├── DSA
│
├── Elliptic Curve DLP
│     ├── ECDH
│     ├── EC ElGamal
│     ├── ECDSA
│     |── EdDSA
|     └── ECIES
│
└── Post-Quantum
      ├── Lattice-based
      ├── Code-based
      ├── Hash-based
      └── Others
```

> [!NOTE]
> 1. Think of ElGamal as "Diffie-Hellman plus encryption."
> 2. Think of ECIES (Elliptic Curve Integrated Encryption Scheme) as a modernized, more robust descendant of EC-ElGamal.

---

### PKCS (Public Key Cryptography Standard)

- **PKCS #5**: Used in CBC padding for block ciphers.
- **PKCS #1**: Used to expand given key (AES key) from 128 to 1024 bits before sharing and sending via RSA. Because, sending 128 bits encrypted in RSA, is vulnerable to Meet-in-the-Middle attack.
  - **PKCS #1 v1.5**: v1.5 is still vulnerable to attack, since it gives quick error on not finding the 02 MSB in the RSA-encrypted ciphertext.
  - **PKCS #1 v2.0 = OAEP (Optimal Asymmetric Encryption Padding)**: Fixes the vulnerability of PKCS #1 v1.5.

### PKCS #1 vs ISO Key Expansion

The ISO way is to generate random 1024 bit value (called, say, X) and send X RSA-encrypted to the other party. So X will be secure from RSA MITM attack since it is long enough. Then both parties use Hash(X) ie SH128(X) as AES key K, which will be 128 bits.

But this is impractical, because most apps want fixed known 128 bits AES key K. So instead of generating large X and then doing K = H(X), we construct large X from a given 128 bit K itself, using PKCS #1.

---

### Complexity of Integer Factorization and DLP/ECDLP

| Problem                                         | In NP?      | Known/Believed to be in P? | Known/Believed to be NP-complete? | In co-NP? |
| ----------------------------------------------- | ----------- | -------------------------- | --------------------------------- | --------- |
| **Integer Factoring**                           | ✅ **Known** | ❌/❌  | ❌/❌ | ✅ **Known** (for standard decision formulations; factoring is in $NP \cap co\text{-}NP$) |
| **Discrete Logarithm Problem (DLP)**            | ✅ **Known** | ❌/❌  | ❌/❌ | ❓ **Not known** AND No widely held Conjecture/belief about Y/N                           |
| **Elliptic Curve Discrete Log Problem (ECDLP)** | ✅ **Known** | ❌/❌  | ❌/❌ | ❓ **Not known** AND No widely held Conjecture/belief about Y/N                           |

---

### RSA

#### Complexity of Factoring

Factoring is known to be in NP and in co-NP, but not known (nor believed) to be in P or NP-complete.

Therefore, factoring is known to be no harder than an NP-complete problem.


#### RSA constraints

Do not RSA-encrypt messages/keys of size much less than RSA modulus size. This will be open to MITM attack.

Do not encrypt entire large messages with RSA either. This will be too inefficient compared to AES. Asymmetric algorithms are inefficient compared to symmetric algorithms.

So, only RSA-encrypt values of size equal to RSA modulus. 

AES needs at least 128 bit key-size / block-size to be secure. But RSA needs at least 2048 bit modulus size to be secure.

If we encrypt a 128 bit value using RSA, MITM attack is feasible.

---

### ECC

Elliptic Curve Cryptography (ECC) is a public-key (asymmetric) cryptography system.

> [!NOTE]
> ECDH is used to share a common secret securely. That secret is then used by both Alice and Bob with a KDF to generate a symmetric AES key. This is Key-Establishment.
> This AES key is then used for encryption/decryption of the subsequent communication between Alice and Bob, in most cases.
> The asymmetry is only in the ECDH Key-Establishment, since Alice and Bob generate random secrets $a$ and $b$ respectively as the first step of the ECDH.
> These $a$ and $b$ are called ephemeral private-keys because they are secret keying material that control the generation of the shared secret via ECDH.
> More precisely, $a$ and $b$ are ephemeral ECDH private keys (secret scalars) used for key agreement.
> Then, the encryption decryption of messages in the subsequent communication in the session, is symmetric.


#### Safe Curves:

* [Curve25519](https://std.neuromancer.sk/other/Curve25519) is safe.

#### Unsafe Curves:

* [secp256k1](https://std.neuromancer.sk/secg/secp256k1) (aka [ansip256k1](https://std.neuromancer.sk/x963/ansip256k1))
* [secp256r1](https://std.neuromancer.sk/secg/secp256r1) (aka [NIST P-256](https://std.neuromancer.sk/nist/P-256) or [prime256v1](https://std.neuromancer.sk/x962/prime256v1))

#### Refer:

Ref: https://safecurves.cr.yp.to/

#### Types of Elliptic Curves:

ECs are classified according to representation/equation format. The most commonly used EC representations are following:

1. **Weierstrass**: $y^2 = x^3 + ax + b$
2. **Edwards**: $x^2 + y^2 = 1 + dx^2y^2$
3. **Twisted Edwards**: $ax^2 + y^2 = 1 + dx^2y^2$
4. **Montgomery**: $By^2 = x^3 + Ax^2 + x$
5. **Hessian**: $x^3 + y^3 + 1 = 3dxy$


There is no single fixed number, because elliptic curve "forms" are mathematical representations, and researchers have created many equivalent forms optimized for different purposes. However, there are a handful of important families that appear frequently in cryptography.

The major ones are:

| Form                       | Typical equation                      | Main advantage / use                                            |
| -------------------------- | ------------------------------------- | --------------------------------------------------------------- |
| **Weierstrass form**       | $y^2=x^3+ax+b$                        | The standard mathematical form; basis for most ECC theory       |
| **Short Weierstrass form** | $y^2=x^3+ax+b$ (over suitable fields) | Most common form in deployed ECC (P-256, secp256k1)             |
| **Montgomery form**        | $By^2=x^3+Ax^2+x$                     | Very efficient scalar multiplication; used by Curve25519/X25519 |
| **Edwards form**           | $x^2+y^2=1+dx^2y^2$                   | Fast and complete addition formulas                             |
| **Twisted Edwards form**   | $ax^2+y^2=1+dx^2y^2$                  | Generalized Edwards; used by Ed25519                            |
| **Hessian form**           | $x^3+y^3+1=3dxy$                      | Efficient addition formulas; less common today                  |
| **Jacobi quartic form**    | $y^2=x^4+2ax^2+1$                     | Useful for certain implementations                              |
| **Legendre form**          | $y^2=x(x-1)(x-\lambda)$               | Useful in mathematical analysis                                 |
| **Huff form**              | $x(a y^2-1)=y(b x^2-1)$               | Alternative addition formulas                                   |

---

ECs can also be classified according to special properties. One property class is **Koblitz Curves** which are a proper subset of non-singular binary-field elliptic curves. When expressed as a Weierstrass-like equation, Koblitz Curves have the form:

$$
E_0: y^2 + xy = x^3 + 1
$$

or

$$
E_1: y^2 + xy = x^3 + x^2 + 1
$$

Some of the many other property classifications of ECs are:

1. Random curves
2. Rigidly generated curves
3. Anomalous curves
4. Supersingular curves
5. Pairing-friendly curves
6. Endomorphism-friendly curves (include GLV-friendly curves, GLS-friendly curves, and others)
7. Cofactor-related classes

> [!NOTE]
> A classic Koblitz curve is usually expressed in a binary-field Weierstrass-like form, but "Koblitz" describes its special mathematical structure, not its equation form.

---

### Hyper-Elliptic(HE) & Super-Elliptic(SE) Curves

An Hyper-Elliptic Curve (HEC) is of the form $y^2 = f(x)$ where $f(x)$ has degree ≥ 5

A Super-Elliptic Curve (SEC) is of the form $y^m = f(x)$ where $m ≥ 2$ and $f(x)$ can have any degree as long as it is square-free (ie has no repeated roots).

> [!IMPORTANT]
> So, $(\text{ EC } \cup \text{ HEC }) \subset \text{ SEC }$

| Curve family  | Equation                      | Degree restriction?         | Has genus? |
| ------------- | ---------------------------   | --------------------------- | ---------- |
| Elliptic      | $y^2=f(x)$, $\deg f=3$ or $4$ | Yes                         | Yes, $g=1$ |
| Hyperelliptic | $y^2=f(x)$, $\deg f\ge5$      | Degree tied to genus        | Yes        |
| Superelliptic | $y^m=f(x)$, $m\ge2$           | No fixed degree restriction | Yes        |

#### https://www.hyperelliptic.org/


