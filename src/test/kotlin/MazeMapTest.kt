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
            rows = 3,
            cols = 3,
            mazeSquares = listOf(
                MazeSquare(
                    row = 0,
                    col = 0,
                    type = MazeSquareType.WALL,
                ),
                MazeSquare(
                    row = 0,
                    col = 1,
                    type = MazeSquareType.WALL,
                ),
                MazeSquare(
                    row = 0,
                    col = 2,
                    type = MazeSquareType.WALL,
                ),
                MazeSquare(
                    row = 1,
                    col = 0,
                    type = MazeSquareType.WALL,
                ),
                MazeSquare(
                    row = 1,
                    col = 1,
                    type = MazeSquareType.FLOOR,
                ),
                MazeSquare(
                    row = 1,
                    col = 2,
                    type = MazeSquareType.WALL,
                ),
                MazeSquare(
                    row = 2,
                    col = 0,
                    type = MazeSquareType.WALL,
                ),
                MazeSquare(
                    row = 2,
                    col = 1,
                    type = MazeSquareType.WALL,
                ),
                MazeSquare(
                    row = 2,
                    col = 2,
                    type = MazeSquareType.WALL,
                ),
            )
        )

        val fromDisk = MazeMapUtility.loadMapFromFile("src\\test\\resources\\maze_1.json")
        assertEquals(mazeMap, fromDisk)
    }

    @Test
    fun `should save image to disk`() {
        val mazeMap = MazeMapUtility.loadMapFromFile("src\\test\\resources\\maze_1.json")
        val image = MazeMapUtility.renderMazeToBufferedImage(mazeMap)
        MazeMapUtility.saveImageToDisk(image, "maze.png")
    }

}