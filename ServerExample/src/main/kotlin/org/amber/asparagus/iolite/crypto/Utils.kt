package org.amber.asparagus.iolite.crypto

import org.bouncycastle.jcajce.spec.KEMExtractSpec
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.SecureRandom
import java.security.Security
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*
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

        fun decapsulateKey(privateKey: String, encapsulatedKey: String): ByteArray {
            val privateKeyFmt        = loadPkcs8KyberPrivateKey(privateKey)
            val encapsulatedKeyBytes = Base64.getDecoder().decode(encapsulatedKey)
            val keyGen = KeyGenerator.getInstance("KYBER", "BCPQC")
            keyGen.init(KEMExtractSpec(privateKeyFmt, encapsulatedKeyBytes, "AES"), SecureRandom())

            return keyGen.generateKey().encoded
        }

        private fun loadPkcs8KyberPrivateKey(privateKey: String): PrivateKey {
            val privateKeyDecoded   = Base64.getDecoder().decode(privateKey)
            val pkcs8EncodedKeySpec = PKCS8EncodedKeySpec(privateKeyDecoded)
            var keyFactory: KeyFactory? = null
            keyFactory = KeyFactory.getInstance("KYBER", "BCPQC")
            return keyFactory.generatePrivate(pkcs8EncodedKeySpec)
        }
    }
}