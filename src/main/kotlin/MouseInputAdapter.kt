import kotlinx.coroutines.flow.MutableStateFlow
import java.awt.event.MouseEvent
import javax.swing.event.MouseInputAdapter


data class MouseState(val mouseEvent: MouseEvent? = null, val firing: Boolean = false)


class MouseInputAdapter(val flow: MutableStateFlow<MouseState>) : MouseInputAdapter() {

    override fun mouseClicked(e: MouseEvent?) {

    }

    override fun mousePressed(e: MouseEvent?) {
        flow.value = flow.value.copy(firing = true)
    }

    override fun mouseReleased(e: MouseEvent?) {
        flow.value = flow.value.copy(firing = false)
    }

    override fun mouseEntered(e: MouseEvent?) {
    }

    override fun mouseExited(e: MouseEvent?) {
    }

    override fun mouseDragged(e: MouseEvent?) {
        flow.value = flow.value.copy(mouseEvent = e!!)
    }

    override fun mouseMoved(e: MouseEvent?) {
        flow.value = flow.value.copy(mouseEvent = e!!)
    }
}