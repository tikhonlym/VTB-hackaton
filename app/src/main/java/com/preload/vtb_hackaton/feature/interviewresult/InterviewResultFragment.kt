package com.preload.vtb_hackaton.feature.interviewresult

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.preload.vtb_hackaton.R
import com.preload.vtb_hackaton.databinding.InterviewResultScreenBinding

class InterviewResultFragment : Fragment(R.layout.interview_result_screen) {

    companion object {
        private const val TAG = "InterviewResultFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = InterviewResultScreenBinding.bind(view)

        // Отключаем кнопку "Назад" - это финальный экран
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Ничего не делаем - блокируем возврат назад
                Log.d(TAG, "Back button pressed - blocked (final screen)")
            }
        })

        Log.d(TAG, "InterviewResultFragment displayed - interview completed")
        
        // Здесь можно добавить логику для показа результатов интервью
        // Например, отображение статистики, оценок и т.д.
    }
}