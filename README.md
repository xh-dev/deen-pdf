# deen-pdf
deen pdf is scala based test, decrypt and encrypt pdf tool

[Download Jar](https://github.com/xh-dev/deen-pdf/blob/master/assembly/deen-pdf.jar?raw=true)

## Command usage
```
Usage: deen-pdf.jar [test|decrypt|encrypt] [options]
  -in, --input-file <value>   input pdf file path
  
Command: test [options]
  Test if a pdf file is encrypted
  -pwd, --password <value>    password to validate
Command: decrypt [options]
  Decrypt pdf with password
  -pwd, --password <value>    decrypt password
Command: encrypt [options]
  encrypt pdf with password
  -pwd, --password <value>    encrypt password

```

## Run command
```bat
# encrypt
java -jar deen-pdf.jar -in {file input} -pwd {password} encrypt

# decrypt
java -jar deen-pdf.jar -in {file input} -pwd {password} decrypt

```
