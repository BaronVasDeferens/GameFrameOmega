import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration


object GameLauncher {
    @JvmStatic
    fun main(args: Array<String>) {
        val width = 800
        val height = 600

        val config = Lwjgl3ApplicationConfiguration()
        config.setResizable(false)
        config.setWindowedMode(800, 600)
        Lwjgl3Application(Drop(width, height), config)
    }
}
