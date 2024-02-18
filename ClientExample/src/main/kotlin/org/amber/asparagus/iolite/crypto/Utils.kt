package org.amber.asparagus.iolite.crypto

import org.bouncycastle.jcajce.SecretKeyWithEncapsulation
import org.bouncycastle.jcajce.spec.KEMGenerateSpec
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider
import java.security.KeyFactory
import java.security.PublicKey
import java.security.SecureRandom
import java.security.Security
import java.security.spec.X509EncodedKeySpec
import javax.crypto.KeyGenerator

class Utils {
    companion object {
        fun registerBouncyCastleProvider() {
            if(Security.getProvider("BCPQC") == null){
                Security.addProvider(BouncyCastlePQCProvider())
            }
            val provider = Security.getProvider("BCPQC")
            println(provider)
        }

        fun generateEncapsulatedAesKey(publicKey: ByteArray): SecretKeyWithEncapsulation {
            val x509EncodedKeySpec = X509EncodedKeySpec(publicKey)
            val keyFactory         = KeyFactory.getInstance("KYBER", "BCPQC")
            val publicKeyFmt       = keyFactory.generatePublic(x509EncodedKeySpec)

            val keyGen = KeyGenerator.getInstance("KYBER", "BCPQC")
            keyGen.init(KEMGenerateSpec(publicKeyFmt, "AES"), SecureRandom())

            return keyGen.generateKey() as SecretKeyWithEncapsulation
        }
    }
}