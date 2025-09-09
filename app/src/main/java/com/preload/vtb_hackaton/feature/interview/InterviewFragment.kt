package com.preload.vtb_hackaton.feature.interview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.preload.vtb_hackaton.R
import com.preload.vtb_hackaton.data.model.Answer
import com.preload.vtb_hackaton.data.model.AnswersRequest
import com.preload.vtb_hackaton.data.model.Question
import com.preload.vtb_hackaton.data.repository.InterviewRepository
import com.preload.vtb_hackaton.databinding.InterviewScreenBinding
import com.preload.vtb_hackaton.utils.LoadingDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class InterviewFragment : Fragment(R.layout.interview_screen), TextToSpeech.OnInitListener {

    private lateinit var progressIndicator: LinearProgressIndicator
    private var progressJob: Job? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsInitialized = false
    private var interviewId: String? = null
    private var questions: List<Question> = emptyList()
    private var currentQuestionIndex = 0
    private var answers: MutableList<Answer> = mutableListOf()
    private val interviewRepository = InterviewRepository()
    
    // Таймер и голосовой ввод
    private lateinit var questionTimer: QuestionTimer
    private lateinit var recognizedTextView: TextView
    
    // Состояние текущего ответа
    private var currentAnswerText = ""
    private var isRecording = false
    
    // Лоадер
    private var loadingDialog: LoadingDialog? = null
    
    // Launcher для Google Voice Typing
    private val voiceInputLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        handleVoiceInputResult(result.resultCode, result.data)
    }

    companion object {
        private const val TAG = "InterviewFragment"
        private const val ARG_INTERVIEW_ID = "interview_id"
        
        fun newInstance(interviewId: String? = null): InterviewFragment {
            val fragment = InterviewFragment()
            val args = Bundle()
            args.putString(ARG_INTERVIEW_ID, interviewId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = InterviewScreenBinding.bind(view)

        // Отключаем кнопку "Назад"
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Ничего не делаем - блокируем возврат назад
                Log.d(TAG, "Back button pressed - blocked")
            }
        })

        // Получаем ID интервью из аргументов навигации или из SharedPreferences (для deep link)
        interviewId = arguments?.getString("interview_id") 
            ?: getDeepLinkIdFromSharedPrefs()
        
        Log.d(TAG, "Interview ID from arguments: ${arguments?.getString("interview_id")}")
        Log.d(TAG, "Interview ID from SharedPrefs: ${getDeepLinkIdFromSharedPrefs()}")
        Log.d(TAG, "Final Interview ID: $interviewId")

        // Инициализация TTS
        textToSpeech = TextToSpeech(requireContext(), this)
        
        // Инициализация таймера
        questionTimer = QuestionTimer(
            onTick = { timeLeft ->
                updateTimerUI(timeLeft)
            },
            onFinish = {
                handleTimeUp()
            }
        )
        
        progressIndicator = binding.linearProgressIndicator
        progressIndicator.max = 100
        
        // Инициализируем TextView для распознанного текста
        recognizedTextView = binding.recognizedText

        // Настраиваем кнопки
        binding.next.setOnClickListener {
            handleNextQuestion()
        }
        
        binding.await.setOnClickListener {
            // Запускаем голосовой ввод
            startVoiceInput()
        }

        // Загружаем вопросы (проверка разрешений будет позже)
        loadQuestions()
    }

    private fun getDeepLinkIdFromSharedPrefs(): String? {
        val sharedPref = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val id = sharedPref.getString("deep_link_id", null)
        // Очищаем ID после использования
        if (id != null) {
            sharedPref.edit().remove("deep_link_id").apply()
        }
        return id
    }

    
    private fun updateTimerUI(timeLeft: Long) {
        try {
            val binding = InterviewScreenBinding.bind(requireView())
            val minutes = timeLeft / (1000 * 60)
            val seconds = (timeLeft % (1000 * 60)) / 1000
            binding.textView5.text = "У вас ${String.format("%02d:%02d", minutes, seconds)} на ответ!"
            
            // Обновляем прогресс бар
            val totalTime = 3 * 60 * 1000L // 3 минуты в миллисекундах
            val progress = ((totalTime - timeLeft) * 100 / totalTime).toInt()
            progressIndicator.setProgress(progress, true)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating timer UI: ${e.message}")
        }
    }
    
    private fun handleTimeUp() {
        Log.d(TAG, "Time is up for question ${currentQuestionIndex + 1}")
        
        // Останавливаем таймер и запись
        questionTimer.stopTimer()
        isRecording = false
        
        // Записываем накопленный ответ (может быть пустым)
        val currentQuestion = questions[currentQuestionIndex]
        val answer = Answer(
            id = currentQuestion.id,
            answer = currentAnswerText
        )
        answers.add(answer)
        
        Log.d(TAG, "Time up answer for question ${currentQuestion.id}: '$currentAnswerText'")
        
        // Показываем финальный ответ
        recognizedTextView.text = "Время истекло!\n\nФинальный ответ: $currentAnswerText"
        
        // Ждем 3 секунды перед переходом к следующему вопросу
        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            recognizedTextView.visibility = View.GONE
            
            // Переходим к следующему вопросу
            currentQuestionIndex++
            if (currentQuestionIndex < questions.size) {
                showCurrentQuestion()
            } else {
                submitAnswers()
            }
        }
    }
    
    private fun startContinuousRecording() {
        if (isRecording) {
            Log.d(TAG, "Already recording, skipping")
            return
        }
        
        isRecording = true
        Log.d(TAG, "Starting Google Voice Typing for question ${currentQuestionIndex + 1}")
        
        // Показываем область для распознанного текста
        recognizedTextView.visibility = View.VISIBLE
        recognizedTextView.text = "Запускаем голосовой ввод..."
        
        // Запускаем Google Voice Typing прямо из Fragment
        startGoogleVoiceTyping()
    }
    
    private fun startGoogleVoiceTyping() {
        try {
            // Создаем Intent для голосового ввода как в примере GeeksforGeeks
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            
            // Устанавливаем модель языка
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            
            // Устанавливаем русский язык
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU")
            
            // Устанавливаем подсказку
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Говорите ваш ответ...")
            
            // Запускаем голосовой ввод
            voiceInputLauncher.launch(intent)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error starting voice input: ${e.message}")
            isRecording = false
        }
    }
    
    private fun handleVoiceInputResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            // Получаем результаты как в примере GeeksforGeeks
            val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!results.isNullOrEmpty()) {
                val recognizedText = results[0]
                Log.d(TAG, "Voice input result: '$recognizedText'")
                
                // Добавляем к накопленному тексту
                currentAnswerText = if (currentAnswerText.isEmpty()) {
                    recognizedText
                } else {
                    "$currentAnswerText $recognizedText"
                }
                
                // Показываем накопленный текст
                recognizedTextView.text = "Слушаю...\n\n$currentAnswerText"
                
                // Автоматически продолжаем голосовой ввод через 2 секунды
                CoroutineScope(Dispatchers.Main).launch {
                    delay(2000)
                    if (questionTimer.isRunning()) {
                        startGoogleVoiceTyping()
                    }
                }
            }
        } else {
            Log.d(TAG, "Voice input cancelled or failed")
            // При отмене или ошибке тоже продолжаем через 3 секунды
            CoroutineScope(Dispatchers.Main).launch {
                delay(3000)
                if (questionTimer.isRunning()) {
                    startGoogleVoiceTyping()
                }
            }
        }
    }
    
    private fun startVoiceInput() {
        // Этот метод теперь используется только для кнопки "Далее"
        if (currentQuestionIndex < questions.size) {
            // Запускаем голосовой ввод для текущего вопроса
            startContinuousRecording()
        }
    }
    

    private fun loadQuestions() {
        // Показываем лоадер
        loadingDialog = LoadingDialog.show(
            requireContext(),
            "Загрузка вопросов",
            "Подготовка интервью..."
        )
        
        // Сначала проверяем, есть ли уже загруженные вопросы в SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val questionsJson = sharedPref.getString("loaded_questions", null)
        
        // Сначала проверяем аргументы навигации
        val questionsJsonFromArgs = arguments?.getString("questions_json")
        val interviewIdFromArgs = arguments?.getString("interview_id")
        
        if (questionsJsonFromArgs != null && interviewIdFromArgs != null) {
            // Данные пришли из аргументов (от ChooseModeFragment)
            try {
                questions = questionsJsonFromArgs.split("|").map { questionString ->
                    val parts = questionString.split(":", limit = 2)
                    Question(
                        id = parts[0].toInt(),
                        question = parts[1]
                    )
                }
                interviewId = interviewIdFromArgs
                Log.d(TAG, "Loaded ${questions.size} questions from arguments")
                
                // Скрываем лоадер
                loadingDialog?.hide()
                
                if (questions.isNotEmpty()) {
                    // Начинаем интервью сразу (пришли из ChooseModeFragment)
                    startInterview()
                } else {
                    Log.w(TAG, "No questions found in arguments")
                    findNavController().navigate(R.id.action_interviewFragment_to_interviewResultFragment)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing questions from arguments: ${e.message}")
                // Скрываем лоадер
                loadingDialog?.hide()
                // Если ошибка парсинга, переходим к результатам
                findNavController().navigate(R.id.action_interviewFragment_to_interviewResultFragment)
            }
        } else if (questionsJson != null) {
            // Вопросы уже загружены в SharedPreferences, парсим их
            try {
                questions = questionsJson.split("|").map { questionString ->
                    val parts = questionString.split(":", limit = 2)
                    Question(
                        id = parts[0].toInt(),
                        question = parts[1]
                    )
                }
                Log.d(TAG, "Loaded ${questions.size} questions from SharedPreferences")
                
                // Скрываем лоадер
                loadingDialog?.hide()
                
                if (questions.isNotEmpty()) {
                    // Начинаем интервью сразу
                    startInterview()
                } else {
                    Log.w(TAG, "No questions found in SharedPreferences")
                    findNavController().navigate(R.id.action_interviewFragment_to_interviewResultFragment)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing questions from SharedPreferences: ${e.message}")
                // Скрываем лоадер
                loadingDialog?.hide()
                // Если ошибка парсинга, переходим к результатам
                findNavController().navigate(R.id.action_interviewFragment_to_interviewResultFragment)
            }
        } else {
            // Вопросы не загружены, переходим к результатам
            Log.w(TAG, "No questions found, navigating to results")
            // Скрываем лоадер
            loadingDialog?.hide()
            findNavController().navigate(R.id.action_interviewFragment_to_interviewResultFragment)
        }
    }
    
    
    
    private fun startInterview() {
        if (questions.isNotEmpty()) {
            currentQuestionIndex = 0
            showCurrentQuestion()
        }
    }
    
    private fun showCurrentQuestion() {
        if (currentQuestionIndex < questions.size) {
            val currentQuestion = questions[currentQuestionIndex]
            Log.d(TAG, "Showing question ${currentQuestionIndex + 1}/${questions.size}: ${currentQuestion.question}")
            
            try {
                // Сбрасываем состояние для нового вопроса
                currentAnswerText = ""
                isRecording = false
                
                // Обновляем текст вопроса в интерфейсе
                val binding = InterviewScreenBinding.bind(requireView())
                binding.textView4.text = currentQuestion.question
                
                // Скрываем область распознанного текста
                recognizedTextView.visibility = View.GONE
                
                // Сбрасываем таймер
                questionTimer.reset()
                
                // Озвучиваем вопрос и запускаем запись
                CoroutineScope(Dispatchers.Main).launch {
                    // Ждем инициализации TTS
                    var attempts = 0
                    while (!isTtsInitialized && attempts < 10) {
                        delay(500L)
                        attempts++
                    }
                    
                    // Озвучиваем вопрос
                    speakText(currentQuestion.question)
                    
                    // Ждем завершения озвучивания (примерно 3-5 секунд)
                    delay(3000L)
                    
                    // Запускаем таймер на 3 минуты
                    questionTimer.startTimer()
                    
                    // Сразу начинаем запись голоса
                    startContinuousRecording()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error showing question: ${e.message}")
            }
        }
    }
    
    private fun handleNextQuestion() {
        if (currentQuestionIndex < questions.size) {
            // Останавливаем таймер и запись
            questionTimer.stopTimer()
            isRecording = false
            
            // Записываем накопленный ответ
            val currentQuestion = questions[currentQuestionIndex]
            val answer = Answer(
                id = currentQuestion.id,
                answer = currentAnswerText
            )
            answers.add(answer)
            
            Log.d(TAG, "Manual next - answer for question ${currentQuestion.id}: '$currentAnswerText'")
            
            // Показываем финальный ответ
            recognizedTextView.text = "Ответ сохранен!\n\n$currentAnswerText"
            
            // Ждем 2 секунды перед переходом к следующему вопросу
            CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                recognizedTextView.visibility = View.GONE
                
                // Переходим к следующему вопросу
                currentQuestionIndex++
                if (currentQuestionIndex < questions.size) {
                    showCurrentQuestion()
                } else {
                    submitAnswers()
                }
            }
        }
    }
    
    private fun submitAnswers() {
        if (interviewId != null && answers.isNotEmpty()) {
            // Показываем лоадер
            loadingDialog = LoadingDialog.show(
                requireContext(),
                "Отправка ответов",
                "Сохранение результатов интервью..."
            )
            
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val answersRequest = AnswersRequest(
                        interviewId = interviewId!!,
                        answers = answers
                    )
                    
                    val result = interviewRepository.postAnswers(answersRequest)
                    
                    if (result.isSuccess) {
                        Log.d(TAG, "Answers submitted successfully: ${result.getOrNull()}")
                    } else {
                        Log.e(TAG, "Failed to submit answers: ${result.exceptionOrNull()?.message}")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error submitting answers: ${e.message}")
                }
                
                // Скрываем лоадер
                loadingDialog?.hide()
                
                // Очищаем все данные сессии
                clearSessionData()
                
                // Переходим к результатам независимо от результата отправки
                findNavController().navigate(R.id.action_interviewFragment_to_interviewResultFragment)
            }
        } else {
            // Очищаем все данные сессии
            clearSessionData()
            
            // Переходим к результатам
            findNavController().navigate(R.id.action_interviewFragment_to_interviewResultFragment)
        }
    }
    
    private fun clearSessionData() {
        try {
            val sharedPref = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            sharedPref.edit()
                .remove("deep_link_id")
                .remove("loaded_questions")
                .remove("interview_id")
                .apply()
            
            Log.d(TAG, "Session data cleared")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing session data: ${e.message}")
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        progressJob?.cancel()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        
        // Очищаем ресурсы таймера и записи
        questionTimer.stopTimer()
        isRecording = false
        
        // Скрываем лоадер если он показывается
        loadingDialog?.hide()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true
            
            // Устанавливаем русский язык
            val result = textToSpeech?.setLanguage(Locale("ru", "RU"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Если русский язык не поддерживается, используем английский
                textToSpeech?.setLanguage(Locale.ENGLISH)
            }
            
            // Настраиваем скорость и тон речи для лучшего качества
            textToSpeech?.setSpeechRate(1.2f) // Оптимальная скорость
            textToSpeech?.setPitch(1f) // Немного ниже тон для более приятного звучания
            
            Log.d("TTS", "TTS initialized successfully")
        } else {
            Log.e("TTS", "TTS initialization error")
        }
    }

    fun speakText(text: String) {
        if (isTtsInitialized && textToSpeech != null) {
            val result = textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            if (result == TextToSpeech.ERROR) {
                Log.e("TTS", "Text-to-speech error")
                // If error occurred, try again
                textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                Log.d("TTS", "Text spoken: $text")
            }
        } else {
            Log.w("TTS", "TTS not initialized, trying to reinitialize")
            // If TTS not initialized, try to initialize again
            textToSpeech = TextToSpeech(requireContext(), this)
        }
    }


    /**
     * Остановить озвучку
     */
    fun stopSpeaking() {
        textToSpeech?.stop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopSpeaking()
    }

}