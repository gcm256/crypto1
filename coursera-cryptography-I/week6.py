import gmpy

def main():
    N, p, q = q1()
    q2()
    q3()
    q4(N, p, q)

def q1():
    N = gmpy.mpz("17976931348623159077293051907890247336179769789423065727343008115 \
                  77326758055056206869853794492129829595855013875371640157101398586 \
                  47833778606925583497541085196591615128057575940752635007475935288 \
                  71082364994994077189561705436114947486504671101510156394068052754 \
                  0071584560878577663743040086340742855278549092581")

    A = gmpy.sqrt(N) + 1
    x1 = A**2 - N
    assert gmpy.is_square(x1)
    x = gmpy.sqrt(x1)
    p = A - x
    q = A + x
    assert p*q == N

    print "Answer to Question 1:"
    print p
    #print q
    #print x
    # Answer:
    assert p == gmpy.mpz("134078079299425970995740249982058461274793658205923933777235614437217640300736 \
                          62768891111614362326998675040546094339320838419523375986027530441562135724301")

    return N, p, q

def q2():
    N = gmpy.mpz("6484558428080716696628242653467722787263437207069762630604390703787 \
                  9730861808111646271401527606141756919558732184025452065542490671989 \
                  2428844841839353281972988531310511738648965962582821502504990264452 \
                  1008852816733037111422964210278402893076574586452336833570778346897 \
                  15838646088239640236866252211790085787877")

    A0 = gmpy.sqrt(N) + 1
    for i in xrange(2**20):
        A = A0 + i
        x1 = A**2 - N
        if gmpy.is_square(x1):
            x = gmpy.sqrt(x1)
            p = A - x
            q = A + x
            if p*q == N:
                break
    assert p*q == N
    print "Answer to Question 2:"
    print p
    # Answer:
    assert p == gmpy.mpz("254647961469961834380088165639739422293414542685241578463285819278857779699852 \
                          22835143851073249573454107384461557193173304497244814071505790566593206419759")


def q3():
    N = gmpy.mpz("72006226374735042527956443552558373833808445147399984182665305798191 \
                  63556901883377904234086641876639384851752649940178970835240791356868 \
                  77441155132015188279331812309091996246361896836573643119174094961348 \
                  52463970788523879939683923036467667022162701835329944324119217381272 \
                  9276147530748597302192751375739387929")

    ''' Given: |3p - 2q| < N**(1/4)                  (1)
        N = pq
        Let A = (3p+2q)/2
        => 2A = (6p+4q)/2
        => 2A is an integer, and lies exactly midway between 6p and 4q
        
        Let 6p = 2A - x
            4q = 2A + x
        => 24pq = 4A**2 - x**2
        => 24N = 4A**2 - x**2
        => x = sqrt(4A**2 - 24N)                     (2)
        
        To find x using (2), we need to find relation between A and N.
        Consider the value A**2 - 6N
        A**2 - 6N = (3p+2q)**2 / 4   -  6pq = (3p-2q)**2 / 4   , on simplifying.
        
        But, A**2 - 6N = (A + sqrt(6N))(A - sqrt(6N))
        Hence, A - sqrt(6N) = (3p-2q)**2 / (4(A + sqrt(6N)))         (3)
        
        Now A = (3p+2q)/2 > sqrt(3p*2q) = sqrt(6N)    since AM > GM for 2 non-equal numbers
        => A > sqrt(6N)
        => A + sqrt(6N) > 2*sqrt(6N)
        
        Using above line in (3), we get the inequality:
        A - sqrt(6N) < (3p-2q)**2 / (8*sqrt(6N)) < sqrt(N) / (8*sqrt(6N))    Using (1)
        
        Thus A - sqrt(6N) < 1 / (8*sqrt(6) < 1              [Proved]'''

    twoA = gmpy.sqrt(4*6*N) + 1  # A = sqrt(6N) + 0.5, since A, which is (3p+2q)/2, is NOT an integer.
    v = (twoA**2) - 24*N
    assert gmpy.is_square(v)
    x = gmpy.sqrt(v)
    p = (twoA - x)/6
    q = (twoA + x)/4
    assert p*q == N
    print "Answer to Question 3:"
    print p
    # Answer:
    assert p == gmpy.mpz("219098495924755330922739885315839558989821760933449290300994235841272120781261 \
                          50044721102570957812665127475051465088833555993294644190955293613411658629209")


def q4(N, p, q):
    c = gmpy.mpz("22096451867410381776306561134883418017410069787892831071731839143676135600120538 \
                  00428232965047350942434394621975151225646583996794288946076454204058156474898801 \
                  37348641204523252293201764879166664029975091887299716905260832220677716000193292 \
                  60870009579993724077458967773697817571267229951148662959627934791540")
    e = gmpy.mpz("65537")

    phi = (p-1)*(q-1)
    d = gmpy.divm(1, e, phi)
    assert (e * d) % phi == 1

    m = power_mod(c, d, N)
    assert power_mod(m, e, N) == c
    #print "M = ", m


    hexstring = hex(m)
    #print len(hexstring), hexstring
    hexstring = hexstring.split('00')[1]
    #print len(hexstring)
    plaintext = hexstring.decode('hex')
    print "Answer to Question 4:"
    print plaintext
    # Answer:
    assert plaintext == "Factoring lets us break RSA."


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