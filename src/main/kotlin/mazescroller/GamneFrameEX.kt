package mazescroller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
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
    private val frameTitle: String,
    private val width: Int,
    private val height: Int,
    private val bufferedImageFlow: MutableStateFlow<BufferedImage>,
    private val scope: CoroutineScope
) {

    private val frame = JFrame()
    private val canvas = Canvas() // TODO: investigate graphicsConfiguration / DoubleBuffer?

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

        bufferedImageFlow.onEach { image ->
            drawImage(image)
        }.onCompletion {
            println("game frame rendering job terminated")
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

    fun drawImage(image: BufferedImage) {
        with(canvas.graphics as Graphics2D) {
            drawImage(image, 0, 0, null)
            dispose()
        }
    }
}