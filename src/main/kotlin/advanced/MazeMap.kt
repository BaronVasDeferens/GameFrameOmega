package advanced

import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

enum class MazeSquareType {
    FLOOR,
    WALL
}

@JsonClass(generateAdapter = true)
data class MazeSquare(
    val row: Int,
    val col: Int,
    val type: MazeSquareType,
    val assetName: String,
    val assetCoordinateX: Int,
    val assetCoordinateY: Int,
    val assetSize: Int
) {

    fun getImage(): BufferedImage {
        val imageFile = File(assetName)
        println(imageFile.absolutePath)
        return ImageIO.read(imageFile)
            .getSubimage(
                assetCoordinateX * assetSize,
                assetCoordinateY * assetSize,
                assetSize,
                assetSize)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is MazeSquare) {
            return false
        }

        return other.row == row
                && other.col == col
                && other.type == type
                && other.assetName == assetName
                && other.assetCoordinateX == assetCoordinateX
                && other.assetCoordinateY == assetCoordinateY
                && other.assetSize == assetSize
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
        //outputFile.writeText(jsonAdapter.toJson(map))
    }

    fun renderMazeToBufferedImage(
        imageWidth: Int,
        imageHeight: Int,
        mazeMap: MazeMap
    ): BufferedImage {

        val image = BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB)
        val graphics = image.graphics as Graphics2D
        mazeMap.mazeSquares.forEach { mazeSquare ->
            graphics.drawImage(
                mazeSquare.getImage(),
                mazeSquare.row * mazeSquare.assetSize,
                mazeSquare.col * mazeSquare.assetSize,
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
    }
}