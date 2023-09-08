import java.awt.Graphics2D
import java.awt.image.BufferedImage
import kotlin.math.cos
import kotlin.math.sin

class Projectile(
    val genesisX: Int,
    val genesisY: Int,
    val thetaAngle: Float,
    val speedPerTick: Int,
    val image: BufferedImage
) : Renderable {

    private var x = genesisX
    private var y = genesisY

    private var deltaX = 0
    private var deltaY = 0

    var isValid: Boolean = true

    init {
        deltaX = (cos(thetaAngle.toDouble()) * speedPerTick).toInt()
        deltaY = (sin(thetaAngle.toDouble()) * speedPerTick).toInt()
    }

    override fun update() {
        x += deltaX
        y += deltaY
    }

    /**
     * Updates the projectiles' validity.
     * Returns true if the projectile is still valid, false otherwise
     */
    fun checkValidity(minX: Int, minY: Int, maxX: Int, maxY: Int): Boolean {
        isValid = (x > minX) && (x < maxX) && (y > minY) && (y < maxY)
        return isValid
    }

    override fun setWindowPosition(posX: Int, posY: Int) {

    }

    override fun render(graphics2D: Graphics2D) {
        graphics2D.drawImage(image, x - (image.width / 2), y - (image.width / 2), null)
    }

    override fun render(graphics2D: Graphics2D, offsetX: Int, offsetY: Int) {
        TODO("Not yet implemented")
    }
}