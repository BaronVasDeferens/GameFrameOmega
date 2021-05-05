import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.nio.Buffer
import javax.imageio.ImageIO
import kotlin.random.Random


class Block(
    val blockSize: Int,
    val x: Int,
    val y: Int,
    val image: BufferedImage,
    val impassable: Boolean = true
) : Renderable {
    override fun render(graphics2D: Graphics2D) {
        graphics2D.drawImage(image, x * blockSize, y * blockSize, null)
    }

}

class WorldMap(
    val blockSize: Int,
    val columns: Int,
    val rows: Int,
    val windowWidth: Int = 500,
    val windowHeight: Int = 500,
) {

    private val floorTileImage1: BufferedImage =
        ImageIO.read(javaClass.classLoader.getResourceAsStream("floorGreyTest.png"))
    private val floorTileImage2: BufferedImage =
        ImageIO.read(javaClass.classLoader.getResourceAsStream("floorGreyTest2.png"))

    private lateinit var floorImage: BufferedImage

    var windowX: Int = 0
    var windowY: Int = 0

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
        floorImage = BufferedImage(columns * blockSize, rows * blockSize, BufferedImage.TYPE_INT_ARGB)
        val graphics2d = floorImage.createGraphics()
        floorTiles.flatten().forEach { block ->
            block.render(graphics2d)
        }
        graphics2d.dispose()
    }

    fun moveWindow(hero: Hero) {

        if (hero.isMoving.get()) {
            // crudely define the "movement zone"
            val leftRightZoneSize = windowWidth / 3
            val upDownZoneSize = windowWidth / 3

            // RIGHT SCROLL
            val rightZoneX = windowWidth - leftRightZoneSize
            if (hero.x >= windowX + rightZoneX) {
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
            if (hero.y >= windowY + downZoneX) {
                if (windowY + hero.movementPerUpdate < floorImage.height - windowHeight) {
                    windowY += hero.movementPerUpdate
                    return
                }
            }
        }
    }

    fun render(entities: List<Renderable>, graphics2D: Graphics2D) {

//        println(">>> window location: $windowX, $windowY")
//
//        val hero = entities[0] as Hero
//        println(">>> player at: ${hero.x}, ${hero.y}")

        val floorCopy = BufferedImage(columns * blockSize, rows * blockSize, BufferedImage.TYPE_INT_ARGB)
        val copyGraphics = floorCopy.createGraphics()
        copyGraphics.drawImage(floorImage, 0, 0, null)
        entities.forEach { entity ->
            entity.render(copyGraphics)
        }
        copyGraphics.dispose()
        val window = floorCopy.getSubimage(windowX, windowY, windowWidth, windowHeight)
        graphics2D.drawImage(window, 0, 0, null)
    }


}