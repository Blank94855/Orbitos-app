package com.orbitos

import java.util.Date
import kotlin.random.Random

class CommandEngine {
    
    var username = "root"
    var hostname = "orbit"
    var onOutput: ((String) -> Unit)? = null
    var onClear: (() -> Unit)? = null
    var onThemeChange: ((String) -> Unit)? = null
    
    private val commandHistory = mutableListOf<String>()

    fun init() {
        onOutput?.invoke("Welcome to OrbitOS 4.0 - Beta")
        onOutput?.invoke("Type 'help' for a list of commands")
    }

    fun process(input: String) {
        commandHistory.add(input)
        val parts = input.split(" ")
        val cmd = parts[0].lowercase()
        val args = parts.drop(1)

        when (cmd) {
            "help" -> showHelp(args.firstOrNull())
            "clear" -> onClear?.invoke()
            "echo" -> onOutput?.invoke(args.joinToString(" "))
            "date" -> onOutput?.invoke(Date().toString())
            "whoami" -> onOutput?.invoke("$username@$hostname")
            "history" -> showHistory()
            "setname" -> setName(args)
            "sethost" -> setHost(args)
            "themes" -> handleThemes(args)
            "coinflip" -> flipCoin()
            "calc" -> onOutput?.invoke("Math parsing not implemented in native yet.")
            else -> onOutput?.invoke("Command not found: $cmd. Type 'help'.")
        }
    }

    private fun showHelp(page: String?) {
        val p = page?.toIntOrNull() ?: 1
        val helpText = when (p) {
            1 -> """
                --- [ General ] ---
                help [page]      - Shows this message
                clear            - Clears screen
                echo [text]      - Prints text
                date             - Shows date
                history          - Shows history
                
                --- [ Customization ] ---
                whoami           - Current user
                setname [name]   - Set username
                sethost [name]   - Set hostname
                themes [name]    - Change theme
            """.trimIndent()
            else -> "Page not found."
        }
        onOutput?.invoke(helpText)
    }

    private fun showHistory() {
        commandHistory.forEachIndexed { index, s ->
            onOutput?.invoke("${index + 1}. $s")
        }
    }

    private fun setName(args: List<String>) {
        if (args.isNotEmpty()) {
            username = args[0]
            onOutput?.invoke("Username updated. Restart shell to see prompt change.")
        } else {
            onOutput?.invoke("Usage: setname [name]")
        }
    }

    private fun setHost(args: List<String>) {
        if (args.isNotEmpty()) {
            hostname = args[0]
            onOutput?.invoke("Hostname updated.")
        } else {
            onOutput?.invoke("Usage: sethost [name]")
        }
    }
    
    private fun flipCoin() {
        val res = if (Random.nextBoolean()) "Heads" else "Tails"
        onOutput?.invoke("Coin flip result: $res")
    }
    
    private fun handleThemes(args: List<String>) {
        if (args.isEmpty()) {
            onOutput?.invoke("Available themes: solar-flare, nebula, matrix, cyberpunk, default")
            return
        }
        val theme = args[0]
        onThemeChange?.invoke(theme)
        onOutput?.invoke("Theme set to $theme")
    }
}

