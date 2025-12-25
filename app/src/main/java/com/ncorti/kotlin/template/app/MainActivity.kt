package com.orbitos

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var terminalOutput: TextView
    private lateinit var inputField: EditText
    private lateinit var scrollView: ScrollView
    private lateinit var promptText: TextView
    private lateinit var lockScreen: View
    private lateinit var terminalContainer: View
    private lateinit var passwordInput: EditText
    private lateinit var unlockBtn: View
    private lateinit var timeText: TextView
    private lateinit var dateText: TextView

    private val commandEngine = CommandEngine()
    private var isLocked = true
    private val correctPass = "admin" 

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        terminalOutput = findViewById(R.id.terminalOutput)
        inputField = findViewById(R.id.inputField)
        scrollView = findViewById(R.id.scrollView)
        promptText = findViewById(R.id.promptText)
        lockScreen = findViewById(R.id.lockScreen)
        terminalContainer = findViewById(R.id.terminalContainer)
        passwordInput = findViewById(R.id.passwordInput)
        unlockBtn = findViewById(R.id.unlockBtn)
        timeText = findViewById(R.id.lockTime)
        dateText = findViewById(R.id.lockDate)

        setupLockScreen()
        setupTerminal()
        
        commandEngine.onOutput = { text ->
            appendToTerminal(text)
        }
        
        commandEngine.onClear = {
            terminalOutput.text = ""
        }
        
        commandEngine.onThemeChange = { themeId ->
            applyTheme(themeId)
        }

        commandEngine.init()
    }

    private fun setupLockScreen() {
        updateTime()
        terminalContainer.visibility = View.GONE
        lockScreen.visibility = View.VISIBLE

        unlockBtn.setOnClickListener {
            if (passwordInput.text.toString() == correctPass) {
                isLocked = false
                lockScreen.animate().alpha(0f).setDuration(500).withEndAction {
                    lockScreen.visibility = View.GONE
                    terminalContainer.visibility = View.VISIBLE
                    terminalContainer.alpha = 0f
                    terminalContainer.animate().alpha(1f).duration = 500
                    inputField.requestFocus()
                }.start()
            } else {
                passwordInput.error = "Incorrect password"
            }
        }
    }

    private fun updateTime() {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        timeText.text = timeFormat.format(Date())
        dateText.text = dateFormat.format(Date())
    }

    private fun setupTerminal() {
        promptText.text = "${commandEngine.username}@${commandEngine.hostname}:~$ "

        inputField.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || 
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val cmd = inputField.text.toString().trim()
                if (cmd.isNotEmpty()) {
                    appendToTerminal("${promptText.text}$cmd")
                    commandEngine.process(cmd)
                    inputField.text.clear()
                }
                true
            } else {
                false
            }
        }
    }

    private fun appendToTerminal(text: String) {
        terminalOutput.append("\n$text")
        scrollView.post {
            scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }
    
    private fun applyTheme(themeId: String) {
        val color = when(themeId) {
            "solar-flare" -> R.color.theme_solar_bg
            "nebula" -> R.color.theme_nebula_bg
            "matrix" -> R.color.theme_matrix_bg
            "cyberpunk" -> R.color.theme_cyberpunk_bg
            else -> R.color.theme_default_bg
        }
        val textColor = when(themeId) {
            "solar-flare" -> R.color.theme_solar_text
            "nebula" -> R.color.theme_nebula_text
            "matrix" -> R.color.theme_matrix_text
            "cyberpunk" -> R.color.theme_cyberpunk_text
            else -> R.color.theme_default_text
        }
        
        val bgColor = ContextCompat.getColor(this, color)
        val txtColor = ContextCompat.getColor(this, textColor)
        
        terminalContainer.setBackgroundColor(bgColor)
        terminalOutput.setTextColor(txtColor)
        inputField.setTextColor(txtColor)
        promptText.setTextColor(ContextCompat.getColor(this, R.color.accent_secondary))
    }
}

