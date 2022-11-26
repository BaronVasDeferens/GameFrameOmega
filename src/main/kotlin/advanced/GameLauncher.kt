package advanced

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration


object GameLauncher {
    @JvmStatic
    fun main(args: Array<String>) {
        val width = 1600
        val height = 1200

        val config = Lwjgl3ApplicationConfiguration()
        config.setResizable(false)
        config.setWindowedMode(width, height)
        Lwjgl3Application(Drop(width, height), config)
    }
}
