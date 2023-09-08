import java.awt.Graphics2D

interface Renderable {
    fun update()
    fun setWindowPosition(posX: Int, posY: Int)
    fun render(graphics2D: Graphics2D)

    fun render(graphics2D: Graphics2D, offsetX: Int, offsetY: Int)
}