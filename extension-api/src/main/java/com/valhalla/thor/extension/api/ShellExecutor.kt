package com.valhalla.thor.extension.api

interface ShellExecutor {
    /**
     * Runs [command] through the host's active privilege gateway and returns
     * `(exitCode, output)`. `suspend` so callers never block the main thread —
     * invoke it from a coroutine (e.g. `rememberCoroutineScope().launch { … }`).
     */
    suspend fun execute(command: String): Pair<Int, String?>
}
