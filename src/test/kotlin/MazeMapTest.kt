import advanced.MazeMap
import advanced.MazeMapUtility
import advanced.MazeSquare
import advanced.MazeSquareType
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

class MazeMapTest {

    @Test
    fun `should save and load maps`() {
        val mazeMap = MazeMap(
            name = "Test 1 Maze",
            rows = 2,
            cols = 2,
            mazeSquares = listOf(
                MazeSquare(
                    row = 0,
                    col = 0,
                    type = MazeSquareType.WALL,
                    assetName = "src\\main\\resources\\fantasy-tileset.png",
                    assetCoordinateX = 3,
                    assetCoordinateY = 2,
                    assetSize = 32
                ),
                MazeSquare(
                    row = 0,
                    col = 1,
                    type = MazeSquareType.WALL,
                    assetName = "src\\main\\resources\\fantasy-tileset.png",
                    assetCoordinateX = 3,
                    assetCoordinateY = 2,
                    assetSize = 32
                ),
                MazeSquare(
                    row = 1,
                    col = 0,
                    type = MazeSquareType.WALL,
                    assetName = "src\\main\\resources\\fantasy-tileset.png",
                    assetCoordinateX = 3,
                    assetCoordinateY = 2,
                    assetSize = 32
                ),
                MazeSquare(
                    row = 1,
                    col = 1,
                    type = MazeSquareType.WALL,
                    assetName = "src\\main\\resources\\fantasy-tileset.png",
                    assetCoordinateX = 3,
                    assetCoordinateY = 2,
                    assetSize = 32
                )
            )
        )

        val fromDisk = MazeMapUtility.loadMapFromFile("src\\test\\resources\\maze_1.json")
        assertEquals(mazeMap, fromDisk)
    }

    @Test
    fun `should save image to disk`() {
        val mazeMap = MazeMapUtility.loadMapFromFile("src\\test\\resources\\maze_1.json")
        val image = MazeMapUtility.renderMazeToBufferedImage(64, 64, mazeMap)
        MazeMapUtility.saveImageToDisk(image, "maze.png")
    }

}