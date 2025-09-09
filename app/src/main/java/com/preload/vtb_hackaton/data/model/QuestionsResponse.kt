package com.preload.vtb_hackaton.data.model

import com.google.gson.annotations.SerializedName

data class QuestionsResponse(
    @SerializedName("questions")
    val questions: List<Question>
)
