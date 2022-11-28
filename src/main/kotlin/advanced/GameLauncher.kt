package advanced

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowListener
import java.lang.System.exit
import kotlin.system.exitProcess


object GameLauncher {
    @JvmStatic
    fun main(args: Array<String>) {
        val width = 1600
        val height = 1200

        val config = Lwjgl3ApplicationConfiguration()
        config.setResizable(false)
        config.setWindowedMode(width, height)
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
        Lwjgl3Application(Drop(width, height), config)
    }
}
