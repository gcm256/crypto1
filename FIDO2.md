# Zero-Trust Micro-segmentation & FIDO2

## Passkey Scenarios

| Scenario | Fake-Server?               | MITM?                      | Passkey exists on device?  |
|----      |----                        |----                        |----                        |
| 1        | $\color{green}{\text{NO}}$ | $\color{green}{\text{NO}}$ | ❌ |
| 2        | $\color{green}{\text{NO}}$ | $\color{green}{\text{NO}}$ | ✅ |
| 3        | $\color{green}{\text{NO}}$ | $\color{red}{\text{YES}}$  | ❌ |
| 4        | $\color{green}{\text{NO}}$ | $\color{red}{\text{YES}}$  | ✅ |
| 5        | $\color{red}{\text{YES}}$  | $\color{green}{\text{NO}}$ | ❌ |
| 6        | $\color{red}{\text{YES}}$  | $\color{green}{\text{NO}}$ | ✅ |
| 7        | $\color{red}{\text{YES}}$  | $\color{red}{\text{YES}}$  | ❌ |
| 8        | $\color{red}{\text{YES}}$  | $\color{red}{\text{YES}}$  | ✅ |


<!--
[Y]: $\color{red}{\text{YES}}$
[N]: $\color{green}{\text{NO}}$
-->


