package org.amber.asparagus.iolite

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.amber.asparagus.iolite.crypto.Utils
import org.amber.asparagus.iolite.dto.EchoInput
import org.amber.asparagus.iolite.dto.EchoOutput
import org.amber.asparagus.iolite.dto.HandshakeInput
import org.amber.asparagus.iolite.dto.HandshakeOutput
import java.util.*

class Main {
    companion object {
        private val gson = Gson()

        @JvmStatic
        fun main(args: Array<String>) {
            Utils.registerBouncyCastleProvider()

            val classLoader = Companion::class.java.classLoader
            val inputStream = classLoader.getResourceAsStream("public.crt")

            val publicKey = inputStream!!.readAllBytes()

            println("Public key : ${Base64.getEncoder().encodeToString(publicKey)}")

            val encapsulatedKey = Utils.generateEncapsulatedAesKey(publicKey)
            val encapsulatedB64 = Base64.getEncoder().encodeToString(encapsulatedKey.encapsulation)

            println("Generated Aes key : ${Base64.getEncoder().encodeToString(encapsulatedKey.encoded)}")
            println("EncapsulatedKey : ${Base64.getEncoder().encodeToString(encapsulatedKey.encapsulation)}")

            val handshakeInput = HandshakeInput(encapsulatedB64)
            val json           = gson.toJson(handshakeInput)

            val client      = createOkhttpClient()
            val mediaType   = "application/json".toMediaType()
            val requestBody = json.toRequestBody(mediaType)

            val request = Request.Builder()
                .url("http://localhost:8080/iolite/api/handshake")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val handshakeResp = gson.fromJson(response.body?.string(), HandshakeOutput::class.java)

            val sessionId = Utils.aesGcmDecrypt(handshakeResp.sessionInfo, encapsulatedKey.encoded)

            println("Session ID : $sessionId")

            val echoInput = EchoInput(sessionId, Utils.aesGcmEncrypt("Hello world", encapsulatedKey.encoded))
            val echoJson  = gson.toJson(echoInput)

            val echoRequestBody = echoJson.toRequestBody(mediaType)
            val echoRequest = Request.Builder()
                .url("http://localhost:8080/iolite/api/echo")
                .post(echoRequestBody)
                .build()

            val echoResponse = client.newCall(echoRequest).execute()
            val echoRespObj  = gson.fromJson(echoResponse.body?.string(), EchoOutput::class.java)

            println(echoRespObj.echo)
        }

        private fun createOkhttpClient(): OkHttpClient {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY // Set the desired log level
            }

            return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
        }
    }
}