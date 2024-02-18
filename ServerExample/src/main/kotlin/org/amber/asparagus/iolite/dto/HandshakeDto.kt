package org.amber.asparagus.iolite.dto

data class HandshakeInput(val encapsulatedSecret: String)

data class HandshakeOutput(val status: String, val sessionInfo: String)
