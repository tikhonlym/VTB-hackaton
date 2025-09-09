package com.preload.vtb_hackaton.data.model

import com.google.gson.annotations.SerializedName

data class Answer(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("answer")
    val answer: String
)
