import kotlinx.coroutines.flow.MutableStateFlow
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.thread

object MainBrain {

    @JvmStatic
    fun main(args: Array<String>) {
        val width = 500
        val height = 500


        SimpleRender(width, height)

    }

    class SimpleRender(
        private val width: Int = 500,
        private val height: Int = 500,
    ) {

        val imageState = MutableStateFlow(BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB))

        val targetFps = 60
        private val gameFrame = GameFrame("Let's try and animations! 2021", width, height, imageState)


        init {

            thread {
                val now = AtomicLong(System.currentTimeMillis())
                val startTime = System.currentTimeMillis()
                val elapsedTime = AtomicLong(0)
                val lastRenderTime = AtomicLong(System.currentTimeMillis())
                val totalRenders = AtomicInteger(0)

                // TODO: adjust this value based on performance data
                var waitBetweenRenders = 16L // Crude approx of 1000/60 = 16.667
                var currentFps = 0.0f

                while (true) {

                    now.set(System.currentTimeMillis())

                    elapsedTime.set(System.currentTimeMillis() - startTime)
                    totalRenders.incrementAndGet()
                    currentFps = totalRenders.get().toFloat() / (elapsedTime.get() / 1000).toFloat()


                    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
                    val g = image.graphics as Graphics2D
                    g.color = Color.BLACK
                    g.fillRect(0, 0, width, height)
                    g.color = Color.RED
                    g.drawString("ELAPSED: ${elapsedTime.get() / 1000} seconds", 50f, 50f)
                    g.drawString("TOTAL FRAMES: ${totalRenders.get()}", 50f, 75f)
                    g.drawString("FPS: ${currentFps}", 50f, 100f)
                    g.dispose()
                    lastRenderTime.set(System.currentTimeMillis())
                    imageState.value = image
                    Thread.sleep(waitBetweenRenders)
                }
            }

            gameFrame.showFrame()

        }


    }

}