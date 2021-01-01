import java.awt.Color
import kotlin.random.Random

data class Entity(var x: Int, var y: Int, var color: Color = Color.WHITE) {

    companion object {
        val random = Random(System.currentTimeMillis())
    }

    fun update() {
        x+= random.nextInt(2) * if (random.nextInt(2) == 1) 1 else -1
        y+= random.nextInt(2) * if (random.nextInt(2) == 1) 1 else -1
    }
}