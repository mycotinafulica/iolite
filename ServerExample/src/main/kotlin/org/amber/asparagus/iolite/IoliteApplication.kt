package org.amber.asparagus.iolite

import org.amber.asparagus.iolite.crypto.Utils
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.event.EventListener
import java.security.Security

@SpringBootApplication
class IoliteApplication

fun main(args: Array<String>) {
	Utils.registerBouncyCastleProvider()
	runApplication<IoliteApplication>(*args)
}