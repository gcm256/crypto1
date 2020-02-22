# crypto1

Comparison of AES cipher modes: (Block size 128 bits, Key size 128, 192 or 256 bits)

Property                               | ECB   | CBC   | CFB   | OFB   | CTR   | GCM   | AEX
---------                              | :---: | :---: | :---: | :---: | :---: | :---: | :---:
Multi-block Semantic Security          |:x:|:white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark:
No Padding Needed. Is a stream &dagger; |:x:|:x:                | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: | 
Parallel Encrypt                       |:white_check_mark: |:x:| :x:                | :x:                | :white_check_mark: | :white_check_mark: | 
Parallel Decrypt                       |:white_check_mark:     | :white_check_mark: | :white_check_mark: | :x: |:white_check_mark:| :white_check_mark: | 
AEAD                                   |:x:|:x: | :x: | :x: | :x: | :white_check_mark: | :white_check_mark:


&dagger; Stream because plaintext block is not input to the Encrypt/Decrypt block function. Hence padding to match blocksize is not needed.
Plaintext is XOR'ed to the output of the Encrypt/Decrypt block function.
