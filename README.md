## Iolite

Welcome to iolite! This repository contains all things you need to start playing with the quantum secure encryption algorithm, Kyber, which is one of the candidates listed by NIST for quantume resistant cryptography algorithms for encryption.

This repository contains the keypair generator and also an example of echo server and echo client that leverage the Kyber algorithm for its message exchange.

## Keypair Generation
This repository provides you with a pre-built keypair generation script which can be found at : [prebuild binaries](https://github.com/mycotinafulica/iolite/tree/master/ChrystalKeypairGenerator/script). To build the binaries on your own, see [building keypair generator](#building-keypair-generator) section

See the help section to see what options are available. Please notes that all the command listed below will assume that java is already on you environment path.

```sh
iolite --help
```

To generate DER formatted kyber keypair

```sh
iolite --spec KYBER512
```

Please see --help to know what other specs are available.

To generate a base64 formatted keypair, specify the encodings :
```sh
iolite --spec KYBER512 --encoding BASE64
```

You can also specify output directory with the following option:
```sh
iolite --spec KYBER512 --output "./your/output/directory"
```

By default, the script will produce the keypair on the current directory from which the script is run.

## Test It Out

Once you created your keypair using the method explained in the [keypair generation](#keypair-generation) section, you can start playing around with the provided client and server example.

First, copy the generate private key to [secret key location](ServerExample/src/main/resources/). The filename has to be private.p8.

Second, copy the generated public key to [public key location](ClientExample/src/main/resources/). The filename has to be public.crt.

**Known limitation : Only Base64 format is working right now.**

The server is a spring-boot project, note that you are expected to have java 17 installed in your test environment for things to work.

To start the server :
```sh
cd ./ServerExample

mvnw spring-boot:run
```

The server will run at localhost:8080

To run the client:
```sh
cd ./ClientExample

mvnw clean package

cd ./target

java -jar JavaClientExample-1.0-SNAPSHOT.jar
```

What happen :</br>
1. The client will create a session key, which will be encapsulated in Kyber encrypted payload and send it to the server.
2. The server will decrypt the payload and use the key as session key for the following traffics. This completes the key handshake.
3. The client will send an echo request encrypted with the agreed upon session key.
4. The server will decrypt the request and send back an echo response, encrypted using the agreed upon session key.

See some ideas on how this can be extended into real world use cases on the [future work](#future-work) section.

## Building Keypair Generator

To build the keypair generator, run the following command : 
```
cd ChrystalKeypairGenerator

mvnw clean package
```

The binaries will be located on the target directory. For the key generator to function, you need to copy both the ChrystalKeypairGenerator.jar and the lib directory into the same directory.

## Future Work

While this project shows how post quantum cryptography can be used, but it lacks of many characteristics that are desireable in production ready applications. 

Some of those characteristics from security standpoint are :
- The key distribution, as you can see that the public key is hardcoded into the client, which makes key rotation is a little bit of a pain.
- You need to create your own mechanism to validate the integrity of the public key certificate. Usually the TLS protocol will do this for you.

And without support in protocol levels such as TLS for example, and therefore leveraging the PKI technology, I'm afraid no ideal solution would found.

But aside from the security concerns, from application programmers point of view, few problems need to be addressed as well :

- As you can see, the current sample project handle the encryption and encryption individually for every endpoint, which is not ideal.

This will not be an issue if the kyber is actually supported in protocol level, but for this concerns, there are actually some ways to get around it by using some filter / interceptors mechanism.