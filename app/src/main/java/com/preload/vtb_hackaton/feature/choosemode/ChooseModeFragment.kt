package com.preload.vtb_hackaton.feature.choosemode

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.preload.vtb_hackaton.R
import com.preload.vtb_hackaton.data.model.Question
import com.preload.vtb_hackaton.databinding.ChooseModeScreenBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChooseModeFragment : Fragment(R.layout.choose_mode_screen) {

    private var questions: List<Question> = emptyList()
    private var interviewId: String? = null

    companion object {
        private const val TAG = "ChooseModeFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = ChooseModeScreenBinding.bind(view)

        // Отключаем кнопку "Назад"
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Ничего не делаем - блокируем возврат назад
                Log.d(TAG, "Back button pressed - blocked")
            }
        })

        // Загружаем вопросы из аргументов (от FallbackFragment)
        loadQuestionsFromArguments()

        // Настраиваем кнопку "Начать"
        binding.startButton.setOnClickListener {
            startInterview()
        }
    }

    private fun loadQuestionsFromArguments() {
        val questionsJson = arguments?.getString("questions_json")
        interviewId = arguments?.getString("interview_id")
        val questionsCount = arguments?.getInt("questions_count", 0) ?: 0

        Log.d(TAG, "Loading from arguments (from FallbackFragment):")
        Log.d(TAG, "  questionsJson: $questionsJson")
        Log.d(TAG, "  interviewId: $interviewId")
        Log.d(TAG, "  questionsCount: $questionsCount")

        if (questionsJson != null && interviewId != null && questionsCount > 0) {
            try {
                questions = questionsJson.split("|").map { questionString ->
                    val parts = questionString.split(":", limit = 2)
                    Question(
                        id = parts[0].toInt(),
                        question = parts[1]
                    )
                }
                Log.d(TAG, "Successfully loaded ${questions.size} questions for interview $interviewId")
                questions.forEachIndexed { index, question ->
                    Log.d(TAG, "  Question ${index + 1}: ${question.question}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing questions: ${e.message}")
                Log.e(TAG, "Raw questionsJson: $questionsJson")
                questions = emptyList()
            }
        } else {
            Log.w(TAG, "No questions or interview ID found in arguments")
            Log.w(TAG, "  questionsJson is null: ${questionsJson == null}")
            Log.w(TAG, "  interviewId is null: ${interviewId == null}")
            Log.w(TAG, "  questionsCount: $questionsCount")
        }
    }

    private fun startInterview() {
        if (questions.isNotEmpty() && interviewId != null) {
            Log.d(TAG, "Starting interview with ${questions.size} questions")
            
            // Передаем данные в InterviewFragment через аргументы
            val questionsJson = questions.joinToString("|") { "${it.id}:${it.question}" }
            val bundle = Bundle().apply {
                putString("questions_json", questionsJson)
                putString("interview_id", interviewId)
                putInt("questions_count", questions.size)
            }
            
            Log.d(TAG, "Passing data to InterviewFragment:")
            Log.d(TAG, "  questionsJson: $questionsJson")
            Log.d(TAG, "  interviewId: $interviewId")
            Log.d(TAG, "  questionsCount: ${questions.size}")
            
            findNavController().navigate(R.id.action_chooseModeFragment_to_interviewFragment, bundle)
        } else {
            Log.e(TAG, "Cannot start interview: questions=${questions.size}, interviewId=$interviewId")
        }
    }
}