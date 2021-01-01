import kotlinx.coroutines.flow.MutableStateFlow
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

class KeyboardInputAdapter(private val keyState: MutableStateFlow<Set<KeyState>>): KeyListener {

    enum class KeyState {
        PAUSE,
        QUIT
    }

    override fun keyTyped(e: KeyEvent?) {

    }

    override fun keyPressed(e: KeyEvent?) {
        e?.apply {
            when (e.keyCode) {
                KeyEvent.VK_SPACE -> {
                    keyState.value = keyState.value.plus(KeyState.PAUSE)
                }

                KeyEvent.VK_ESCAPE -> {
                    keyState.value = keyState.value.plus(KeyState.QUIT)
                }
            }
        }
    }

    override fun keyReleased(e: KeyEvent?) {

    }
}