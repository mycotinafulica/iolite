package org.amber.asparagus.iolite

import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider
import org.bouncycastle.pqc.jcajce.spec.KyberParameterSpec
import java.io.File
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.Security
import java.util.Base64

// Reference if we want to use encrypted format https://stackoverflow.com/questions/63832456/parsing-encrypted-pkcs8-encoded-pem-file-programatically
// by default java PKCS#8 format is not encrypted, and it is in the form of DER format.
// If you want to use the base64 encoding, you should encode it first

// For digital signature we might be able to see here : https://github.com/bcgit/bc-java/blob/main/core/src/test/java/org/bouncycastle/pqc/crypto/test/CrystalsDilithiumTest.java
class KeypairGenerator {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            if(Security.getProvider("BCPQC") == null){
                Security.addProvider(BouncyCastlePQCProvider())
            }

            val parsedArgument = ArgumentParser().parse(args)

            if(parsedArgument.showHelp) {
                showHelp()
            }
            else if(parsedArgument.invalidArgs){
                if(parsedArgument.invalidReason != null) {
                    println(parsedArgument.invalidReason)
                }
                else {
                    showHelp()
                }
            }
            else {
                val spec = parsedArgument.spec ?: KyberParameterSpec.kyber512
                val outputDir = parsedArgument.outputDirectory ?: "./"
                val directory = File(outputDir)
                if(!directory.exists()) {
                    directory.mkdirs()
                }
                if(parsedArgument.encoding != null && parsedArgument.encoding == "BASE64") {
                    generateBase64EncodedKeyPair(spec, "${outputDir}/")
                }
                else {
                    generateDerEncodedKeyPair(spec, "${outputDir}/")
                }
            }
        }

        private fun generateBase64EncodedKeyPair(spec: KyberParameterSpec, outputDir: String) {
            val privateKey = File("${outputDir}private.p8")
            val publicKey  = File("${outputDir}public.crt")

            val keyPair = generateChrystalsKyberKeyPair(spec)
            val base64Private = Base64.getEncoder().encodeToString(keyPair.private.encoded)
            val base64Public  = Base64.getEncoder().encodeToString(keyPair.public.encoded)

            val privateContent = StringBuilder()
            privateContent.append("-----BEGIN PRIVATE KEY-----\n")
            for(i in base64Private.indices step 64) {
                if(i + 64 < base64Private.length) {
                    privateContent.append(base64Private.slice(i until i + 64))
                }
                else {
                    privateContent.append(base64Private.slice(i until base64Private.length))
                }
                privateContent.append("\n")
            }
            privateContent.append("-----END PRIVATE KEY-----\n")

            val publicContent = StringBuilder()
            publicContent.append("-----BEGIN PUBLIC KEY-----\n")
            for(i in base64Public.indices step 64) {
                if(i + 64 < base64Public.length) {
                    publicContent.append(base64Public.slice(i until i + 64))
                }
                else {
                    publicContent.append(base64Public.slice(i until base64Public.length))
                }
                publicContent.append("\n")
            }
            publicContent.append("-----END PUBLIC KEY-----\n")

            privateKey.writeText(privateContent.toString())
            publicKey.writeText(publicContent.toString())
        }

        private fun generateDerEncodedKeyPair(spec: KyberParameterSpec, outputDir: String) {
            val privateKey = File("${outputDir}private.p8")
            val publicKey  = File("${outputDir}public.crt")

            val keyPair = generateChrystalsKyberKeyPair(spec)
            privateKey.writeBytes(keyPair.private.encoded)
            publicKey.writeBytes(keyPair.public.encoded)
        }

        private fun generateChrystalsKyberKeyPair(spec: KyberParameterSpec): KeyPair {
            val keyPairGenerator = KeyPairGenerator.getInstance("KYBER", "BCPQC")
            keyPairGenerator.initialize(spec, SecureRandom())
            return keyPairGenerator.genKeyPair()
        }

        private fun showHelp() {
            println("USAGE : iolite [options]\n" +
                    "OR\n" +
                    "If used directly from the jar\n" +
                    "java -jar ChrystalKeypairGenerator.jar [options]\n" +
                    "\n" +
                    "options :\n" +
                    "--help           Show help\n" +
                    "--spec           Set kyber spec to use, available spec is KYBER512, KYBER768, KYBER1024.\n" +
                    "--encoding       Set the output encoding, possible value is BASE64 and DER. If not specified, DER is used.\n" +
                    "--output         Set the output directory of generated keypair. If not specified, current directory will be used.")
        }
    }
}