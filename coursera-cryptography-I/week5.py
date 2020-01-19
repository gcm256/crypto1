#import gmpy2

""""  "pip install gmpy2" did not work in my virtual env due to missing dependency on mpfr and libmpc
      See https://stackoverflow.com/questions/23187801/installing-gmpy-on-osx-mpc-h-not-found
      But I do not want to do "brew install mpfr" and "brew install mpfr" """

"""> cc -fno-strict-aliasing -fno-common -dynamic -g -Os -pipe -fno-common -fno-strict-aliasing -fwrapv 
     -DENABLE_DTRACE -DMACOSX -DNDEBUG -Wall -Wstrict-prototypes -Wshorten-64-to-32 -DNDEBUG -g -fwrapv -Os -Wall 
     -Wstrict-prototypes -DENABLE_DTRACE -arch i386 -arch x86_64 -pipe -DWITHMPFR -DWITHMPC -I/usr/local/include 
     -I/System/Library/Frameworks/Python.framework/Versions/2.7/include/python2.7 -c src/gmpy2.c 
     -o build/temp.macosx-10.14-intel-2.7/src/gmpy2.o

     In file included from src/gmpy2.c:426:
     src/gmpy.h:252:12: fatal error: 'mpfr.h' file not found
     #  include "mpfr.h"
                ^~~~~~~~
     1 error generated.
     error: command 'cc' failed with exit status 1
     
Hence we will use gmpy"""
import gmpy

def main():
    p = gmpy.mpz("134078079299425970995740249982058461274793658205923933 \
                  77723561443721764030073546976801874298166903427690031 \
                  858186486050853753882811946569946433649006084171")

    g = gmpy.mpz("11717829880366207009516117596335367088558084999998952205 \
                  59997945906392949973658374667057217647146031292859482967 \
                  5428279466566527115212748467589894601965568")

    h = gmpy.mpz("323947510405045044356526437872806578864909752095244 \
                  952783479245297198197614329255807385693795855318053 \
                  2878928001494706097394108577585732452307673444020333")

    ####### Find x such that h = g**x (mod p)
    # Also given, 1 <= x <= 2**40

    B = gmpy.mpz(2**20)

    # Let x = B*x0 + x1, where x0, x1 < B
    # h / g**x1 == (g**B)**x0 in Zp

    x = None
    hash_table = dict()

    for x1 in range(0, B):
        #q, r = gmpy.fdivmod(g**x1,p)
        r = power_mod(g, x1, p)
        n = gmpy.divm(h, r, p)
        hash_table[n] = x1


    g_pow_B = power_mod(g, B, p)

    for x0 in range(0, B):
        key = power_mod(g_pow_B, x0, p)
        if key in hash_table:
            x1 = hash_table[key]
            x = x0*B + x1
            break

    assert x
    assert h == power_mod(g, x, p)
    print "x = Dlog of h to the base g in Zp = ", x
    # assert x == 375374217830 # Answer. Took about 28 seconds to run on my computer.


    #print "p = ", B, 2**40

# Unlike gmpy2, gmpy does not have powmod function.
# The below function will be used instead. It is taken from
# https://github.com/Robert-Campbell-256/Number-Theory-Python/blob/master/numbthy.py
def power_mod(b,e,n):
    """power_mod(b,e,n) computes the eth power of b mod n.
    (Actually, this is not needed, as pow(b,e,n) does the same thing for positive integers.
    This will be useful in future for non-integers or inverses.)"""
    if e<0: # Negative powers can be computed if gcd(b,n)=1
        e = -e
        b = gmpy.divm(1,b,n)
    accum = 1; i = 0; bpow2 = b
    while ((e>>i)>0):
        if((e>>i) & 1):
            accum = (accum*bpow2) % n
        bpow2 = (bpow2*bpow2) % n
        i+=1
    return accum

if __name__ == "__main__":
    main()