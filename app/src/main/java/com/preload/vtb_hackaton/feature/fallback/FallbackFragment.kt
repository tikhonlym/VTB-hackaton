package com.preload.vtb_hackaton.feature.fallback

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.preload.vtb_hackaton.R
import com.preload.vtb_hackaton.data.repository.InterviewRepository
import com.preload.vtb_hackaton.databinding.FallBackScreenBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FallbackFragment : Fragment(R.layout.fall_back_screen) {

    private val interviewRepository = InterviewRepository()
    private lateinit var sharedPreferences: SharedPreferences
    private var lastProcessedTimestamp: Long = 0
    private var sharedPreferencesListener: SharedPreferences.OnSharedPreferenceChangeListener? = null
    
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "FallbackFragment onAttach called")
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "FallbackFragment onCreate called")
    }
    
    // Список необходимых разрешений (только критически важные)
    private val requiredPermissions = arrayOf(
        Manifest.permission.RECORD_AUDIO
    )
    
    // Флаг для отслеживания повторных запросов
    private var isRetryRequest = false

    companion object {
        private const val TAG = "FallbackFragment"
        private const val PERMISSION_REQUEST_CODE = 1001
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "FallbackFragment onViewCreated called")
        val binding = FallBackScreenBinding.bind(view)

        // Инициализируем SharedPreferences и listener
        initSharedPreferencesListener()

        // Показываем лоадер с самого начала
        showLoading(binding)

        // Отключаем кнопку "Назад"
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Ничего не делаем - блокируем возврат назад
                Log.d(TAG, "Back button pressed - blocked")
            }
        })

        // Сначала проверяем разрешения
        checkPermissions()
    }
    
    private fun checkPermissions() {
        Log.d(TAG, "Checking required permissions...")
        Log.d(TAG, "Android version: ${Build.VERSION.SDK_INT} (API ${Build.VERSION.SDK_INT})")
        Log.d(TAG, "Required permissions: ${requiredPermissions.joinToString()}")
        
        val missingPermissions = requiredPermissions.filter { permission ->
            val isGranted = ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
            Log.d(TAG, "Permission $permission: ${if (isGranted) "GRANTED" else "DENIED"}")
            !isGranted
        }
        
        Log.d(TAG, "Missing permissions: $missingPermissions")
        
        if (missingPermissions.isEmpty()) {
            Log.d(TAG, "All permissions already granted")
            checkQuestionsAndNavigate()
        } else {
            Log.d(TAG, "Requesting missing permissions: ${missingPermissions.joinToString()}")
            requestPermissions(missingPermissions.toTypedArray())
        }
    }
    
    private fun requestPermissions(permissions: Array<String>) {
        Log.d(TAG, "Requesting permissions: ${permissions.joinToString()}")
        Log.d(TAG, "Activity: ${requireActivity()}")
        Log.d(TAG, "Request code: $PERMISSION_REQUEST_CODE")
        
        try {
            // Используем ActivityCompat.requestPermissions для гарантированного показа системной плашки
            ActivityCompat.requestPermissions(requireActivity(), permissions, PERMISSION_REQUEST_CODE)
            Log.d(TAG, "ActivityCompat.requestPermissions called successfully")
            
            // Добавляем небольшую задержку для проверки
            CoroutineScope(Dispatchers.Main).launch {
                delay(1000)
                Log.d(TAG, "1 second after requestPermissions - checking if dialog appeared")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting permissions: ${e.message}")
            e.printStackTrace()
        }
    }
    
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        Log.d(TAG, "onRequestPermissionsResult called with requestCode: $requestCode")
        Log.d(TAG, "Permissions: ${permissions.joinToString()}")
        Log.d(TAG, "Grant results: ${grantResults.joinToString()}")
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            
            if (allGranted) {
                Log.d(TAG, "All permissions granted, proceeding with app flow")
                checkQuestionsAndNavigate()
            } else {
                Log.w(TAG, "Some permissions were denied")
                // Скрываем лоадер при отказе в разрешениях
                val binding = FallBackScreenBinding.bind(requireView())
                hideLoading(binding)
                
                if (isRetryRequest) {
                    // Повторный отказ - закрываем приложение
                    Log.w(TAG, "Permissions denied again, closing app")
                    showFinalPermissionDeniedToast()
                    closeApp()
                } else {
                    // Первый отказ - показываем системную плашку еще раз
                    Log.d(TAG, "First permission denial, requesting again with system dialog")
                    isRetryRequest = true
                    requestPermissions(requiredPermissions)
                }
            }
        }
    }
    
    private fun showFinalPermissionDeniedToast() {
        Toast.makeText(
            requireContext(),
            "Без разрешений приложение не может работать. Приложение будет закрыто.",
            Toast.LENGTH_LONG
        ).show()
    }
    
    private fun closeApp() {
        Log.d(TAG, "Closing app due to missing permissions")
        requireActivity().finishAffinity()
    }

    private fun checkQuestionsAndNavigate() {
        Log.d(TAG, "All permissions granted, now checking for questions and deciding navigation...")
        
        // Получаем binding для управления UI
        val binding = FallBackScreenBinding.bind(requireView())
        
        // Получаем ID интервью из SharedPreferences (от deep link)
        val sharedPref = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val interviewId = sharedPref.getString("deep_link_id", null)
        
        Log.d(TAG, "Interview ID from deep link: $interviewId")

        if (interviewId != null) {
            // Есть ID интервью, загружаем вопросы с сервера
            Log.d(TAG, "Found interview ID, loading questions from server")
            loadQuestionsAndNavigate(interviewId, binding)
        } else {
            // Нет ID интервью, показываем ошибку
            Log.w(TAG, "No interview ID found, showing error message")
            hideLoading(binding)
            // Здесь можно показать сообщение об ошибке пользователю
            // Например, "Ссылка недействительна" или "Интервью не найдено"
            // Пока просто остаемся на FallbackFragment
        }
    }
    
    private fun loadQuestionsAndNavigate(interviewId: String, binding: FallBackScreenBinding) {
        Log.d(TAG, "Starting to load questions from server (after permissions check)")
        CoroutineScope(Dispatchers.Main).launch {
            try {
                Log.d(TAG, "Making API call with ID: $interviewId (permissions already granted)")
                val result = interviewRepository.getQuestions(interviewId)
                
                if (result.isSuccess) {
                    val questions = result.getOrNull()?.questions ?: emptyList()
                    Log.d(TAG, "Successfully loaded ${questions.size} questions from server")
                    
                    if (questions.isNotEmpty()) {
                        // Переходим к ChooseModeFragment с вопросами
                        navigateToChooseMode(questions, interviewId)
                    } else {
                        Log.w(TAG, "No questions received")
                        hideLoading(binding)
                        // Остаемся на FallbackFragment с ошибкой
                    }
                } else {
                    Log.e(TAG, "Failed to load questions: ${result.exceptionOrNull()?.message}")
                    hideLoading(binding)
                    // Остаемся на FallbackFragment с ошибкой
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading questions: ${e.message}")
                hideLoading(binding)
                // Остаемся на FallbackFragment с ошибкой
            }
        }
    }
    
    private fun navigateToChooseMode(questions: List<com.preload.vtb_hackaton.data.model.Question>, interviewId: String) {
        Log.d(TAG, "Navigating to ChooseModeFragment with ${questions.size} questions")
        
        // Подготавливаем данные для передачи через аргументы
        val questionsJson = questions.joinToString("|") { "${it.id}:${it.question}" }
        
        val bundle = Bundle().apply {
            putString("questions_json", questionsJson)
            putString("interview_id", interviewId)
            putInt("questions_count", questions.size)
        }
        
        Log.d(TAG, "Questions data prepared:")
        Log.d(TAG, "  questionsJson: $questionsJson")
        Log.d(TAG, "  interviewId: $interviewId")
        Log.d(TAG, "  questions count: ${questions.size}")
        
        // Проверяем текущий destination перед навигацией
        val navController = findNavController()
        val currentDestination = navController.currentDestination
        
        Log.d(TAG, "Current destination: ${currentDestination?.id}")
        Log.d(TAG, "Current destination label: ${currentDestination?.label}")
        
        // Небольшая задержка для показа экрана
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000) // 1 секунда задержки
            
            try {
                // Проверяем, можем ли мы навигировать из текущего destination
                if (currentDestination?.id == R.id.fallbackFragment) {
                    Log.d(TAG, "Navigating from FallbackFragment to ChooseModeFragment")
                    navController.navigate(R.id.action_fallbackFragment_to_chooseModeFragment, bundle)
                } else {
                    Log.w(TAG, "Cannot navigate from current destination: ${currentDestination?.id}")
                    Log.w(TAG, "Current destination is not FallbackFragment, using global navigation")
                    
                    // Используем глобальную навигацию для безопасного перехода
                    navController.navigate(R.id.action_global_chooseModeFragment, bundle)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Navigation error: ${e.message}", e)
                // В случае ошибки навигации, показываем ошибку пользователю
                hideLoading(FallBackScreenBinding.bind(requireView()))
            }
        }
    }
    
    private fun showLoading(binding: FallBackScreenBinding) {
        Log.d(TAG, "Showing loading state")
        binding.loadingProgressBar.visibility = View.VISIBLE
        binding.loadingText.visibility = View.VISIBLE
        binding.textView.visibility = View.GONE
        binding.textView2.visibility = View.GONE
    }
    
    private fun hideLoading(binding: FallBackScreenBinding) {
        Log.d(TAG, "Hiding loading state")
        binding.loadingProgressBar.visibility = View.GONE
        binding.loadingText.visibility = View.GONE
        binding.textView.visibility = View.VISIBLE
        binding.textView2.visibility = View.VISIBLE
    }
    
    private fun initSharedPreferencesListener() {
        sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        
        // Создаем listener
        sharedPreferencesListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "deep_link_timestamp") {
                val newTimestamp = sharedPreferences.getLong("deep_link_timestamp", 0)
                if (newTimestamp > lastProcessedTimestamp) {
                    Log.d(TAG, "New deep link detected, timestamp: $newTimestamp")
                    lastProcessedTimestamp = newTimestamp
                    handleNewDeepLink()
                }
            }
        }
        
        // Регистрируем listener
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)
        
        // Инициализируем timestamp
        lastProcessedTimestamp = sharedPreferences.getLong("deep_link_timestamp", 0)
        Log.d(TAG, "SharedPreferences listener initialized, last timestamp: $lastProcessedTimestamp")
    }
    
    private fun handleNewDeepLink() {
        Log.d(TAG, "Handling new deep link - showing loader and checking questions")
        
        // Получаем binding для управления UI
        val binding = FallBackScreenBinding.bind(requireView())
        
        // Показываем лоадер
        showLoading(binding)
        
        // Проверяем разрешения и загружаем вопросы
        checkQuestionsAndNavigate()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        // Отписываемся от listener
        if (::sharedPreferences.isInitialized && sharedPreferencesListener != null) {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferencesListener)
        }
    }
}