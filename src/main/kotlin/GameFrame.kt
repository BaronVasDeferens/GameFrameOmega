import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.awt.*
import java.awt.event.KeyListener
import java.awt.image.BufferedImage
import javax.swing.JFrame
import javax.swing.event.MouseInputAdapter


class GameFrame(
    frameTitle: String,
    width: Int,
    height: Int,
    imageState: MutableStateFlow<BufferedImage>
) {

    private val frame = JFrame()
    private val canvas = Canvas() // TODO: investigate graphicsConfiguration / DoubleBuffer?

    private var backgroundImage: BufferedImage? = null


    private val hideMouseCursor = true

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

        canvas.createBufferStrategy(2)

        if (hideMouseCursor) {
            frame.cursor = frame.toolkit.createCustomCursor(
                BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
                Point(),
                null
            )
        }

        frame.setLocation(750, 250)
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