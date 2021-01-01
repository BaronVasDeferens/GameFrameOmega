import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.awt.Canvas
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.GraphicsEnvironment
import java.awt.event.KeyListener
import java.awt.image.BufferedImage
import java.nio.Buffer
import java.util.concurrent.Executors
import javax.swing.JFrame
import javax.swing.event.MouseInputAdapter
import kotlin.coroutines.CoroutineContext


class GameFrame(
    private val frameTitle: String = "TITLE OF GAME",
    private val width: Int = 1000,
    private val height: Int = 1000,
    private val imageState: MutableStateFlow<BufferedImage>
) {

    private val frame = JFrame()
    private val canvas = Canvas() // TODO: investigate graphicsConfiguration / DoubleBuffer?

    private var backgroundImage: BufferedImage? = null


    init {

        val env = GraphicsEnvironment.getLocalGraphicsEnvironment()
        val device = env.defaultScreenDevice
        println(">>> refresh rate: ${device.displayMode.refreshRate}")

        frame.title = frameTitle

        frame.setSize(width, height)
        frame.preferredSize = Dimension(width, height)

        canvas.size = Dimension(width, height)

        frame.add(canvas)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.pack()

        canvas.requestFocus()

        imageState.onEach { image ->
            drawImage(image)
        }.launchIn(GlobalScope)


    }

    fun setKeyListener(listener: KeyListener) {
        canvas.addKeyListener(listener)
    }

    fun setMouseAdapter(clickListener: MouseInputAdapter) {
        canvas.addMouseMotionListener(clickListener)
        canvas.addMouseListener(clickListener)
    }

    fun showFrame() {
        frame.isVisible = true
    }

    fun drawImage(image: BufferedImage, x: Int = 0, y: Int = 0) {
        val graphics = canvas.graphics as Graphics2D
        backgroundImage?.let {
            graphics.drawImage(backgroundImage, 0, 0, null)
        }

        graphics.drawImage(image, x, y, null)
        graphics.dispose()
    }

}