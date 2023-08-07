package advanced

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowListener
import kotlin.system.exitProcess


object GameLauncher {
    @JvmStatic
    fun main(args: Array<String>) {
        val windowWidth = 800
        val windowHeight = 800

        val config = Lwjgl3ApplicationConfiguration()
        config.setResizable(false)
        config.setWindowedMode(windowWidth, windowHeight)
        config.setWindowListener(object : Lwjgl3WindowListener {
            override fun created(window: Lwjgl3Window?) {
            }

            override fun iconified(isIconified: Boolean) {
            }

            override fun maximized(isMaximized: Boolean) {
            }

            override fun focusLost() {
            }

            override fun focusGained() {
            }

            override fun closeRequested(): Boolean {
                exitProcess(0)
            }

            override fun filesDropped(files: Array<out String>?) {
            }

            override fun refreshRequested() {
            }

        })
        Lwjgl3Application(Drop(windowWidth, windowHeight), config)
    }
}
