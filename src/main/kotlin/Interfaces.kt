import java.awt.Graphics2D

interface Renderable {
    fun update()
    fun render(graphics2D: Graphics2D)
}