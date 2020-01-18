import urllib2
import sys
import binascii
import string
from Crypto.Util.strxor import strxor

TARGET = 'http://crypto-class.appspot.com/po?er='
#--------------------------------------------------------------
# padding oracle
#--------------------------------------------------------------
class PaddingOracle(object):
    def query(self, q):
        target = TARGET + urllib2.quote(q)    # Create query URL
        req = urllib2.Request(target)         # Send HTTP request to server
        try:
            f = urllib2.urlopen(req)          # Wait for response
        except urllib2.HTTPError, e:
            #print "We got: %d" % e.code       # Print response code
            if e.code == 404:
                return True # good padding
            return False # bad padding


po = PaddingOracle()

def main():
    block_size = 16 #Bytes
    ciphertext_hex_str = "f20bdba6ff29eed7b046d1df9fb7000058b1ffb4210a580f748b4ac714c001bd4a61044426fb515dad3f21f18aa577c0bdf302936266926ff37dbf7035d5eeb4"
    #print "SS = ", po.query(ciphertext_hex_str)
    cipher = binascii.unhexlify(ciphertext_hex_str)
    num_blocks = len(cipher)/block_size
    ct_blocks = [cipher[i:i+block_size] for i in xrange(0, len(cipher), block_size)]
    pt_blocks = [binascii.unhexlify("00")*block_size for i in xrange(0,num_blocks-1)]
    d = dict(zip(range(len(ct_blocks)), ct_blocks))

    for i in range(0,num_blocks-1):
        curr_ct_blocks, m = ct_blocks[:i+2], pt_blocks[i]
        for pos in range(0,block_size):
            p = pos + 1
            print "\n\nBlock#", i+1, "of", num_blocks-1, " :: Search for Position#", p, " from the end"
            print "Starting with = ", m #binascii.hexlify(m[:-p]) + "".join(i + j for i, j in zip("*"*len(m[-p:]), m[-p:]))
            m = update_m_with_byte_at(pos, curr_ct_blocks, m)
        print "\n\n\n\n**********Fully Decrypted Block#",i+1,  " : ", m
        pt_blocks[i] = m
        print "==========Full Decrypted portion now is: ", "".join(pt_blocks[:i+1])


    #print len(cipher), len(ciphertext_hex_str), num_blocks, len(ct_blocks)
    #print [binascii.hexlify(ct_blocks[i]) for i in [0,1,2,3]]
    #print d
    #print pt_blocks


def update_m_with_byte_at(pos_from_end_of_m, curr_ct_blocks, m):
    ls = string.ascii_letters + " " #If we don't assume only letters,space in plaintext, we can say string.printable but it will take longer.
    is_last_block = len(curr_ct_blocks) == 4
    if is_last_block:
        #For last block, include the possible pad hex numbers 01 - 0f, to the set of guesses.
        ls = binascii.unhexlify("0102030405060708090a0b0c0d0e0f") + ls
    ct_prefix, ct_to_change, ct_end = curr_ct_blocks[:-2], curr_ct_blocks[-2], curr_ct_blocks[-1]
    k = pos_from_end_of_m + 1
    n = len(m)
    pad_gen_block = binascii.unhexlify("00"*(n-k) + ("%0.2x" % k)*k)
    print "Padding  with = ", binascii.hexlify(pad_gen_block)
    for g in ls:
        m = m[:n-k] + g + m[n-k+1:]
        print "Current guess block = ", m
        #print len(ct_to_change), len(m), len(pad_gen_block)
        ct_changed = strxor(ct_to_change, strxor(m,pad_gen_block))
        query_str = binascii.hexlify("".join(ct_prefix) + ct_changed + ct_end)
        #print "Trying ", g, "Query = ", len(query_str)/2, " bytes, ", query_str
        #print "Trying ", binascii.hexlify(g), g
        status = po.query(query_str)
        #print "Status = ", status
        # Only for final block: When pad_gen_block matches correct pad, status=None, so we need to accept the guess in that case.
        # But still skip the end position (k=1). For k=1, pad = 0x01. So when g = 0x01 picked, they will cancel and produce original
        # plaintext, status will be None, but this g should not be accepted.
        if status or (is_last_block and k > 1 and status is None):
            print "=============================================================\""+g+"\" Works!"
            return m


if __name__ == "__main__":
    main()
