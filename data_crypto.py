import binascii
#import Crypto #pip install pycryptodome
from Crypto.Cipher import AES
from Crypto.Random import get_random_bytes
from cStringIO import StringIO

def main():
  headerAAD = "[header = H1]"

  session_key_in_hex = "5B7F0A474EE002908CDEA4A271EC54D4"

  cipher_in_hex = "FA96B8CC9D2825F8659C3F068567898F1F5B0B74E209E634095E37EFAB3FE01479756E05692F9D4CCA8C2553FF73E0F3A961480F4A576B2F73BF3CA24C858143E15AB6E92C762A11B46481BFE848432EE5502922305DA649DC37"

  d = data_decrypt(headerAAD, session_key_in_hex,cipher_in_hex)
  print "D = " + d

def data_decrypt(headerAAD, session_key_in_hex, encrypted_data_in_hex):
  #s = StringIO(binascii.unhexlify(encrypted_data_in_hex))
  #nonce, ciphertext, tag = [ s.read(x) for x in (12, 19, -1) ]

  s = binascii.unhexlify(encrypted_data_in_hex)
  nonce, ciphertext, tag = s[:12], s[12:-16], s[-16:]
  print "Nonce = " + binascii.hexlify(nonce)
  print "Tag   = " + binascii.hexlify(tag)
  print "CipherText = " + binascii.hexlify(ciphertext)

  cipher_aes = AES.new(binascii.unhexlify(session_key_in_hex), AES.MODE_GCM, nonce=nonce)
  cipher_aes.update(headerAAD)
  data = cipher_aes.decrypt_and_verify(ciphertext, tag)
  return data

main()
