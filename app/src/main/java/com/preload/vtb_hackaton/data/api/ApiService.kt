package com.preload.vtb_hackaton.data.api

import com.preload.vtb_hackaton.data.model.AnswersRequest
import com.preload.vtb_hackaton.data.model.QuestionsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    
    @GET("api/v1/questions")
    suspend fun getQuestions(
        @Query("interview_id") interviewId: String
    ): Response<QuestionsResponse>
    
    @POST("api/v1/answers")
    suspend fun postAnswers(
        @Body answersRequest: AnswersRequest
    ): Response<String>
}
