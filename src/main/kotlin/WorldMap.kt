import java.awt.Graphics2D
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
    ) : Renderable {

    private val floorTileImage1: BufferedImage = ImageIO.read(javaClass.classLoader.getResourceAsStream("floorGreyTest.png"))
    private val floorTileImage2: BufferedImage = ImageIO.read(javaClass.classLoader.getResourceAsStream("floorGreyTest2.png"))

    private lateinit var floorImage: BufferedImage

    var windowX: Int = 0
    var windowY: Int = 0

    private val floorTiles: Array<Array<Block>> = Array(rows) { rowNum ->
        Array(columns) { colNum ->

            val tile = if(Random.nextBoolean()) {
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
        // crudely define the "movement zone"
        val rightZoneX = windowWidth - (windowWidth / 5)

        if (hero.x >= rightZoneX && hero.isMoving.get()) {
            windowX += hero.movementPerUpdate
        }
    }

    override fun render(graphics2D: Graphics2D) {
        val window = floorImage.getSubimage(windowX, windowY, windowWidth, windowHeight)
        graphics2D.drawImage(window, 0, 0, null)
    }


}