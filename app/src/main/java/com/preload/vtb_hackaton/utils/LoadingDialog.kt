package com.preload.vtb_hackaton.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import com.preload.vtb_hackaton.R

class LoadingDialog(private val context: Context) {
    
    private var dialog: Dialog? = null
    private var loadingText: TextView? = null
    private var loadingSubtitle: TextView? = null
    private var progressIndicator: ImageView? = null
    
    fun show(message: String = "Загрузка...", subtitle: String = "Пожалуйста, подождите") {
        if (dialog?.isShowing == true) {
            return
        }
        
        dialog = Dialog(context)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.loading_dialog)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        
        // Инициализируем элементы
        loadingText = dialog?.findViewById(R.id.loading_text)
        loadingSubtitle = dialog?.findViewById(R.id.loading_subtitle)
        progressIndicator = dialog?.findViewById(R.id.loading_progress)
        
        // Устанавливаем текст
        loadingText?.text = message
        loadingSubtitle?.text = subtitle
        
        // Запускаем анимацию
        startAnimation()
        
        dialog?.show()
    }
    
    fun updateMessage(message: String, subtitle: String? = null) {
        loadingText?.text = message
        if (subtitle != null) {
            loadingSubtitle?.text = subtitle
        }
    }
    
    fun hide() {
        dialog?.dismiss()
        dialog = null
    }
    
    fun isShowing(): Boolean {
        return dialog?.isShowing == true
    }
    
    private fun startAnimation() {
        // Анимация уже настроена в XML drawable
        // Можно добавить дополнительную анимацию здесь если нужно
    }
    
    companion object {
        fun show(context: Context, message: String = "Загрузка...", subtitle: String = "Пожалуйста, подождите"): LoadingDialog {
            val loadingDialog = LoadingDialog(context)
            loadingDialog.show(message, subtitle)
            return loadingDialog
        }
    }
}
