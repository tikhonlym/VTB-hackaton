package com.preload.vtb_hackaton.data.model

import com.google.gson.annotations.SerializedName

data class Question(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("question")
    val question: String
)
