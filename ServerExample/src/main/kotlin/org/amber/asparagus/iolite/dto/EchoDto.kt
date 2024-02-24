package org.amber.asparagus.iolite.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class EchoInput(
    @JsonProperty("session_info")
    val sessionInfo: String,
    @JsonProperty("value")
    val value: String
)

data class EchoOutput(
    @JsonProperty("echo")
    val echo: String
)