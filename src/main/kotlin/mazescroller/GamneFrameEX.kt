package mazescroller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.awt.Canvas
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Point
import java.awt.event.KeyListener
import java.awt.image.BufferedImage
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.JFrame
import javax.swing.event.MouseInputAdapter


class GameFrameEX(
    frameTitle: String,
    width: Int,
    height: Int,
    imageState: MutableStateFlow<BufferedImage>,
    scope: CoroutineScope
) {

    private val frame = JFrame()
    private val canvas = Canvas() // TODO: investigate graphicsConfiguration / DoubleBuffer?

    private var backgroundImage: BufferedImage? = null


    private val hideMouseCursor = AtomicBoolean(false)


    init {

//        val env = GraphicsEnvironment.getLocalGraphicsEnvironment()
//        val device = env.defaultScreenDevice

        frame.title = frameTitle

        frame.setSize(width, height)
        frame.preferredSize = Dimension(width, height)

        canvas.size = Dimension(width, height)

        frame.add(canvas)

        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.pack()

        canvas.createBufferStrategy(2)

        if (hideMouseCursor.get()) {
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
        }.launchIn(scope)
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