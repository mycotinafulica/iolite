package org.amber.asparagus.iolite

import org.bouncycastle.pqc.jcajce.spec.KyberParameterSpec

class ArgumentParser {

    fun parse(args: Array<String>): Arguments {
        var spec: KyberParameterSpec? = null
        var encoding: String? = null
        var outputDirectory: String? = null
        val validSpec = arrayOf("KYBER512", "KYBER768", "KYBER1024")
        val validEncoding = arrayOf("BASE64", "DER")

        if (args.isEmpty()) {
            return Arguments(true, false,null, null, null, null)
        }

        if(args.size == 1 && args[0] == "--help") {
            return Arguments(true, false,null, null, null, null)
        }
        else if (args.size == 1){
            return Arguments(false, true, null, null, null, null)
        }

        var currInx = 0
        try {
            while (currInx < args.size) {
                if (args[currInx] == "--spec") {
                    val strSpec = args[currInx + 1]
                    if(validSpec.contains(strSpec)){
                        when(strSpec){
                            "KYBER512"  -> spec = KyberParameterSpec.kyber512
                            "KYBER768"  -> spec = KyberParameterSpec.kyber768
                            "KYBER1024" -> spec = KyberParameterSpec.kyber1024
                        }
                    }
                    else{
                        return Arguments(false, true,
                            "Valid spec is [KYBER512, KYBER768, KYBER1024]",
                            null, null, null)
                    }
                    currInx += 2
                } else if (args[currInx] == "--encoding") {
                    encoding = args[currInx + 1]
                    if(!validEncoding.contains(encoding)) {
                        return Arguments(false, true,
                            "Valid encoding is [BASE64, DER]",
                            null, null, null)
                    }

                    currInx += 2
                } else if (args[currInx] == "--output") {
                    outputDirectory = args[currInx + 1]
                    currInx += 2
                }
                else {
                    return Arguments(false, true, null, null, null, null)
                }
            }
        } catch (e: Exception) {
            return Arguments(false, true, null, null, null, null)
        }


        if(spec == null) {
            return Arguments(false, true, "Spec is required", null, null, null)
        }

        return Arguments(false, false, null, spec, encoding, outputDirectory)
    }
}

data class Arguments (
    val showHelp: Boolean,
    val invalidArgs: Boolean,
    val invalidReason: String?,
    val spec: KyberParameterSpec?,
    val encoding: String?,
    val outputDirectory: String?
)

