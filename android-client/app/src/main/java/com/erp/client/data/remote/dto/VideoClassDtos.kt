package com.erp.client.data.remote.dto

data class CreateVideoClassRequest(
    val title: String,
    val targetTag: String,
    val meetLink: String,
    val scheduledTime: String
)
