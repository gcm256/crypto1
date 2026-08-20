# Homomorphic Encryption (HE)


## Summary of HE Schemes:

| Name / acronym                               | Year of invention | Mathematical basis                              | PHE / SHE / FHE + operation                                | Common apps / software                                                                                                                                                                                                  |
| -------------------------------------------- | ----------------: | ----------------------------------------------- | ---------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **RSA** — Rivest–Shamir–Adleman              |          **1977** | Integer factorization / RSA problem             | **PHE — multiplicative** in *textbook RSA*                 | **Browsers, TLS, OpenPGP, S/MIME, OS crypto libraries** historically/currently for signatures and key transport. **Important:** deployed RSA-OAEP/PSS does **not** retain textbook RSA's homomorphism. ([Wikipedia][1]) |
| **Goldwasser–Micali (GM)**                   |          **1982** | Quadratic Residuosity                           | **PHE — XOR / addition mod 2**                             | Mainly **academic / specialized privacy protocols**; not a mainstream browser primitive. The original paper dates to 1982. ([DOI][2])                                                                                   |
| **ElGamal**                                  |     **1984/1985** | Discrete logarithm; DDH-style security          | **PHE — multiplicative**                                   | **OpenPGP/GnuPG historically**; RFC 4880 specified ElGamal as an encryption algorithm. Not a normal browser/TLS encryption primitive today. ([Cryptology City][3])                                                      |
| **EC-ElGamal** — Elliptic-Curve ElGamal      |    **~1985–90s*** | Elliptic-curve discrete logarithm               | **PHE — additive** when plaintexts are EC-group elements   | Mostly **research / specialized cryptographic libraries**; no major mainstream browser protocol uses basic EC-ElGamal as its bulk-encryption primitive.                                                                 |
| **Benaloh** — Dense Probabilistic Encryption |          **1994** | Composite residuosity                           | **PHE — additive** over a limited message space            | Mainly **e-voting and research/privacy protocols**. Benaloh's 1994 work specifically discussed verifiable secret sharing and secret-ballot elections. ([Microsoft][4])                                                  |
| **Paillier** — Paillier Cryptosystem         |          **1999** | Composite Residuosity / DCR                     | **PHE — additive**                                         | **E-voting, privacy-preserving aggregation, threshold cryptography, research libraries**. Not a browser/TLS primitive. ([ResearchGate][5])                                                                              |
| **BGN** — Boneh–Goh–Nissim                   |          **2005** | Bilinear pairings; subgroup-decision assumption | **SHE — arbitrary additions + at most one multiplication** | Mainly **research / privacy-preserving computation**. ([Stanford AI Laboratory][6])                                                                                                                                     |
| **BGV** — Brakerski–Gentry–Vaikuntanathan    |          **2011** | Lattice / RLWE-style assumptions                | **SHE/FHE — addition + multiplication**                    | **HE libraries**, e.g. Microsoft SEAL; not normal browser/OS encryption. SEAL supports BGV directly. ([GitHub][7])                                                                                                      |
| **BFV** — Brakerski/Fan–Vercauteren          |          **2012** | Lattice / RLWE-style assumptions                | **SHE/FHE — exact modular addition + multiplication**      | **Microsoft SEAL, OpenFHE**, privacy-preserving computation, encrypted ML/data analytics. SEAL explicitly implements BFV. ([GitHub][7])                                                                                 |
| **CKKS** — Cheon–Kim–Kim–Song                |    **2016/2017*** | Lattice / RLWE-style assumptions                | **SHE/FHE — approximate addition + multiplication**        | **Microsoft SEAL, OpenFHE**, especially encrypted numerical computation and privacy-preserving ML. SEAL specifically describes CKKS for encrypted real/complex arithmetic. ([GitHub][7])                                |

[1]: https://en.wikipedia.org/wiki/RSA_cryptosystem "RSA cryptosystem"
[2]: https://doi.org/10.1145/800070.802212 "Probabilistic encryption & how to play mental poker keeping secret all partial information | Proceedings of the fourteenth annual ACM symposium on Theory of computing"
[3]: https://cryptology.city/References/ElGamal85---A-Public-Key-Cryptosystem-and-a-Signature-Scheme-Based-on-Discrete-Logarithms "ElGamal85"
[4]: https://www.microsoft.com/en-us/research/?p=151178 "Dense Probabilistic Encryption - Microsoft Research"
[5]: https://www.researchgate.net/publication/221348062_Public-Key_Cryptosystems_Based_on_Composite_Degree_Residuosity_Classes "(PDF) Public-Key Cryptosystems Based on Composite Degree Residuosity Classes"
[6]: https://ai.stanford.edu/~xb/eurocrypt06/eprint-2005-381.pdf "Compact Group Signatures Without Random Oracles"
[7]: https://github.com/microsoft/SEAL "GitHub - microsoft/SEAL: Microsoft SEAL is an easy-to-use and powerful homomorphic encryption library. · GitHub"

### RSA is a special case

Mathematically:

```
textbook RSA:
    Enc(m₁) × Enc(m₂) = Enc(m₁ × m₂)
```

But secure RSA encryption isn't used that way. RSA-OAEP deliberately adds randomized encoding, so you **shouldn't think of modern RSA encryption as a practical homomorphic encryption scheme**.

## Historical Progression:

```
1977  RSA
        │
1982  Goldwasser–Micali
        │
1984  ElGamal
        │
1994  Benaloh
        │
1999  Paillier
        │
2005  BGN
        │
2011  BGV
        │
2012  BFV
        │
2016/17  CKKS
```

## Computation Operations:

```
RSA       → multiplication
GM        → XOR
ElGamal   → multiplication
Paillier  → addition
BGN       → addition + one multiplication
BGV/BFV   → addition + multiplication
CKKS      → approximate numerical computation
FHE       → arbitrary circuits
```

## Are there symmetric homomorphic schemes?

There are both **symmetric-key HE** and **public-key HE**, although public-key HE is much more prominent in applications involving an untrusted evaluator.

Because one of the most interesting applications of HE is:

> **Someone who does NOT possess the decryption key should be able to compute on your encrypted data.**

That's naturally a **public-key** scenario.

## Glossary

* {P|S|F}HE = {Public | Symmetric | Fully} HE

