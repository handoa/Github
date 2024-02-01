package com.example.github.data

import java.io.Serializable

data class Message (
    val userId: String = "",
    val nickname: String = "",
    val message: String = "",
    val time: String = ""
) : Serializable