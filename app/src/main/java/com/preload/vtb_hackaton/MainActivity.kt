package com.preload.vtb_hackaton

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.preload.vtb_hackaton.data.repository.InterviewRepository
import com.preload.vtb_hackaton.feature.fallback.FallbackFragment
import com.preload.vtb_hackaton.feature.interview.InterviewFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }
    
    private val interviewRepository = InterviewRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clearAllSharedPreferences()
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        Log.d(TAG, "MainActivity onCreate called")
        Log.d(TAG, "Intent: $intent")
        Log.d(TAG, "Intent action: ${intent.action}")
        Log.d(TAG, "Intent data: ${intent.data}")

        // Обработка deep link при запуске приложения
        val interviewId = handleDeepLink(intent)
        
        // Проверяем буфер обмена на наличие deep link (для Telegram)
        val clipboardId = checkClipboardForDeepLink()
        
        // Определяем ID для запроса к API
        val finalInterviewId = interviewId ?: clipboardId
        
        // Сохраняем ID и переходим к FallbackFragment
        if (finalInterviewId != null) {
            Log.d(TAG, "Saving interview ID: $finalInterviewId")
            val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            sharedPref.edit()
                .putString("deep_link_id", finalInterviewId)
                .putLong("deep_link_timestamp", System.currentTimeMillis())
                .apply()
            
            Log.d(TAG, "Deep link saved in SharedPreferences, timestamp: ${System.currentTimeMillis()}")
        }
        
        // FallbackFragment сам решит, куда переходить
        Log.d(TAG, "Deep link processed, FallbackFragment will handle navigation")
        Log.d(TAG, "MainActivity onCreate completed successfully")
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent called with: $intent")
        
        // Обновляем intent для корректной обработки
        setIntent(intent)
        
        // Обрабатываем диплинк
        val interviewId = handleDeepLink(intent)
        
        // Проверяем буфер обмена на наличие deep link (для Telegram)
        val clipboardId = checkClipboardForDeepLink()
        
        // Определяем ID для запроса к API
        val finalInterviewId = interviewId ?: clipboardId
        
        // Сохраняем ID и уведомляем фрагменты об обновлении
        if (finalInterviewId != null) {
            Log.d(TAG, "Saving new interview ID from onNewIntent: $finalInterviewId")
            val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            sharedPref.edit()
                .putString("deep_link_id", finalInterviewId)
                .putLong("deep_link_timestamp", System.currentTimeMillis())
                .apply()
            
            Log.d(TAG, "Deep link updated in SharedPreferences, timestamp: ${System.currentTimeMillis()}")
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "MainActivity onDestroy - clearing all SharedPreferences")
        clearAllSharedPreferences()
    }
    
    private fun clearAllSharedPreferences() {
        try {
            val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            sharedPref.edit().clear().apply()
            Log.d(TAG, "All SharedPreferences cleared successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing SharedPreferences: ${e.message}")
        }
    }

    private fun handleDeepLink(intent: Intent): String? {
        val action = intent.action
        val data: Uri? = intent.data

        Log.d(TAG, "handleDeepLink called with action: $action, data: $data")

        if (data != null) {
            Log.d(TAG, "Deep link received: $data")
            Log.d(TAG, "Scheme: ${data.scheme}, Host: ${data.host}, Path: ${data.path}")
            Log.d(TAG, "Query: ${data.query}")

            // Проверяем все возможные форматы ссылок
            val id = when {
                // Приоритет 1: Query параметр id
                data.getQueryParameter("id") != null -> {
                    Log.d(TAG, "Found ID in query parameter: ${data.getQueryParameter("id")}")
                    data.getQueryParameter("id")
                }
                // Приоритет 2: Путь /interview/123
                data.host == "vtbhackaton.app" && data.pathSegments.size >= 2 && data.pathSegments[0] == "interview" -> {
                    Log.d(TAG, "Found ID in path: ${data.pathSegments[1]}")
                    data.pathSegments[1]
                }
                // Приоритет 3: Custom scheme vtbhackaton://interview/123
                data.scheme == "vtbhackaton" && data.pathSegments.size >= 2 && data.pathSegments[0] == "interview" -> {
                    Log.d(TAG, "Found ID in custom scheme: ${data.pathSegments[1]}")
                    data.pathSegments[1]
                }
                // Приоритет 4: Последний сегмент пути
                data.lastPathSegment != null -> {
                    Log.d(TAG, "Found ID as last path segment: ${data.lastPathSegment}")
                    data.lastPathSegment
                }
                else -> null
            }
            
            if (id != null) {
                Log.d(TAG, "Extracted ID: $id")
                Toast.makeText(this, "Deep Link ID: $id", Toast.LENGTH_LONG).show()
                
                // Сохраняем ID для использования в фрагментах
                val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
                sharedPref.edit()
                    .putString("deep_link_id", id)
                    .putLong("deep_link_timestamp", System.currentTimeMillis())
                    .apply()
                
                return id
            } else {
                Log.w(TAG, "No ID found in deep link")
                Toast.makeText(this, "No ID found in link: $data", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.d(TAG, "No data in intent")
        }
        
        return null
    }

    private fun extractIdFromUri(uri: Uri): String? {
        return when {
            // Для URL с query параметром: https://vtbhackaton.app?id=123
            uri.getQueryParameter("id") != null -> {
                uri.getQueryParameter("id")
            }
            // Для URL вида: https://vtbhackaton.app/interview/123
            uri.host == "vtbhackaton.app" -> {
                val pathSegments = uri.pathSegments
                if (pathSegments.size >= 2 && pathSegments[0] == "interview") {
                    pathSegments[1]
                } else {
                    // Если нет /interview/, берем последний сегмент пути
                    uri.lastPathSegment
                }
            }
            // Для custom scheme: vtbhackaton://interview/123
            uri.scheme == "vtbhackaton" -> {
                val pathSegments = uri.pathSegments
                if (pathSegments.size >= 2 && pathSegments[0] == "interview") {
                    pathSegments[1]
                } else {
                    // Если нет /interview/, берем последний сегмент пути
                    uri.lastPathSegment
                }
            }
            else -> null
        }
    }

    private fun checkClipboardForDeepLink(): String? {
        try {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.primaryClip?.let { clip ->
                if (clip.itemCount > 0) {
                    val text = clip.getItemAt(0).text.toString()
                    Log.d(TAG, "Clipboard content: $text")
                    
                    // Проверяем различные форматы ссылок
                    if (text.contains("vtbhackaton.app") || 
                        text.contains("vtbhackaton://") ||
                        text.contains("vtbhackaton") ||
                        text.contains("interview")) {
                        
                        Log.d(TAG, "Found potential deep link in clipboard: $text")
                        
                        // Пытаемся извлечь ID из текста
                        val id = extractIdFromText(text)
                        if (id != null) {
                            Log.d(TAG, "Extracted ID from clipboard: $id")
                            Toast.makeText(this, "Found ID in clipboard: $id", Toast.LENGTH_LONG).show()
                            
                            // Сохраняем ID
                            val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
                            sharedPref.edit()
                                .putString("deep_link_id", id)
                                .putLong("deep_link_timestamp", System.currentTimeMillis())
                                .apply()
                            
                            return id
                        }
                        
                        // Пытаемся обработать как URI
                        try {
                            val uri = Uri.parse(text)
                            if (uri != null) {
                                return handleDeepLink(Intent(Intent.ACTION_VIEW, uri))
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing clipboard text as URI: ${e.message}")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking clipboard: ${e.message}")
        }
        
        return null
    }
    
    private fun extractIdFromText(text: String): String? {
        // Ищем ID в различных форматах
        val patterns = listOf(
            "id=(\\d+)",           // ?id=123
            "/interview/(\\d+)",   // /interview/123
            "vtbhackaton://interview/(\\d+)", // vtbhackaton://interview/123
            "interview/(\\d+)"     // interview/123
        )
        
        for (pattern in patterns) {
            val regex = Regex(pattern)
            val match = regex.find(text)
            if (match != null) {
                return match.groupValues[1]
            }
        }
        
        return null
    }
    
    // УДАЛЕНО: больше не используется, FallbackFragment сам решает навигацию
    /*
    private fun loadQuestionsAndNavigate(interviewId: String?) {
        Log.d(TAG, "loadQuestionsAndNavigate called with interviewId: $interviewId")
        CoroutineScope(Dispatchers.Main).launch {
            try {
                if (interviewId != null) {
                    Log.d(TAG, "Loading questions for interview ID: $interviewId")
                    
                    // Пытаемся преобразовать ID в число
                    val idInt = interviewId.toIntOrNull()
                    if (idInt != null) {
                        Log.d(TAG, "Making API call with ID: $idInt")
                        val result = interviewRepository.getQuestions(idInt)
                        
                        if (result.isSuccess) {
                            Log.d(TAG, "Questions loaded successfully: ${result.getOrNull()?.questions?.size} questions")
                            Toast.makeText(this@MainActivity, "Questions loaded successfully", Toast.LENGTH_SHORT).show()
                            
                            // Переходим на InterviewFragment
                            Log.d(TAG, "Navigating to InterviewFragment")
                            navigateToInterviewFragment(interviewId)
                        } else {
                            Log.e(TAG, "Failed to load questions: ${result.exceptionOrNull()?.message}")
                            Toast.makeText(this@MainActivity, "Failed to load questions", Toast.LENGTH_SHORT).show()
                            
                            // Переходим на FallbackFragment
                            navigateToFallbackFragment()
                        }
                    } else {
                        Log.e(TAG, "Invalid interview ID format: $interviewId")
                        Toast.makeText(this@MainActivity, "Invalid interview ID", Toast.LENGTH_SHORT).show()
                        
                        // Переходим на FallbackFragment
                        navigateToFallbackFragment()
                    }
                } else {
                    Log.d(TAG, "No interview ID found, navigating to fallback")
                    
                    // Переходим на FallbackFragment
                    navigateToFallbackFragment()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in loadQuestionsAndNavigate: ${e.message}", e)
                Toast.makeText(this@MainActivity, "Error loading questions", Toast.LENGTH_SHORT).show()
                
                // Переходим на FallbackFragment
                navigateToFallbackFragment()
            }
        }
    }
    
    private fun navigateToInterviewFragment(interviewId: String) {
        try {
            Log.d(TAG, "navigateToInterviewFragment called with ID: $interviewId")
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container) as? NavHostFragment
            if (navHostFragment != null) {
                Log.d(TAG, "NavHostFragment found, navigating...")
                navHostFragment.navController.navigate(
                    R.id.action_fallbackFragment_to_chooseModeFragment,
                    Bundle().apply {
                        putString("interview_id", interviewId)
                    }
                )
                Log.d(TAG, "Navigation completed")
            } else {
                Log.e(TAG, "NavHostFragment not found")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to InterviewFragment: ${e.message}", e)
            // Fallback: просто показываем FallbackFragment
            navigateToFallbackFragment()
        }
    }
    
    private fun navigateToFallbackFragment() {
        try {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container) as? NavHostFragment
            // FallbackFragment уже является стартовым фрагментом, поэтому просто остаемся на нем
            Log.d(TAG, "Staying on FallbackFragment")
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to FallbackFragment: ${e.message}")
        }
    }
    */
}