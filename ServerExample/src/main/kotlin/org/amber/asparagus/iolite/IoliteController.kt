package org.amber.asparagus.iolite

import org.amber.asparagus.iolite.crypto.Utils
import org.amber.asparagus.iolite.dto.EchoInput
import org.amber.asparagus.iolite.dto.EchoOutput
import org.amber.asparagus.iolite.dto.HandshakeInput
import org.amber.asparagus.iolite.dto.HandshakeOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.filter.CommonsRequestLoggingFilter
import java.security.Security
import java.util.*
import kotlin.collections.HashMap

@RestController
@RequestMapping("/iolite/api")
class IoliteController {

    // Session ID to key pair. In real implementation, something like redis db might be appropriate, so we can set session expiry.
    private val sessions: HashMap<String, ByteArray> = HashMap()

    @Autowired
    @Qualifier("privateKey")
    private lateinit var privateKey: String

    @PostMapping("/echo")
    fun echo(@RequestBody input: EchoInput): ResponseEntity<EchoOutput> {
        val valueToEcho = Utils.aesGcmDecrypt(input.value, sessions[input.sessionInfo]!!)

        val response = EchoOutput("Echo from server : $valueToEcho!")

        return ResponseEntity<EchoOutput>(response, HttpStatus.OK)
    }

    @PostMapping("/handshake")
    fun handshake(@RequestBody handshakeInput: HandshakeInput): ResponseEntity<HandshakeOutput> {
        val encapsulatedKey = handshakeInput.encapsulatedSecret
        println("Encapsulated key : $encapsulatedKey")
        val sessionKey      = Utils.decapsulateKey(privateKey, encapsulatedKey)
        val sessionId       = UUID.randomUUID().toString()

        println("Session ID  : $sessionId")
        println("Session key : ${Base64.getEncoder().encodeToString(sessionKey)}")

        sessions.put(sessionId, sessionKey)

        val response = HandshakeOutput("00", Utils.aesGcmEncrypt(sessionId, sessionKey))

        return ResponseEntity<HandshakeOutput>(response, HttpStatus.OK)
    }
}