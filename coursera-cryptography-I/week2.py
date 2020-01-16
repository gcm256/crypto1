from Crypto.Cipher import AES
from Crypto.Util import Counter

def main():
  BLOCK_SIZE_BYTES = 16

  cbc_keys = [ "140b41b22a29beb4061bda66b6747e14",
  "140b41b22a29beb4061bda66b6747e14"]
  cbc_cipher_texts = [
    "4ca00ff4c898d61e1edbf1800618fb2828a226d160dad07883d04e008a7897ee2e4b7465d5290d0c0e6c6822236e1daafb94ffe0c5da05d9476be028ad7c1d81",
  "5b68629feb8606f9a6667670b75b38a5b4832d0f26e1ab7da33249de7d4afc48e713ac646ace36e872ad5fb8a512428a6e21364b0c374df45503473c5242a253"]

  ctr_keys = [
    "36f18357be4dbd77f050515c73fcf9f2",
    "36f18357be4dbd77f050515c73fcf9f2"
  ]

  ctr_cipher_texts = [ "69dda8455c7dd4254bf353b773304eec0ec7702330098ce7f7520d1cbbb20fc388d1b0adb5054dbd7370849dbf0b88d393f252e764f1f5f7ad97ef79d59ce29f5f51eeca32eabedd9afa9329",
  "770b80259ec33beb2561358a9f2dc617e46218c0a53cbeca695ae45faa8952aa0e311bde9d4e01726d3184c34451"]

  #print cbc_decrypt("140b41b22a29beb4061bda66b6747e14", "4ca00ff4c898d61e1edbf1800618fb2828a226d160dad07883d04e008a7897ee2e4b7465d5290d0c0e6c6822236e1daafb94ffe0c5da05d9476be028ad7c1d81", 16)

  print "CBC MODE AES:"
  print "============"
  print "\n".join([cbc_decrypt(k,c, BLOCK_SIZE_BYTES) for (k,c) in zip(cbc_keys,cbc_cipher_texts)])

  print "\nCTR Mode AES:"
  print   "============"
  print "\n".join([ctr_decrypt(k,c, BLOCK_SIZE_BYTES) for (k,c) in zip(ctr_keys,ctr_cipher_texts)])

def cbc_decrypt(key, cipher_text, block_size):
  k = key.decode('hex')
  ct = cipher_text.decode('hex')
  iv = ct[:block_size]
  ct1 = ct[block_size:]
  obj = AES.new(key=k,mode=AES.MODE_CBC,IV=iv)
  padded_str = obj.decrypt(ct1)
  padding_amount = ord(padded_str[len(padded_str)-1:])
  return padded_str[:-padding_amount]

def ctr_decrypt(key, cipher_text, block_size):
  k = key.decode('hex')
  ct = cipher_text.decode('hex')
  iv = ct[:block_size]
  ct1 = ct[block_size:]
  ctr = Counter.new(block_size*8,initial_value=long(iv.encode('hex'),16))
  obj = AES.new(key=k,mode=AES.MODE_CTR,counter=ctr)
  padded_str = obj.decrypt(ct1)
  #padding_amount = ord(padded_str[len(padded_str)-1:])
  return padded_str#[:-padding_amount]


main()
  
