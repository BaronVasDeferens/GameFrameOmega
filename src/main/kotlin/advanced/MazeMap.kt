package advanced

import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

enum class MazeSquareType() {
    FLOOR,
    WALL
}

@JsonClass(generateAdapter = true)
data class MazeSquare(
    val row: Int,
    val col: Int,
    val type: MazeSquareType
) {
    override fun equals(other: Any?): Boolean {
        if (other !is MazeSquare) {
            return false
        }

        return other.row == row
                && other.col == col
                && other.type == type
    }
}

@JsonClass(generateAdapter = true)
class MazeMap(
    val name: String,
    val rows: Int,
    val cols: Int,
    val mazeSquares: List<MazeSquare>
) {

    override fun equals(other: Any?): Boolean {
        if (other !is MazeMap) {
            return false
        }

        return other.name == name &&
                other.rows == rows
                && other.cols == cols
                && other.mazeSquares.sortedBy { it.row } == mazeSquares
    }
}

@OptIn(ExperimentalStdlibApi::class)
object MazeMapUtility {

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val jsonAdapter: JsonAdapter<MazeMap> = moshi.adapter()

    const val tileSize = 32
    private val allTilesImage = ImageIO.read(File("src\\main\\resources\\fantasy-tileset.png"))



    fun loadMapFromFile(fileName: String): MazeMap {
        val mapFile = File(fileName)
        println(mapFile.absolutePath)
        return jsonAdapter.fromJson(mapFile.readText())!!
    }

    fun writeMapToFile(mazeMap: MazeMap, fileName: String) {
        val outputFile = File(fileName)
        if (!outputFile.exists()) {
            outputFile.createNewFile()
        }

        println((jsonAdapter.toJson(mazeMap)))
        outputFile.writeText(jsonAdapter.toJson(mazeMap))
    }

    fun loadImageTile(type: MazeSquareType): BufferedImage {

        return when(type) {

            MazeSquareType.FLOOR -> {
                allTilesImage.getSubimage(
                    0 * tileSize,
                    1 * tileSize,
                    tileSize,
                    tileSize)
            }

            MazeSquareType.WALL -> {
                allTilesImage.getSubimage(
                    2 * tileSize,
                    2 * tileSize,
                    tileSize,
                    tileSize)
            }
        }
    }

    fun renderMazeToBufferedImage(mazeMap: MazeMap): BufferedImage {
        val imageWidth = mazeMap.cols * tileSize
        val imageHeight = mazeMap.rows * tileSize
        val image = BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB)
        val graphics = image.graphics as Graphics2D
        mazeMap.mazeSquares.forEach { mazeSquare ->
            graphics.drawImage(
                loadImageTile(mazeSquare.type),
                mazeSquare.row * tileSize,
                mazeSquare.col * tileSize,
                null
            )
        }

        graphics.dispose()
        return image
    }

    fun saveImageToDisk(image: BufferedImage, fileName: String) {
        val outputFile = File(fileName)
        if (!outputFile.exists()) {
            outputFile.createNewFile()
        }
        ImageIO.write(image, "png", outputFile)
        println("Saved ${outputFile.absolutePath}")
    }
}