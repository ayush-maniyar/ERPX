package com.erp.client.data.remote.dto

data class SendTagEmailRequest(
    val tagName: String,
    val subject: String,
    val body: String
)
