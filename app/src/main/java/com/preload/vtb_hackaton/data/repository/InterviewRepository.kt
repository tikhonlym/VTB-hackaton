package com.preload.vtb_hackaton.data.repository

import com.preload.vtb_hackaton.data.api.ApiService
import com.preload.vtb_hackaton.data.model.AnswersRequest
import com.preload.vtb_hackaton.data.model.QuestionsResponse
import com.preload.vtb_hackaton.data.network.NetworkModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InterviewRepository {
    
    private val apiService: ApiService = NetworkModule.apiService
    
    suspend fun getQuestions(interviewId: String): Result<QuestionsResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getQuestions(interviewId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to get questions: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun postAnswers(answersRequest: AnswersRequest): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.postAnswers(answersRequest)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to post answers: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
