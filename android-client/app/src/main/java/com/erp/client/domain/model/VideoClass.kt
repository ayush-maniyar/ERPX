package com.erp.client.domain.model

data class VideoClass(
    val id: Long,
    val title: String,
    val targetTag: String,
    val meetLink: String,
    val scheduledTime: String
)
