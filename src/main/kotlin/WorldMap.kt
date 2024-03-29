import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import kotlin.random.Random


class Block(
    private val blockSize: Int,
    private val x: Int,
    private val y: Int,
    private val image: BufferedImage,
    val impassable: Boolean = true
) : Renderable {
    override fun update() {

    }

    override fun setWindowPosition(posX: Int, posY: Int) {

    }

    override fun render(graphics2D: Graphics2D) {
        graphics2D.drawImage(image, x * blockSize, y * blockSize, null)
    }

}

class WorldMap(
    private val blockSize: Int,
    private val columns: Int,
    private val rows: Int,
    val windowWidth: Int,
    val windowHeight: Int,
) {

    private val floorTileImage1: BufferedImage =
        ImageIO.read(javaClass.classLoader.getResourceAsStream("floor_grey_basic.png"))
    private val floorTileImage2: BufferedImage =
        ImageIO.read(javaClass.classLoader.getResourceAsStream("floor_grey_center_square.png"))

    private var floorImage: BufferedImage = BufferedImage(columns * blockSize, rows * blockSize, BufferedImage.TYPE_INT_ARGB)

    var windowX: Int = 0
    var windowY: Int = 0

    // crudely define the "movement zone"
    private val leftRightZoneSize = windowWidth / 3
    private val upDownZoneSize = windowWidth / 3

    private val drawDebugArtifacts = false

    private val floorTiles: Array<Array<Block>> = Array(rows) { rowNum ->
        Array(columns) { colNum ->

            val tile = if (Random.nextBoolean()) {
                floorTileImage1
            } else {
                floorTileImage2
            }

            Block(blockSize, rowNum, colNum, tile, false)
        }
    }

    init {
        val graphics2d = floorImage.createGraphics()
        floorTiles.flatten().forEach { block ->
            block.render(graphics2d)
        }
        graphics2d.dispose()
    }

    fun moveWindow(hero: Player) {

        if (hero.isMoving.get() || hero.isCoasting.get()) {

            // RIGHT SCROLL
            val rightZoneX = windowWidth - leftRightZoneSize
            if (hero.x + hero.spriteSize >= windowX + rightZoneX) {
                if (windowX + hero.movementPerUpdate < floorImage.width - windowWidth) {
                    windowX += hero.movementPerUpdate
                    return
                }
            }

            // LEFT SCROLL
            if (hero.x > windowX && hero.x < windowX + leftRightZoneSize) {
                if (windowX - hero.movementPerUpdate > 0) {
                    windowX -= hero.movementPerUpdate
                    return
                }
            }

            // UP SCROLL
            if (hero.y > windowY && hero.y < windowY + upDownZoneSize) {
                if (windowY - hero.movementPerUpdate > 0) {
                    windowY -= hero.movementPerUpdate
                    return
                }
            }

            // DOWN SCROLL
            val downZoneX = windowHeight - upDownZoneSize
            if (hero.y + hero.spriteSize >= windowY + downZoneX) {
                if (windowY + hero.movementPerUpdate < floorImage.height - windowHeight) {
                    windowY += hero.movementPerUpdate
                    return
                }
            }
        }

        // Update Tank's internal notion of its position within the window
        val posX = if (hero.x > windowX) {
            hero.x - windowX
        } else {
            hero.x
        }

        val posY = if(hero.y > windowY) {
            hero.y - windowY
        } else {
            hero.y
        }

        hero.setWindowPosition(posX, posY)
    }

    fun render(entities: List<Renderable>, graphics2D: Graphics2D) {

        val floorCopy = BufferedImage(columns * blockSize, rows * blockSize, BufferedImage.TYPE_INT_ARGB)
        val copyGraphics = floorCopy.createGraphics()
        copyGraphics.drawImage(floorImage, 0, 0, null)
        entities.forEach { entity ->
            entity.render(copyGraphics)
        }

        if (drawDebugArtifacts) {
            // Draw window movement zones
            copyGraphics.color = Color.RED
            copyGraphics.drawRect(windowX, windowY, leftRightZoneSize, windowHeight) // left
            copyGraphics.drawRect(windowX + (2 * leftRightZoneSize), windowY, leftRightZoneSize, windowHeight) // right

            copyGraphics.color = Color.BLUE
            copyGraphics.drawRect(windowX + upDownZoneSize, windowY, upDownZoneSize, upDownZoneSize) // up
            copyGraphics.drawRect(
                windowX + upDownZoneSize,
                windowY + (2 * upDownZoneSize),
                upDownZoneSize,
                upDownZoneSize
            ) // down
        }

        copyGraphics.dispose()
        val window = floorCopy.getSubimage(windowX, windowY, windowWidth, windowHeight)
        graphics2D.drawImage(window, 0, 0, null)
    }


}