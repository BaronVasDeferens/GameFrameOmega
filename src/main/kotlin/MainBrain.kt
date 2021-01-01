import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.thread

object MainBrain {

    @JvmStatic
    fun main(args: Array<String>) {

        SimpleRender()

    }

    class SimpleRender {

        val targetFps = 60

        private val width = 500
        private val height = 500

        private val gameFrame = GameFrame("Let's try and animations! 2021", width, height)


        init {

            thread {
                val startTime = System.currentTimeMillis()
                val lastRenderTime = AtomicLong(System.currentTimeMillis())
                val totalRenders = AtomicInteger(0)

                while (true) {

                    val elapsedTime = (System.currentTimeMillis() - startTime)
                    totalRenders.incrementAndGet()

                    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
                    val g = image.graphics as Graphics2D
                    g.color = Color.BLACK
                    g.fillRect(0, 0, width, height)
                    g.color = Color.RED
                    g.drawString("ELAPSED: ${elapsedTime/1000} seconds", 50f, 50f)
                    g.drawString("TOTAL FRAMES: ${totalRenders.get()}", 50f, 75f)
                    g.drawString("FPS: ${totalRenders.get().toFloat() / (elapsedTime / 1000).toFloat()}", 50f, 100f)
                    gameFrame.drawImage(image)

                    g.dispose()
                }
            }

            gameFrame.showFrame()

        }


    }

}