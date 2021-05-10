import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.concurrent.atomic.AtomicInteger
import javax.imageio.ImageIO

class RobotDrone(var x: Int,
                 var y: Int,
                 val spriteSize: Int = 64,
                 val spriteFileName: String = "robot_basic_sprite_sheet.png"): Renderable {

    private var spriteSheet: BufferedImage = ImageIO.read(javaClass.classLoader.getResourceAsStream(spriteFileName))

    private val currentSpriteIndex = AtomicInteger(0)
    private val ticksPerUpdate: Int = 30
    private val currentTick = AtomicInteger(0)

    fun update() {
        val tick = currentTick.incrementAndGet()
        if (tick >= ticksPerUpdate) {
            currentTick.set(0)
            currentSpriteIndex.set((currentSpriteIndex.get() + 1) % 4)
        }
    }

    override fun render(graphics2D: Graphics2D) {
        val subImage = spriteSheet.getSubimage(spriteSize * currentSpriteIndex.get(), 0, spriteSize, spriteSize)
        graphics2D.drawImage(subImage, x, y, null)
    }

}

class CpuClock(val x: Int, val y: Int, spriteFileName: String = "processor.png"): Renderable{

    private var spriteSheet: BufferedImage = ImageIO.read(javaClass.classLoader.getResourceAsStream(spriteFileName))

    override fun render(graphics2D: Graphics2D) {
        graphics2D.drawImage(spriteSheet, x, y, null)
    }

}