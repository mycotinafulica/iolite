package org.amber.asparagus.iolite.dto

import com.google.gson.annotations.SerializedName

data class EchoInput(
    @SerializedName("session_info")
    val sessionInfo: String,
    val value: String
)

data class EchoOutput(
    val echo: String
)