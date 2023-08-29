import kotlinx.coroutines.flow.MutableStateFlow
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

class KeyboardInputAdapter(private val keyState: MutableStateFlow<Set<KeyState>>): KeyListener {

    enum class KeyState {
        PAUSE,
        QUIT,
        MOVE_UP,
        MOVE_DOWN,
        MOVE_LEFT,
        MOVE_RIGHT
    }

    override fun keyTyped(e: KeyEvent?) {

    }

    override fun keyPressed(e: KeyEvent?) {
        e?.apply {

            when (e.keyCode) {

                KeyEvent.VK_ESCAPE -> {
                    keyState.value = setOf(KeyState.QUIT)
                }

                KeyEvent.VK_A -> {
                    keyState.value = keyState.value.plus(KeyState.MOVE_LEFT)
                }

                KeyEvent.VK_D -> {
                    keyState.value = keyState.value.plus(KeyState.MOVE_RIGHT)
                }

                KeyEvent.VK_W -> {
                    keyState.value = keyState.value.plus(KeyState.MOVE_UP)
                }

                KeyEvent.VK_S -> {
                    keyState.value = keyState.value.plus(KeyState.MOVE_DOWN)
                }

                else -> {
                    println("Key PRESSED: ${e.keyCode}")
                }
            }
        }
    }

    override fun keyReleased(e: KeyEvent?) {
        e?.apply {
            when (e.keyCode) {

                KeyEvent.VK_SPACE -> {
                    val state = keyState.value
                    if (!state.contains(KeyState.PAUSE)) {
                        keyState.value = keyState.value.plus(KeyState.PAUSE)
                    } else {
                        keyState.value = keyState.value.minus(KeyState.PAUSE)
                    }
                }

                KeyEvent.VK_A -> {
                    keyState.value = keyState.value.minus(KeyState.MOVE_LEFT)
                }

                KeyEvent.VK_D -> {
                    keyState.value = keyState.value.minus(KeyState.MOVE_RIGHT)
                }

                KeyEvent.VK_W -> {
                    keyState.value = keyState.value.minus(KeyState.MOVE_UP)
                }

                KeyEvent.VK_S -> {
                    keyState.value = keyState.value.minus(KeyState.MOVE_DOWN)
                }

                else -> {
                    println("Key RELEASED: ${e.keyChar}")
                }
            }
        }
    }
}