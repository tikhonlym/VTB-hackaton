package com.preload.vtb_hackaton.data.model

import com.google.gson.annotations.SerializedName

data class AnswersRequest(
    @SerializedName("interviewId")
    val interviewId: String,
    
    @SerializedName("answers")
    val answers: List<Answer>
)
