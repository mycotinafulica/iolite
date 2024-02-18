package org.amber.asparagus.iolite.dto

import com.google.gson.annotations.SerializedName

data class HandshakeInput(
    @SerializedName("encapsulated_secret")
    val encapsulatedSecret: String
)

data class HandshakeOutput(
    val status: String,
    @SerializedName("session_info")
    val sessionInfo: String
)